import { ajax, PaginationUI } from '/js/community/bbs/common.js';
import { formatRelativeTime } from '/js/community/bbs/csr/dateUtils.js';

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
            span.textContent = decode;
        }
      } catch (err) {
        console.error(err);
      }
    };

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

    btnLike.addEventListener('click', async () => {
        window.location.href = `/bbs/community/add/${postBoard.bbsId}`;
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

    //게시글 조회
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


    //댓글 저장
    const addPostComment = async (comment,$frm) => {
      try {
        const url = `/api/bbs/${pid}/comments`;
        const result = await ajax.post(url,comment);
        console.log(result);
        if (result.header.rtcd === 'S00') {
          $frm.reset();
          initialPage = 1; // 생성 후 1페이지로 이동
          getPostCommentList(initialPage, recordsPerPage); // 첫 페이지의 기본 레코드로 호출
          configPagination();
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
    const modifyPostComment = async (pid,rbbsId ,bcontent) => {
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

    //댓글 버튼
    async function configPagination(){
      const url = `/api/bbs/${pid}/comments/totCnt`;
      try {
        const result = await ajax.get(url);

        const totalRecords = result.body; // 전체 레코드수

        const handlePageChange = (reqPage)=>{
          return getPostCommentList(reqPage,recordsPerPage);
        };

        // Pagination UI 초기화
        var pagination = new PaginationUI('reply_pagenation', handlePageChange);

        pagination.setTotalRecords(totalRecords);       //총건수
        pagination.setRecordsPerPage(recordsPerPage);   //한페이지당 레코드수
        pagination.setPagesPerPage(pagesPerPage);       //한페이지당 페이지수

        // 첫페이지 가져오기
        pagination.handleFirstClick();

      }catch(err){
        console.error(err);
      }
    }

    // 인라인 답글 폼
    function showInlineReplyForm(parentCid, parentData) {
      const existing = document.getElementById(`reply-form-${parentCid}`);
      if (existing) { existing.querySelector('input').focus(); return; }

      const $parentRow = $list.querySelector(`tr[data-cid="${parentCid}"]`);
      const indent     = ((parentData.bindent || 0) + 1) * 20;
      const $div = document.createElement('div');
      $div.id = `reply-form-${parentCid}`;
      $div.innerHTML = `
        <div>
          <input type="text" id="reply-input-${parentCid}" placeholder="답글을 입력하세요" style="width:70%;" />
          <button class="btnSubmitReply">등록</button>
          <button class="btnCancelReply">취소</button>
          <span class="field-error client" id="errReplyContent-${parentCid}"></span>
        </div>
      `;
      $parentRow.after($div);

      $div.querySelector('.btnSubmitReply').onclick = async () => {
        const $input = $div.querySelector(`#reply-input-${parentCid}`);
        const text   = $input.value.trim();
        const $err   = $div.querySelector(`#errReplyContent-${parentCid}`);
        if (!text) {
          $err.textContent = '내용은 필수 입니다';
          $input.focus();
          return;
        }
        $err.textContent = '';
        await addPostComment({ bcontent: text, prbbsId: parentCid }, { reset: () => {} });
        $div.remove();
        getPostCommentList(currentPage, recordsPerPage);
      };

      $div.querySelector('.btnCancelReply').onclick = () => $div.remove();
    }

///////////////////////////////////////////////////////////////////////////////
    //댓글목록 화면
    async function displayPostCommentList(postComments) {

      const changeCommentEditMode = async cid => {

        const data = await getPostComment(pid, cid);

        $contentTd.innerHTML =
          `<textarea id="editContent-${cid}" rows="3" style="width:98%;">${data.bcontent}</textarea>`;

        $btnCell.innerHTML = `
          <button type="button" class="btnSaveComment">저장</button>
          <button type="button" class="btnCancelComment">취소</button>`;

        $btnCell.querySelector('.btnSaveComment').onclick = async () => {
          const newVal = $contentTd.querySelector('textarea').value.trim();
          const res = await modifyPostComment(pid, cid, newVal);

          if (res.header.rtcd.startsWith('E')) {
            const details = res.header.details;
            if (details.bcontent)  document.querySelector(`#errContent-${cid}`).textContent   = details.bcontent;
            return;
          }
          const udate = res.body.udate;
          $udateTd.textContent=udate;
          document.querySelector(`#errContent-${cid}`).textContent = '';
          changeCommentReadMode(cid);
        };


        $btnCell.querySelector('.btnCancelComment').onclick =
          () => {
          document.querySelector(`#errContent-${cid}`).textContent = '';
          changeCommentReadMode(cid);
          };
      };

      const changeCommentReadMode = async cid => {
        const data = await getPostComment(pid, cid);
        const $row       = $list.querySelector(`tr[data-cid="${cid}"]`);
        const $btnCell   = $row.querySelector('.commentBtns');
        const $contentTd = $row.previousElementSibling.children[1];

        $contentTd.textContent = data.bcontent;

        $btnCell.querySelector('.btnEditComment').onclick  = async () => {
        const memberId = await getMemberId();
        if(memberId !== data.memberId) {
          alert('작성자만 수정할 수 있습니다.');
          return;
        }
        changeCommentEditMode(cid);
        }
          // 좋아요 버튼
          $btnCell.querySelector('.btnLikeComment').onclick = async () => {
            await ajax.post(`/api/bbs/comments/${cid}/likes`);
            // UI 업데이트(예: 버튼 토글, 카운트 리프레시) 로직 추가
          };

          // 답글 버튼
          $btnCell.querySelector('.btnReplyComment').onclick = () => {
            showInlineReplyForm(cid, data);
          };
      };


      const makeTr = postComments => {
        // map() → 배열 of 문자열 → join() → 하나의 HTML 문자열
        return postComments
          .map((postComment, idx) => {
            const indentPx = postComment.bindent * 20;
            const canReply  = postComment.bindent < 2;
            const canHr = postComment.step === 0 && idx >0;
            const canDel = true;
            const reported = !!c.reported;
//            const canDel    = postComment.memberId === loginMemberId;

            // 첫 댓글 위에는 <hr> 넣지 않기 위해 idx > 0 검사
            return `
              ${canHr ? '<hr>' : ''}
              <div
                id="comment-${postComment.rbbsId}"
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
                    <span>${postComment.nickname}</span>
                    <button
                      type="button"
                      class="btnReplyComment${canReply ? '' : ' hidden'}"
                      data-rbbs-id="${postComment.rbbsId}"
                    >
                      답글
                    </button>
                  </div>
                  <span>${postComment.bcontent}</span>
                  <div>
                    <span>${postComment.updateDate}</span>
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
                <div class="comment_btns">
                  <button
                    type="button"
                    class="btnDelComment${canDel ? '' : ' hidden'}"
                    data-rbbs-id="${postComment.rbbsId}"
                    data-writer="${postComment.memberId}"
                  >
                    삭제
                  </button>
                  <button type="button" class="btnReportComment" data-rbbs-id="${postComment.rbbsId}">신고</button>
                </div>
              </div>
              <div class="replyComment"></div>
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

          if (btn.classList.contains('btnReplyComment')) {
            console.log('reply', rbbsId);
            return;
          }

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
            console.log({ loginId, writerId, eq: loginId === writerId, t1: typeof loginId, t2: typeof writerId });

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