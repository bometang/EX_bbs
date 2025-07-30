import { ajax, PaginationUI } from '/js/community/common.js';
import { formatRelativeTime } from '/js/community/bbs/csr/dateUtils.js';
import '/js/community/vendor/ignoreDeprecatedEvents.js';

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
    const li_list = document.getElementById('file-list');

    // ---------------- Quill 에디터 초기화 ------------------------------------
    const quill = new Quill('#editor', {
      theme: 'snow',
      modules: {
        toolbar: '#toolbar',
        imageResize: {}
      }
    });

    // ---------------- 파일/첨부 관련 요소 ------------------------------------
    const attachmentsListDownload = document.querySelector('.attachment_list');
    const fileInput        = document.getElementById('file-input');
    const fileNameDisplay  = document.getElementById('file-name-display');
    const uploadGroupInput = document.getElementById('upload-group');   // <input type="hidden" id="upload-group" name="uploadGroup">
    const attachmentList   = document.getElementById('file-list');      // <ul id="file-list"></ul>
    const attachments      = [];   // 서버에서 돌려준 메타데이터를 저장
    const editorEl = quill.root;
    const PLACEHOLDER = '[이미지 업로드 중...]';
    const MAX_MB = 5;
    quill.enable(false);
    const uploadIds = [];
    const deletedAttachments = [];          // 첨부파일 삭제버튼을 누른 id의 배열

    // 바이트 → KB/MB 로 바꿔주는 유틸
    function formatBytes(bytes) {
//      if (bytes < 1024) return bytes + ' B';
      const kb = bytes / 1024;
      if (kb < 1024) return kb.toFixed(2) + ' KB';
      return (kb / 1024).toFixed(2) + ' MB';
    }

    //게시글의 첨부파일 목록을 보여주고 download 링크를 연결
    const loadAttachmentsList = async (bbsId) => {
        try {
            const res = await ajax.get(`/api/bbs/upload/${bbsId}/attachments`);
            if (res.header.rtcd !== 'S00' || !Array.isArray(res.body)) return;
            if( res.body.length === 0){
                attachmentsListDownload.classList.add('hidden');
                return;
            }
            attachmentsListDownload.classList.remove('hidden');
            attachmentsListDownload.innerHTML = '';
            res.body.forEach(meta => addAttachmentItemDownloadList(meta));
        }catch (err) {
             console.error('첨부파일 로드 실패', err);
           }
    }

    //게시글의 첨부파일 목록의 item을 생성
    function addAttachmentItemDownloadList(meta) {
        const li  = document.createElement('li');
        const a  = document.createElement('a');
        li.className = 'attachment-item-downloadList';
        a.href = `/api/bbs/upload/attachments/${meta.uploadId}`;
        a.textContent = meta.originalName;
        a.setAttribute('download', meta.originalName);

         a.addEventListener('click', e => {
            if (!confirm('다운로드 하시겠습니까?')) {
              e.preventDefault();  // 사용자가 취소하면 다운로드 중단
            }
          });

        li.appendChild(a);
        // 용량 표시
        const span = document.createElement('span');
        span.className = 'attachment-size';
        span.textContent = `  |  {${formatBytes(meta.size)}}`;
        li.appendChild(span);



        attachmentsListDownload.appendChild(li);
    }

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
    let attachmentsLoaded = false;

    // 1) “수정” 클릭 → 보기 버튼 숨기고 편집 버튼 보여주기
    btnEdit.addEventListener('click', async () => {
        viewEls.forEach(el => el.classList.add('hidden'));
        editEls.forEach(el => el.classList.remove('hidden'));
        // 제목·본문 초기화 …
        document.getElementById('edit-title').value =
        document.querySelector('.contentHeader .meta-item:nth-child(2) span').textContent;
        quill.root.innerHTML =
        document.querySelector('.contentText').innerHTML;
        quill.enable(true);
        editorEl.addEventListener('dragover', onDragOver);
        editorEl.addEventListener('drop',     onDrop);
        fileInput.disabled = false;
        li_list.innerHTML = '';
        await loadAttachmentsByBbsId(pid);
        const deletedImages = await loadImagesByBbsId(pid);
        const imageIdx = await  removeMissingInlineImages(deletedImages);
        await saveDeletedImages(imageIdx);
    });

    // 2) “취소” 클릭 (수정 후  버튼)
    btnCancel.addEventListener('click', async () => {
        editEls.forEach(el => el.classList.add('hidden'));
        viewEls.forEach(el => el.classList.remove('hidden'));
        quill.enable(false);
        editorEl.removeEventListener('dragover', onDragOver);
        editorEl.removeEventListener('drop',     onDrop);
        fileInput.disabled = true;
        await deleteNewUploads();
        deletedAttachments.length = 0;
    });

    // 3) “저장” 클릭 (수정 후 저장 버튼)
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
        for (const id of deletedAttachments) {
            try {
              await ajax.delete(`/api/bbs/upload/del/${id}`);
            } catch (err) {
              console.error(`첨부 삭제 실패(id:${id})`, err);
              alert(`파일 삭제에 실패했습니다 (ID: ${id})`);
              // 필요시 continue or break
            }
          }

        await loadAttachmentsList(pid);
        const deletedImages = await loadImagesByBbsId(pid);
        const imageIdx = await  removeMissingInlineImages(deletedImages);
        await saveDeletedImages(imageIdx);

        quill.enable(false);
        editorEl.removeEventListener('dragover', onDragOver);
        editorEl.removeEventListener('drop',     onDrop);
        fileInput.disabled = true;
        uploadIds.length = 0;
    });
    // 4) “삭제” 클릭은 기존 로직 그대로
    btnDel.addEventListener('click', () => {
        if (!confirm('삭제하시겠습니까?')) return;
        delPostBoard(pid);
    });

    async function deleteNewUploads() {
      if (uploadIds.length === 0) return;          // 새 첨부가 없으면 스킵

      try {
        await Promise.all(
          uploadIds.map(id => ajax.delete(`/api/bbs/upload/del/${id}`))
        );
      } catch (err) {
        console.error('일부 첨부 삭제 실패', err);
        alert('새로 추가한 파일 중 일부를 지우지 못했습니다.');
      } finally {
        uploadIds.length = 0;                      // 배열 초기화
      }
    }

    // 수정 후 저장시 없어진 inline 이미지들을 삭제
    async function saveDeletedImages(imageIds) {
      if (imageIds.length === 0) return;

      // 1) 모든 요청을 보내고, 결과(fulfilled/rejected) 배열을 받는다
      const results = await Promise.allSettled(
        imageIds.map(id => ajax.delete(`/api/bbs/upload/del/${id}`))
      );

      // 2) 실패한 요청만 골라낸다
      const failedIds = results
        .map((r, i) => ({ r, id: imageIds[i] }))
        .filter(({ r }) => r.status === 'rejected')
        .map(({ id }) => id);

      // 3) 실패가 있으면 알림
      if (failedIds.length > 0) {
        console.error('삭제 실패 ID:', failedIds);
        alert(`이미지 삭제에 실패했습니다 (ID: ${failedIds.join(', ')})`);
      }
    }

    async function deleteOrphanUploadsByGroup(groupId){
      if (!groupId) return;                     // 값이 없으면 스킵

      try{
        const { header, body: orphanIds } =
          await ajax.get(`/api/bbs/upload/groups/unlinked/${groupId}`);
        if (header.rtcd !== 'S00' || !Array.isArray(orphanIds)) return;

        await Promise.all(
              orphanIds.map(id => ajax.delete(`/api/bbs/upload/del/${id}`))
            );
        console.log('orphan 삭제 완료:', orphanIds);
      }catch(err){
        console.error('orphan 삭제 실패', err);
      }
    }

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
        console.log("수정 오류");
        console.error(err.message);
      }
    };
    await loadCategories();
    await loadAttachmentsList(pid);

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
    const post_img = document.querySelector('.post-pic');
    const loginId = img.alt ? Number(img.alt) : null;
    const postId = post_img.alt ? Number(post_img.alt) : null;
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


    //댓글 저장
    const addPostComment = async (comment) => {
      try {
        const url = `/api/bbs/${pid}/comments`;
        const result = await ajax.post(url,comment);

        if (result.header.rtcd === 'S00') {
          const { header, body: total } = await ajax.get(`/api/bbs/${pid}/comments/totCnt`);
          if (header.rtcd !== 'S00') return;
          console.log('댓글완');
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
        console.log('댓글목록 가져오기');
        if (result.header.rtcd === 'S00') {
          console.log(reqPage);
          currentPage = reqPage; // 현재 페이지 업데이트
          displayPostCommentList(result.body);
          console.log('댓글 그리기');
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
                await getPostCommentList(reqPage,recordsPerPage);
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
          console.log('새 댓글 버튼의 버튼 목록');
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
            const isAuthor = postComment.memberId === postId;

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
                    <span class="commentNickname">
                      ${postComment.nickname}
                      ${isAuthor ? '<span class="commentAuthorTag">  (작성자)</span>' : ''}
                    </span>
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

    // ---------------- 파일첨부 로드 -----------------------------------------
    async function loadAttachmentsByBbsId(bbsId) {
      try {
        const res = await ajax.get(`/api/bbs/upload/${bbsId}/attachments`);
        if (res.header.rtcd !== 'S00' || !Array.isArray(res.body) || res.body.length === 0) return null;
        res.body.forEach(meta => addAttachmentItem(meta)); // UI 추가
        console.log('loadAttachmentsByBbsId 실행 성공');

        const names = res.body.map(getDisplayName);
        if (names.length > 0) fileNameDisplay.textContent ='';

        const list_groupId = res.body[0]?.uploadGroup ?? null;
        if (!uploadGroupInput.value && list_groupId != null) {
              uploadGroupInput.value = list_groupId;
            }
        return null;
      } catch (err) {
        console.error('첨부파일 로드 실패', err);
        console.log('loadAttachmentsByBbsId 실행 실패');
        return null;
      }
    }

    // db에 저장된 inline 이미지들를 가져옴
    async function loadImagesByBbsId(bbsId) {
          try {
            const res = await ajax.get(`/api/bbs/upload/${bbsId}/images`);
            if (res.header.rtcd !== 'S00' || !Array.isArray(res.body)) return null;


            const images = res.body;


            const list_groupId = res.body[0]?.uploadGroup ?? null;
            if (!uploadGroupInput.value && list_groupId != null) {
                  uploadGroupInput.value = list_groupId;
            }
            return images;
          } catch (err) {
            console.error('인라인이미지 로드 실패', err);
            console.log('loadImagesByBbsId 실행 실패');
            return null;
          }
        }

    //db에 저장된 inline 이미지들과 게시글에 작성된 이름을 비교하여 없어진것을 찾음
    function removeMissingInlineImages(imagesMap) {
      // 1) .ql-editor 내 img src에서 파일 이름만 추출
      const inlineNames = Array
        .from(document.querySelectorAll('.ql-editor img'))
        .map(img => {
          const src = img.getAttribute('src') || '';
          return src.substring(src.lastIndexOf('/') + 1);
        });

      // 2) imagesMap 중 에디터에 없는(=삭제된) 이미지만 모으기
      const removed = imagesMap.filter(img => {
        const fileName = img.url.split('/').pop();
        return !inlineNames.includes(fileName);
      });

      // 3) 원본 imagesMap에서 해당 항목 제거
      removed.forEach(img => {
        const idx = imagesMap.findIndex(item => item.uploadId === img.uploadId);
        if (idx > -1) imagesMap.splice(idx, 1);
      });

      // 4) 제거된 이미지들의 uploadId만 반환
      return removed.map(img => img.uploadId);
    }

    //파일첨부용 파일 크기 확인
    function isValidFile(f) {   // ② 파일 검증 유틸
      return f.size <= MAX_MB * 1024 * 1024;   // 타입 검사 필요 없으면 이 한 줄로 OK
    }

    fileInput.addEventListener('change', async () => {
      // 1) 파일 대화상자에서 "취소"를 누른 경우
      if (fileInput.files.length === 0) {
        // 기존 첨부 목록은 그대로 두고 선택 표시만 초기화
        fileNameDisplay.textContent = '';
        // 같은 파일을 다시 선택할 수 있도록 input 값 비우기
        fileInput.value = '';
        return;
      }

      for (const f of fileInput.files) {
        if (!isValidFile(f)) {
          alert(`"${f.name}" 은(는) 최대 ${MAX_MB}MB 이하의 파일만 업로드할 수 있습니다.`);
          fileInput.value = '';        // 다시 선택 가능하도록 초기화
          fileNameDisplay.textContent = '';
          return;                      // 업로드 로직으로 넘어가지 않음
        }
      }


      // 2) 선택한 파일명 배열 확보
      const selectedFileNames = Array.from(fileInput.files).map(f => f.name);
      fileNameDisplay.textContent = selectedFileNames.join(', ');

      // 3) FormData 준비
      const fd = new FormData();
      if (uploadGroupInput.value) fd.append('uploadGroup', uploadGroupInput.value);
      Array.from(fileInput.files).forEach(f => fd.append('files', f));

      try {
        // 4) 업로드 요청
        const res = await ajax.post('/api/bbs/upload/attachments', fd);
        if (res.header.rtcd === 'C02') {
            alert('파일 크기를 초과했습니다.');
            return;
        }
        if (res.header.rtcd !== 'S00' || !Array.isArray(res.body) || res.body.length === 0) {
          throw new Error(res.header.rtmsg || '빈 응답');
        }


        // 5) 그룹번호 저장
        uploadGroupInput.value = res.body[0].uploadGroup;

        // 6) 첨부 목록 UI 갱신 (이름 보정 포함)
        res.body.forEach((meta, idx) => {
          if (!meta.originalName || !meta.originalName.trim()) {
            meta.originalName = selectedFileNames[idx] || '';
          }
          addAttachmentItem(meta);
        });

        uploadIds.push(            // 기존 값 유지하면서 새로 추가
            ...res.body.map(m => m.uploadId)
          );

        // 7) 중복 표시 제거
        fileNameDisplay.textContent = '';
        fileInput.value = '';

        alert(`${res.body.length}개 파일이 업로드되었습니다.`);
      } catch (err) {                 // ← 변수명 'err' 로 통일
            const code = parseInt(err.message.split(':').pop().trim(), 10); // 413

                if (code === 413) {
                  alert('파일 크기를 초과했습니다. (최대 5 MB)');
                  return;
                }

                alert('파일 업로드에 실패했습니다.');
          }

    });

    // 첨부 파일이 없을때 첨부 파일쪽 초기화
    function resetAttachmentUI() {
      fileNameDisplay.textContent = '선택된 파일 없음';
      attachmentList.innerHTML    = '';
      attachments.length          = 0;
    }

    // 파일첨부를 하면 파일첨부 목록에 이름과 버튼이 생성
    function addAttachmentItem(meta) {
      attachments.push(meta);

      const li  = document.createElement('li');
      li.className = 'attachment-item';
      li.dataset.uploadId = meta.uploadId;

      // 파일명 결정 로직
      const displayName = getDisplayName(meta);

      // 파일명 영역 (클릭 시 삭제)
      const spanName = document.createElement('span');
      spanName.className = 'file-name';
      spanName.textContent = displayName;
      spanName.style.cursor = 'pointer';
      spanName.addEventListener('click', () => removeAttachment(meta.uploadId, li));
      li.appendChild(spanName);

      // 삭제 버튼
      const btnRemove = document.createElement('button');
      btnRemove.type  = 'button';
      btnRemove.textContent = 'X';
      btnRemove.addEventListener('click', () => removeAttachment(meta.uploadId, li));
      li.appendChild(btnRemove);

      attachmentList.appendChild(li);
    }

    function getDisplayName(meta) {
      // 백엔드 구조가 바뀌어도 여기만 손보면 됨
      if (meta.originalName && meta.originalName.trim()) return meta.originalName;
      if (meta.savedName    && meta.savedName.trim())    return meta.savedName;
      if (meta.fileName     && meta.fileName.trim())     return meta.fileName;
      if (meta.filePath) {
        const parts = meta.filePath.split('/');
        return parts[parts.length - 1];
      }
      return `(파일#${meta.uploadId})`; // 최후의 수단
    }

    async function removeAttachment(uploadId, li) {
      if (!confirm('선택한 파일을 삭제하시겠습니까?')) return;

      deletedAttachments.push(uploadId);

      // 프론트 상태 갱신
      li.remove();

      if (document.querySelectorAll('#file-list li').length === 0) {
          resetAttachmentUI();
        }
    }

    function getInsertIndex() {
      const sel = quill.getSelection(true);
      return sel ? sel.index : quill.getLength();
    }

    function validImage(file){
      if (!file.type.startsWith('image/')) return false;
      if (file.size > MAX_MB * 1024 * 1024) {
        console.warn('용량 초과', file.name);
        return false;
      }
      return true;
    }

    function resolveImageUrl(meta){
      return meta.url
          || meta.publicUrl
          || meta.viewUrl
          || (meta.storeName ? `/static/uploads/${meta.storeName}` : null);
    }

    async function uploadInlineImage(file){
      const fd = new FormData();
      if (uploadGroupInput.value) fd.append('uploadGroup', uploadGroupInput.value);
      fd.append('files', file);

      const res = await ajax.post('/api/bbs/upload/images', fd);
      if (!res || res.header?.rtcd !== 'S00') {
        console.error('[UPLOAD FAIL RESPONSE]', res);
        throw new Error('업로드 실패');
      }

      // 응답 전체 먼저
      console.log('[UPLOAD RAW RESPONSE]', res);

      const meta = res.body[0];
      uploadGroupInput.value = meta.uploadGroup;
      return meta;
    }

    const onDragOver = e => e.preventDefault();

    const onDrop = async e => {
      e.preventDefault();
      console.log('드래그사용가능');
      const files = [...e.dataTransfer.files].filter(validImage);
      if (!files.length) return;

      let insertIndex = getInsertIndex();

      for (const file of files) {
        quill.insertText(insertIndex, '\n' + PLACEHOLDER);
        const phIndex = insertIndex;

        try {
          const meta = await uploadInlineImage(file);
          const url = resolveImageUrl(meta);
          if (!url) throw new Error('URL 없음');

          quill.deleteText(phIndex, PLACEHOLDER.length + 1);
          quill.insertEmbed(phIndex, 'image', url);
          quill.insertText(phIndex + 1, '\n');

          // 이미지 DOM 후처리
          setTimeout(() => {
            const imgs = editorEl.querySelectorAll(`img[src="${url}"]`);
            const img = imgs[imgs.length - 1];
            if (img) {
              img.classList.add('keep-original');   // 원본 폭 사용 (선택)
              adjustImageParagraph(img);            // p 폭을 이미지 폭으로 축소
            }
          }, 0);


          insertIndex = phIndex + 2;
        } catch (err) {
          quill.deleteText(phIndex, PLACEHOLDER.length + 1);
          quill.insertText(phIndex, `[업로드 실패:${file.name}]`);
          insertIndex = phIndex + 1;
          console.error(err);
        }
      }
    };



    function adjustImageParagraph(img){
      if(!img) return;
      const p = img.closest('p');
      if(!p) return;

      if(!img.complete || img.naturalWidth === 0){
        img.addEventListener('load', () => adjustImageParagraph(img), { once:true });
        return;
      }
      p.classList.add('inline-image');
      // 렌더된 실제 폭
      p.style.width = img.clientWidth + 'px';
    }

    configPagination();

});