import { ajax, PaginationUI } from '/js/community/common.js';
import { formatRelativeTime } from '/js/community/bbs/csr/dateUtils.js';
let pagination = null;
// community_detail.js (또는 적당한 JS 파일)
document.addEventListener('DOMContentLoaded', async () => {

    let currentPage = 1;
    let initialPage = 1;
    const recordsPerPage = 10;
    const pagesPerPage   = 5;
    let categoryMap = {};
    const span = document.querySelector('.contentCategory span');
    const board = document.querySelector(".board");
    const pid = board.id;
    const Quill = window.Quill;
    const frm      = document.getElementById('edit-form');

    // ---------------- Quill 에디터 초기화 ------------------------------------
    const quill = new Quill('#editor', {
      theme: 'snow',
      modules: {
        toolbar: '#toolbar',
        imageResize: {}
      }
    });

    // 카테고리 매핑 로드 (페이지 최초 1회만 호출)
    const loadCategories = async () => {
      try {
        const res = await ajax.get('/api/bbs/categories');
        if (res.header.rtcd === 'S00') {
          categoryMap = Object.fromEntries(
            res.body.map(({ codeId, decode }) => [codeId, decode])
          );
            const bbsData = span.textContent.trim();
            const decode = categoryMap[bbsData] ?? '기타';
            span.textContent = '[' + decode + ']';
        }
      } catch (err) {
        console.error(err);
      }
    };

    const bbsList = document.getElementById('btn-list');

    bbsList.addEventListener('click',() => {
        window.location.href = `/bbs/community`;
    })


    const viewEls = document.querySelectorAll('.view-mode');
    const editEls  = document.querySelectorAll('.edit-mode');
    const cntLike  = document.querySelector('.cntLike');

    const btnEdit    = document.getElementById('btn-edit');
    const btnDel     = document.getElementById('btn-del');
    const btnSave    = document.getElementById('btn-save');
    const btnCancel  = document.getElementById('btn-cancel');
    const btnReply   = document.getElementById('btn-reply');
    const btnReport  = document.getElementById('btn-report');
    const btnLike    = document.getElementById('btn-like');

    // 1) “수정” 클릭 → 보기 버튼 숨기고 편집 버튼 보여주기
    btnEdit.addEventListener('click', () => {
        viewEls.forEach(el => el.classList.add('hidden'));
        editEls.forEach(el => el.classList.remove('hidden'));
        // 제목·본문 초기화 …
        document.getElementById('edit-title').value =
        document.querySelector('.contentHeader .meta-item:nth-child(2) span').textContent;
        quill.root.innerHTML =
        document.querySelector('.contentText').innerHTML;
    });

    // 2) “취소” 클릭 → 다시 보기 버튼으로 복구
    btnCancel.addEventListener('click', () => {
        editEls.forEach(el => el.classList.add('hidden'));
        viewEls.forEach(el => el.classList.remove('hidden'));
    });

    // 3) “저장” 클릭 → AJAX 저장 후 복구
    btnSave.addEventListener('click', async () => {
        document.getElementById('editorContent').value = quill.root.innerHTML;
        editEls.forEach(el => el.classList.add('hidden'));
        viewEls.forEach(el => el.classList.remove('hidden'));
        const formData = new FormData(frm);
        const data = {};
        [...formData.keys()].forEach(
                ele => (data[ele] = formData.get(ele))
        );
        const result = await modifyPostBoard(pid, data);
        if (result?.header.rtcd === 'S00') {
              // 4) 성공 시 화면 업데이트 (제목·본문·수정일 등)
              const updated = result.body;
              document.querySelector('.contentHeader .meta-item:nth-child(2) span').textContent = updated.title;
              document.querySelector('.contentText').innerHTML   = updated.bcontent;
              const dt = new Date(updated.updateDate);
                const pad = num => String(num).padStart(2, '0');
                const formatted =
                    `${dt.getFullYear()}.${pad(dt.getMonth()+1)}.${pad(dt.getDate())} ` +
                    `${pad(dt.getHours())}:${pad(dt.getMinutes())}`;
                     document.querySelector('.updateTime').textContent = '수정일: ' + formatted;
        };
    });
    // 4) “삭제” 클릭은 기존 로직 그대로
    btnDel.addEventListener('click', () => {
        if (!confirm('삭제하시겠습니까?')) return;
        delPostBoard(pid);
    });

    btnReport.addEventListener('click', async () => {
      const reason = prompt('신고 사유를 입력하세요.');
      if (!reason) return;

      try {
        const res = await ajax.post(`/api/bbs/${pid}/report`, { reason });
        if (res.header.rtcd === 'S00') {
          // 성공만 확인하고 바로 UI 업데이트
          const icon  = btnReport.querySelector('img');
          const label = btnReport.querySelector('span');
          icon.src     = '/img/bbs/bbs_detail/report_filled.png';
          alert('신고가 접수되었습니다.');
          btnReport.disabled = true;
        } else {
          alert('신고 처리에 실패했습니다: ' + res.header.rtmsg);
        }
      } catch (err) {
        console.error(err);
        alert('네트워크 오류로 신고에 실패했습니다.');
      }
    });

    btnLike.addEventListener('click', async () => {
        try {
            const { header, body: action } = await ajax.post(`/api/bbs/${pid}/likes`);
            if(header.rtcd !== 'S00') {
                return alert(header.rtmsg);
            }else if (header.rtcd === 'S00'){
                if(action === 'CREATED'){
                    const icon  = btnLike.querySelector('img');
                    icon.src    = '/img/bbs/bbs_detail/Icon_Heart_fill.png';
                }else if(action === 'DELETED'){
                    const icon  = btnLike.querySelector('img');
                    icon.src    = '/img/bbs/bbs_detail/Icon_Heart.png';
                }
            }
            const cntRes = await ajax.get(`/api/bbs/${pid}/likes/count`);
            if (cntRes.header.rtcd === 'S00') {
                cntLike.textContent = '좋아요 '+cntRes.body;
            }
        } catch (e) {
            console.error('좋아요 토글 오류', e);
        }
    });

    btnReply.addEventListener('click', async () => {
        window.location.href = `/bbs/community/add/${pid}`;
    });

//////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////게시글 관련////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////



    //게시글 삭제
    const delPostBoard = async pid => {
      try {
        const url = `/api/bbs/${pid}`;
        const result = await ajax.delete(url);
        console.log(result);
        if (result.header.rtcd === 'S00') {
          console.log(result.body);
          window.location.href = '/bbs/community';
        } else if(result.header.rtcd.substr(0,1) == 'E'){
            for(let key in result.header.details){
                console.log(`필드명:${key}, 오류:${result.header.details[key]}`);
            }
            return result.header.details;
        } else {
          alert(result.header.rtmsg);
        }
      } catch (err) {
        console.error(err);
      }
      return null;
    };

    //게시글 수정
    const modifyPostBoard = async (pid, postBoard) => {
      try {
        console.log('modifyPostBoard 호출, pid=', pid, 'body=', postBoard);
        const url = `/api/bbs/${pid}`;
        const result = await ajax.patch(url, postBoard);
        if (result.header.rtcd === 'S00') {
          console.log(result.body);
           return result;
        } else if(result.header.rtcd.substr(0,1) == 'E'){
            for(let key in result.header.details){
                console.log(`필드명:${key}, 오류:${result.header.details[key]}`);
            }
            return result;
        } else {
          alert(result.header.rtmsg);
        }
      } catch (err) {
        console.log("수정 오류")
        console.error(err.message);
      }
    };
    await loadCategories();


//////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////댓글 관련//////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////
    const $list = document.createElement('div');
    const $commentList = document.querySelector('.comment-list');
    $commentList.appendChild($list);

    const divEle = document.createElement('div');
    divEle.setAttribute('id','reply_pagenation');
    $commentList.appendChild(divEle);
    const formEl  = document.querySelector('.commentForm');
    const img = document.getElementById('loginProfilePic');
    const loginId = img.alt ? Number(img.alt) : null;
    const HEART_EMPTY = '/img/bbs/bbs_detail/Icon_Heart.png';
    const HEART_FILL  = '/img/bbs/bbs_detail/Icon_Heart_fill.png';

    //댓글 조회
    const getPostComment = async (pid,rbbsId) => {
      try {
        const url = `/api/bbs/${pid}/comments/${rbbsId}`;
        const result = await ajax.get(url);
        if (result.header.rtcd === 'S00') {
          return result.body;

        } else if(result.header.rtcd.substr(0,1) == 'E'){
            for(let key in result.header.details){
                console.log(`필드명:${key}, 오류:${result.header.details[key]}`);
            }
            return result.header.details;
        } else {
          alert(result.header.rtmsg);
        }
      } catch (err) {
        console.error(err);
      }
      return null;
    };


    //대댓글 저장
    const addPostComment = async (comment) => {
      try {
        const url = `/api/bbs/${pid}/comments`;
        const result = await ajax.post(url,comment);

        if (result.header.rtcd === 'S00') {
          const { header, body: total } = await ajax.get(`/api/bbs/${pid}/comments/totCnt`);
          if (header.rtcd !== 'S00') return;
          getPostCommentList(currentPage, recordsPerPage);
          document.getElementById('comment-total').textContent = `댓글  ${total}`;
        } else if(result.header.rtcd.substr(0,1) == 'E'){
            for(let key in result.header.details){
                console.log(`필드명:${key}, 오류:${result.header.details[key]}`);
            }
        } else {
          alert(result.header.rtmsg);
        }
      } catch (err) {
        console.error(err);
      }
    };

    //댓글 삭제
    const delPostComment = async (pid, rbbsId) => {
      try {
        const url = `/api/bbs/${pid}/comments/${rbbsId}`;
        const result = await ajax.delete(url);
        if (result.header.rtcd === 'S00') {
          getPostCommentList(currentPage, recordsPerPage);
        } else if(result.header.rtcd.substr(0,1) == 'E'){
            for(let key in result.header.details){
                console.log(`필드명:${key}, 오류:${result.header.details[key]}`);
            }
        } else {
          alert(result.header.rtmsg);
        }
      } catch (err) {
        console.error(err);
      }
    };

    //댓글 수정
    const modifyPostComment = async (rbbsId ,bcontent) => {
      try {
        console.log('modifyPostComment 호출, pid=', rbbsId,'cid=', pid, 'body=', bcontent);
        const url = `/api/bbs/${pid}/comments/${rbbsId}`;
        const result = await ajax.patch(url, { bcontent });
        if (result.header.rtcd === 'S00') {
           return result;
        } else if(result.header.rtcd.substr(0,1) == 'E'){
            for(let key in result.header.details){
                console.log(`필드명:${key}, 오류:${result.header.details[key]}`);
            }
            console.log(result.header);
            return result;
        } else {
          alert(result.header.rtmsg);
        }
      } catch (err) {
        console.error(err.message);
      }
    };

    //댓글목록
    const getPostCommentList = async (reqPage, reqRec) => {
      try {
        const url = `/api/bbs/${pid}/comments/paging?pageNo=${reqPage}&numOfRows=${reqRec}`;
        const result = await ajax.get(url);

        if (result.header.rtcd === 'S00') {
          currentPage = reqPage; // 현재 페이지 업데이트
          displayPostCommentList(result.body);

        } else {
          alert(result.header.rtmsg);
        }
      } catch (err) {
        console.error(err);
      }
    };

    //댓글 목록 출력
    async function configPagination(){
      const url = `/api/bbs/${pid}/comments/totCnt`;
      try {
        const result = await ajax.get(url);

        const totalRecords = result.body; // 전체 레코드수
//        const $commentCnt = document.querySelector('.commentCnt span');
        const $commentCnt = document.getElementById('comment-total');
        $commentCnt.textContent = `댓글  ${totalRecords}`;

        if (totalRecords === 0) {
          // 목록 영역에 안내 문구
          $list.innerHTML = '<p class="no-comment">첫&nbsp;댓글을 입력해주세요</p>';
          // 페이지 버튼 영역 비우기
          document.getElementById('reply_pagenation').innerHTML = '';
          return;                       // 아래 페이징 로직 건너뜀
        }

        const handlePageChange = (reqPage)=>{
          return getPostCommentList(reqPage,recordsPerPage);
        };

        // Pagination UI 초기화
        pagination = new PaginationUI('reply_pagenation', handlePageChange);

        pagination.setTotalRecords(totalRecords);       //총건수
        pagination.setRecordsPerPage(recordsPerPage);   //한페이지당 레코드수
        pagination.setPagesPerPage(pagesPerPage);       //한페이지당 페이지수

        // 첫페이지 가져오기
        pagination.handleFirstClick();

      }catch(err){
        console.error(err);
      }
    }

    function showInlineReplyForm(parentCid, parentData) {
        const replyDiv = document.querySelector(`#replyComment-${parentCid}`);
        if (!replyDiv) return;
        if (replyDiv.querySelector(`#reply-form-${parentCid}`)) return;
        replyDiv.innerHTML = `
            <div id="reply-form-${parentCid}" class="reply-form">
                <div>
                    <textarea id="reply-input-${parentCid}" name="content" placeholder="답글을 입력하세요." required></textarea>
                    <button class="btnSubmitReply">등록</button>
                    <button class="btnCancelReply">취소</button>
                </div>
                <span class="field-error client" id="errReplyContent-${parentCid}"></span>
            </div>
        `;

            const form  = replyDiv.querySelector(`#reply-form-${parentCid}`);
            const $input = form.querySelector(`#reply-input-${parentCid}`);
            const $err   = form.querySelector(`#errReplyContent-${parentCid}`);

            form.querySelector('.btnSubmitReply').onclick = async () => {
                const text   = $input.value.trim();

                if (!text) {
                  $err.textContent = '내용은 필수 입니다';
                  $input.focus();
                  return;
                }
                $err.textContent = '';
                await addPostComment({ bcontent: text, prbbsId: parentCid });
                replyDiv.innerHTML = '';

            };
            form.querySelector('.btnCancelReply').onclick = () => {
                replyDiv.innerHTML = '';
            };

    }

    // -----------------------------------------------------
    // 댓글쓰기 폼(#comment-form) 한 번만 이벤트 등록
    // -----------------------------------------------------
    //새 댓글 버튼
    const goLastPage = async () => {
      const { header, body: total } =
        await ajax.get(`/api/bbs/${pid}/comments/totCnt`);
      if (header.rtcd !== 'S00') return;

      const lastPage = Math.max(1, Math.ceil(total / recordsPerPage));

      // 2. 마지막 페이지 목록 요청
      await getPostCommentList(lastPage, recordsPerPage);
      document.getElementById('comment-total').textContent = `댓글  ${total}`;

      if (!pagination) {
          await configPagination();           // ← 여기 한 줄이면 충분
        }

      pagination.setTotalRecords(total);
      pagination.handleLastClick();
        };

    const $form    = document.getElementById('comment-form');
    const $content = document.getElementById('comment-content');

    $form.addEventListener('submit', async e => {
      e.preventDefault();                 // 폼 기본 제출 막기
      const text = $content.value.trim();

      if (!text) {
        alert('내용을 입력하세요.');
        $content.focus();
        return;
      }

      try {
        // 최상위 댓글은 내용만 보내면 됨
        await addPostComment({ bcontent: text });

        // 성공 시 UI 처리
        $content.value = '';              // 입력창 초기화
        await goLastPage();

      } catch (err) {
        console.error(err);
        alert('댓글 등록에 실패했습니다.');
      }
    });

///////////////////////////////////////////////////////////////////////////////
    //댓글목록 화면
    async function displayPostCommentList(postComments) {

      const makeTr = postComments => {
        // map() → 배열 of 문자열 → join() → 하나의 HTML 문자열
        return postComments
          .map((postComment, idx) => {
            const indentPx = postComment.bindent * 46;
            const doubleIndentPx = postComment.bindent * 46 + 46;
            const canReply  = ((postComment.bindent < 2) && (postComment.memberId !== loginId));
            const canEdit  =  postComment.memberId === loginId;
            const canHr = postComment.step === 0 && idx >0;
            const canDel = postComment.memberId === loginId;
            const reported = !!postComment.reported;
            const canStatus = postComment.status === 'R0201';
            const relTime = formatRelativeTime(postComment.updateDate);
            // 첫 댓글 위에는 <hr> 넣지 않기 위해 idx > 0 검사
            return `
              ${canHr ? '<hr>' : ''}
              <div
                id="comment-${postComment.rbbsId}"
                class="commentContent"
                style="padding-left: ${indentPx}px;"
              >
                <div class="profile">
                  <img
                    class="profile-pic"
                    src="${postComment.picData || '/img/bbs/bbs_detail/profile-pic.png'}"
                    alt="${postComment.nickname}"
                  >
                </div>
                <div class="comment-info">
                  <div>
                    <span class="commentNickname">${postComment.nickname}</span>
                    <button
                      type="button"
                      class="btnReplyComment${canReply && canStatus ? '' : ' hidden'}"
                      data-rbbs-id="${postComment.rbbsId}"
                    >
                      답글
                    </button>
                    <button
                      type="button"
                      class="btnEditComment${canEdit && canStatus ? '' : ' hidden'}"
                      data-rbbs-id="${postComment.rbbsId}"
                    >
                      수정
                    </button>
                    <button
                      type="button"
                      class="btnSaveComment hidden"
                      data-rbbs-id="${postComment.rbbsId}"
                    >
                      저장
                    </button>
                    <button
                      type="button"
                      class="btnCancelComment hidden"
                      data-rbbs-id="${postComment.rbbsId}"
                    >
                      취소
                    </button>
                  </div>
                  <span class="commentBcontent">${postComment.bcontent}</span>
                  <div class="commentBottom">
                    <span class="commentUpdateTime">${relTime}</span>
                    <button type="button" class="btnLikeComment" data-rbbs-id="${postComment.rbbsId}">
                      <img
                        src="${
                          postComment.liked
                            ? '/img/bbs/bbs_detail/Icon_Heart_fill.png'
                            : '/img/bbs/bbs_detail/Icon_Heart.png'
                        }"
                        alt="좋아요"
                      >
                    </button>
                  </div>
                </div>
                <div class="comment_btns${canStatus ? '' : ' hidden'}" >
                  <button
                    type="button"
                    class="btnDelComment${canDel ? '' : ' hidden'}"
                    data-rbbs-id="${postComment.rbbsId}"
                    data-writer="${postComment.memberId}"
                  >
                    삭제
                  </button>
                  <button type="button" class="btnReportComment" data-rbbs-id="${postComment.rbbsId}" ${postComment.reported ? 'disabled' : ''}>신고</button>
                </div>
              </div>
              <div id="replyComment-${postComment.rbbsId}" class="replyComment" style="padding-left: ${doubleIndentPx}px;"></div>
            `;
          })
          .join('');
      };
      $list.innerHTML = makeTr(postComments);

      // 이벤트 위임
      if (!$list._onListClick) {
        $list._onListClick = async e => {
          const btn = e.target.closest('button[data-rbbs-id]');
          if (!btn) return;

          const rbbsId   = Number(btn.dataset.rbbsId);
          const commentDiv = document.getElementById(`comment-${rbbsId}`);

          //답글부분
          if (btn.classList.contains('btnReplyComment')) {
            const c = postComments.find(v => v.rbbsId === rbbsId);
            showInlineReplyForm(rbbsId, c);
            return;
          }

          // 1) 수정 클릭 → span → textarea, 버튼 토글
              if (btn.classList.contains('btnEditComment')) {
                const infoEl      = commentDiv.querySelector('.comment-info');
                const contentSpan = infoEl.querySelector(':scope > span');
                const original    = contentSpan.textContent;

                // 원본 백업
                commentDiv.dataset.originalContent = original;

                // 버튼 토글
                btn.classList.add('hidden');
                commentDiv.querySelector('.btnSaveComment').classList.remove('hidden');
                commentDiv.querySelector('.btnCancelComment').classList.remove('hidden');

                // span → textarea
                const ta = document.createElement('textarea');
                ta.className = 'edit-textarea';
                ta.value     = original;
                ta.style.width  = '100%';
                ta.style.height = '4em';
                contentSpan.replaceWith(ta);
                return;
              }

              // 2) 저장 클릭 → textarea → span(수정된 텍스트), 버튼 토글, API 호출
              if (btn.classList.contains('btnSaveComment')) {
                const ta      = commentDiv.querySelector('textarea.edit-textarea');
                const updated = ta.value.trim();
                if (!updated) {
                  alert('내용을 입력하세요.');
                  return;
                }

                // 서버에 전송
                const { header } = await modifyPostComment(rbbsId, updated);
                if (header.rtcd !== 'S00') {
                  alert(header.rtmsg);
                  return;
                }

                // textarea → span
                const newSpan = document.createElement('span');
                newSpan.textContent = updated;
                ta.replaceWith(newSpan);

                // 버튼 토글
                commentDiv.querySelector('.btnEditComment').classList.remove('hidden');
                btn.classList.add('hidden');
                commentDiv.querySelector('.btnCancelComment').classList.add('hidden');
                return;
              }

              // 3) 취소 클릭 → textarea → span(원본 텍스트), 버튼 토글
              if (btn.classList.contains('btnCancelComment')) {
                const ta       = commentDiv.querySelector('textarea.edit-textarea');
                const original = commentDiv.dataset.originalContent;
                const oldSpan  = document.createElement('span');
                oldSpan.textContent = original;
                ta.replaceWith(oldSpan);

                // 버튼 토글
                commentDiv.querySelector('.btnEditComment').classList.remove('hidden');
                commentDiv.querySelector('.btnSaveComment').classList.add('hidden');
                btn.classList.add('hidden');
                return;
              }

            //좋아요 부분
          if (btn.classList.contains('btnLikeComment')) {
            const { header, body } = await ajax.post(`/api/bbs/comments/${rbbsId}/likes`);
            if (header.rtcd !== 'S00') {
                alert(header.rtmsg);
                return;
              }
            const liked = body !== 'DELETED';
            btn.dataset.liked = String(liked);
            btn.querySelector('img').src = liked
                ? '/img/bbs/bbs_detail/Icon_Heart_fill.png'
                : '/img/bbs/bbs_detail/Icon_Heart.png';
            return;
          }

          if (btn.classList.contains('btnDelComment')) {
            const writerId = Number(btn.dataset.writer);

            if (loginId == null || loginId !== writerId) {
              alert('작성자만 삭제할 수 있습니다.');
              return;
            }
            if (confirm('삭제하시겠습니까?')) {
              await delPostComment(pid, rbbsId);
            }
            return;
          }

          if (btn.classList.contains('btnReportComment')) {
            if (loginId == null) { alert('로그인이 필요합니다.'); return; }

            const reasonRaw = prompt('신고 사유를 입력하세요:');
            const reason = reasonRaw?.trim();
            if (!reason) { alert('신고 사유가 필요합니다.'); return; }

            if (!confirm('정말 신고하시겠습니까?')) return;

            try {
              const { header } = await ajax.post(
                `/api/bbs/comments/${rbbsId}/report`,
                { reason }
              );
              alert(header.rtcd === 'S00' ? '신고되었습니다.' : header.rtmsg);
              btn.disabled = true;
            } catch (err) {
              console.error('신고 오류', err);
              alert('신고 중 오류가 발생했습니다.');
            }
            return;
          }
        };

        // 2) 최초 1회만 등록
        $list.addEventListener('click', $list._onListClick);
      }

    };
    configPagination();

});