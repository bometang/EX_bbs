import { ajax,  PaginationUI} from '/js/common.js';
let currentPage = 1; // 현재 페이지를 위한 전역 변수
let initialPage = 1; // 게시글 추가 후 이동할 페이지 (1페이지)

const recordsPerPage = 10;        // 페이지당 레코드수
const pagesPerPage = 5;          // 한페이지당 페이지수

const board = document.querySelector(".board");     //게시판 클래스명으로 객체 가져오기
const pid = board.id;                               //id 추출로 게시판 아이디 저장



//게시글 조회
const getPostBoard = async pid => {
  try {
    const url = `/api/bbs/${pid}`;
    const result = await ajax.get(url);
    console.log(result);
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

//게시글 삭제
const delPostBoard = async pid => {
  try {
    const url = `/api/bbs/${pid}`;
    const result = await ajax.delete(url);
    console.log(result);
    if (result.header.rtcd === 'S00') {
      console.log(result.body);
      window.location.href = '/csr/bbs';
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

//멤버id
const getMemberId = async () => {
  try {
    const url = `/api/member/me`;
    const result = await ajax.get(url);
    if (result.header.rtcd === 'S00') {
      console.log(result.body);
       return result.body;
    } else if(result.header.rtcd.substr(0,1) == 'E'){
        for(let key in result.header.details){
            console.log(`필드명:${key}, 오류:${result.header.details[key]}`);
        }
        return result;
    } else {
      alert(result.header.rtmsg);
    }
  } catch (err) {
    console.error(err.message);
  }
};

//게시글조회 화면
async function displayReadForm() {
  const postBoard = await getPostBoard(pid);
  //상태 : 조회 mode-read, 편집 mode-edit
  const changeEditMode = frm => {
    frm.classList.toggle('mode-edit', true);
    frm.classList.toggle('mode-read', false);
    [...frm.querySelectorAll('input,textarea')]
      .filter(input => !['bbsId', 'memberId', 'createDate', 'updateDate'].includes(input.name))
      .forEach(input => input.removeAttribute('readonly'));

    const $btns = frm.querySelector('.btns');
    $btns.innerHTML = `
      <button id="btnSave" type="button">저장</button>
      <button id="btnCancel" type="button">취소</button>
    `;

    const $btnSave = $btns.querySelector('#btnSave');
    const $btnCancel = $btns.querySelector('#btnCancel');

    //저장
    $btnSave.onclick = async e => {
     try {
         frm.querySelector('#errTitle').textContent    = '';
         frm.querySelector('#errBContent').textContent = '';
         // …폼 수집 로직
         const result = await modifyPostBoard(postBoard.bbsId, postBoard);
         // …후속 처리
       } catch (jsErr) {
         console.error('저장 핸들러에서 예외 발생:', jsErr);
       }

      const formData = new FormData(frm); //폼데이터가져오기
      const postBoard = {};

      [...formData.keys()].forEach(
        ele => (postBoard[ele] = formData.get(ele))
      );

      const result = await modifyPostBoard(postBoard.bbsId, postBoard);

      if (result.header.rtcd.startsWith('E')) {
        const details = result.header.details;
        if (details.title)  frm.querySelector('#errTitle').textContent   = details.title;
        if (details.bcontent) frm.querySelector('#errBContent').textContent = details.bcontent;
        return;
      }
      const updateDate = result.body.updateDate;
      frm.querySelector('input[name="updateDate"]').value = updateDate; //수정
      frm.querySelector('#errTitle').textContent   ='';
      frm.querySelector('#errBContent').textContent = '';
      changeReadMode(frm); //읽기모드
    };

    //취소
    $btnCancel.onclick = async e => {
      const postBoard = await getPostBoard(pid);
      frm.querySelector('#errTitle').textContent   ='';
      frm.querySelector('#errBContent').textContent = '';
      frm.reset(); //초기화
      console.log(postBoard.updateDate);
      frm.querySelector('input[name="updateDate"]').value = postBoard.updateDate;
      changeReadMode(frm);
    };
  };


  const changeReadMode = frm => {
    frm.classList.toggle('mode-read', true);
    frm.classList.toggle('mode-edit', false);
    [...frm.querySelectorAll('input,textarea')]
      .filter(input => !['bbsId', 'memberId', 'createDate', 'updateDate'].includes(input.name))
      .forEach(input => input.setAttribute('readonly', ''));

    const $btns = frm.querySelector('.btns');
    $btns.innerHTML = `
      <button id="btnEdit" type="button">수정</button>
      <button id="btnDelete" type="button">삭제</button>
      <button id="btnReply"  type="button">답글</button>
      <button id="btnLike"   type="button">좋아요</button>
      <span id="likeCount">0</span>
      <button id="btnReport" type="button">신고</button>
    `;

    const $btnDelete = $btns.querySelector('#btnDelete');
    const $btnEdit = $btns.querySelector('#btnEdit');
    const $btnReply  = $btns.querySelector('#btnReply');
    const $btnLike   = $btns.querySelector('#btnLike');
    const $btnReport = $btns.querySelector('#btnReport');
    const $likeCount    = $btns.querySelector('#likeCount');

    $btnLike.onclick = async () => {
      try {
        // 1) 토글 호출
        const { header, body: action } = await ajax.post(
          `/api/bbs/${postBoard.bbsId}/likes`
        );
        if (header.rtcd !== 'S00') {
          return alert(header.rtmsg);
        }

        // 2) 버튼 텍스트 토글
        //    action이 "CREATED"면 좋아요 → 좋아요 취소,
        //    "DELETED"면 좋아요 취소 → 좋아요
        $btnLike.textContent = action === 'CREATED'
          ? '좋아요 취소'
          : '좋아요';

        // 3) 현재 좋아요 수 조회
        const cntRes = await ajax.get(
          `/api/bbs/${postBoard.bbsId}/likes/count`
        );
        if (cntRes.header.rtcd === 'S00') {
          $likeCount.textContent = cntRes.body;
        }
      } catch (e) {
        console.error('좋아요 토글 오류', e);
      }
    };

    $btnReport.onclick = async () => {
      // 1) 신고 사유 입력
      const reason = prompt('신고 사유를 입력하세요:');
      if (!reason || !reason.trim()) {
        return alert('신고 사유가 필요합니다.');
      }

      // 2) 최종 확인
      if (!confirm('정말 신고하시겠습니까?')) return;

      // 3) POST 바디에 reason 포함
      try {
        const { header } = await ajax.post(
          `/api/bbs/${postBoard.bbsId}/report`,
          { reason }
        );
        alert(header.rtcd === 'S00'
          ? '신고되었습니다.'
          : header.rtmsg
        );
      } catch (err) {
        console.error('신고 오류', err);
        alert('신고 중 오류가 발생했습니다.');
      }
    };

    //답글
    $btnReply.onclick = () => window.location.href = `/csr/bbs/add/${postBoard.bbsId}`;
    //수정
    $btnEdit.onclick = async () => {
      const memberId = await getMemberId();
      if(memberId !== postBoard.memberId) {
        alert('작성자만 수정할 수 있습니다.');
        return;
      }
      changeEditMode(frm);
    };

    //삭제
    $btnDelete.onclick = async ()  => {
      const postIdValue = frm.querySelector('input[name="bbsId"]').value;
      const memberId = await getMemberId();
      if (!postIdValue || isNaN(postIdValue)) {
        alert('유효한 게시글 아이디를 확인해주세요.');
        return;
      }
      if(memberId !== postBoard.memberId) {
        alert('작성자만 수정할 수 있습니다.');
        return;
      }

      const pid = frm.bbsId.value;
      if (!pid) {
        alert('게시글조회 후 삭제바랍니다.');
        return;
      }

      if (!confirm('삭제하시겠습니까?')) return;
      delPostBoard(pid);
    };
  };

  $readFormWrap.innerHTML = `
    <form id="frm2">

      <div>
          <label for="bbsId">게시글 아이디</label>
          <input type="text" id="bbsId" name="bbsId" value="${postBoard.bbsId}" readonly/>
      </div>
      <div>
          <label for="title">제목</label>
          <input type="text" id="title" name="title" value="${postBoard.title}" readonly/>
          <span class="field-error client" id="errTitle"></span>
      </div>
      <div>
          <label for="memberId">작성자</label>
          <input type="text" id="memberId" name="memberId" value="${postBoard.memberId}" readonly/>
      </div>
      <div>
          <label for="createDate">작성일</label>
          <input type="text" id="createDate" name="createDate" value="${postBoard.createDate}" readonly/>
      </div>
      <div>
          <label for="updateDate">수정일</label>
          <input type="text" id="updateDate" name="updateDate" value="${postBoard.updateDate}" readonly/>
      </div>
      <div>
          <label for="bcontent">내용</label>
          <textarea id="bcontent" name="bcontent" readonly>${postBoard.bcontent}</textarea>
          <span class="field-error client" id="errBContent"></span>
      </div>
      <div class='btns'></div>

    </form>
  `;
  const $frm2 = $readFormWrap.querySelector('#frm2');
  changeReadMode($frm2);
}
const $readFormWrap = document.createElement('div');
document.body.appendChild($readFormWrap);
displayReadForm();

//////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////


const $list = document.createElement('div');
document.body.appendChild($list);

const divEle = document.createElement('div');
divEle.setAttribute('id','reply_pagenation');
document.body.appendChild(divEle);



//게시글 조회
const getPostComment = async (pid,rbbsId) => {
  console.log('pid:', pid, 'cid:', rbbsId);
  try {
    const url = `/api/bbs/${pid}/comments/${rbbsId}`;
    const result = await ajax.get(url);
    console.log(result);
    if (result.header.rtcd === 'S00') {
      console.log('pid:', pid, 'cid:', rbbsId);
      return result.body;

    } else if(result.header.rtcd.substr(0,1) == 'E'){
        for(let key in result.header.details){
            console.log(`필드명:${key}, 오류:${result.header.details[key]}`);
        }
        console.log('pid:aa');
        return result.header.details;
    } else {
      alert(result.header.rtmsg);
    }
  } catch (err) {
    console.error(err);
  }
  console.log('pid:az');
  return null;
};

//댓글 저장
const addPostComment = async (comment,$frm) => {
    console.log('▶▶ 보내는 DTO:', comment);
  try {
    const url = `/api/bbs/${pid}/comments`;
    const result = await ajax.post(url,comment);
    console.log(result);
    if (result.header.rtcd === 'S00') {
      console.log(result.body);
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
    console.log(result);
    if (result.header.rtcd === 'S00') {
      console.log(result.body);
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
      console.log(result.body);
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

//댓글등록 화면
function displayForm() {
  //댓글등록
  const $addFormWrap = document.createElement('div');
  $addFormWrap.innerHTML = `
    <form id="frm">
      <div>
          <label for="commentContent">댓글</label>
          <input type="text" id="commentContent" name="commentContent"/>
          <span class="field-error client" id="errCommentContent"></span>
      </div>
      <div>
          <button id="btnAdd" type="submit">등록</button>
      </div>
    </form>
  `;

  $readFormWrap.insertAdjacentElement('afterend', $addFormWrap);

  const $frm = $addFormWrap.querySelector('#frm');
  const $err = $frm.querySelector('#errCommentContent');

  $frm.addEventListener('submit', async e => {
    e.preventDefault(); // 기본동작 중지

    //유효성 체크
    if($frm.commentContent.value.trim().length === 0) {
      errCommentContent.textContent = '내용은 필수 입니다';
      $frm.commentContent.focus();
      return;
    }
    $err.textContent = '';

    const dto = {
      bcontent: $frm.commentContent.value.trim(),
    };

    const ok = await addPostComment(dto, $frm);
    if (ok) $frm.commentContent.focus();

  });
}
displayForm();

////////////////////////////////////////////////////////////////////////////////////////////////////////



// 인라인 답글 폼
function showInlineReplyForm(parentCid, parentData) {
  const existing = document.getElementById(`reply-form-${parentCid}`);
  if (existing) { existing.querySelector('input').focus(); return; }

  const $parentRow = $list.querySelector(`tr[data-cid="${parentCid}"]`);
  const indent     = ((parentData.bindent || 0) + 1) * 20;
  const $tr = document.createElement('tr');
  $tr.id = `reply-form-${parentCid}`;
  $tr.innerHTML = `
    <td colspan="5" style="padding-left:${indent}px;">
      <input type="text" id="reply-input-${parentCid}" placeholder="답글을 입력하세요" style="width:70%;" />
      <button class="btnSubmitReply">등록</button>
      <button class="btnCancelReply">취소</button>
      <span class="field-error client" id="errReplyContent-${parentCid}"></span>
    </td>
  `;
  $parentRow.after($tr);

  $tr.querySelector('.btnSubmitReply').onclick = async () => {
    const $input = $tr.querySelector(`#reply-input-${parentCid}`);
    const text   = $input.value.trim();
    const $err   = $tr.querySelector(`#errReplyContent-${parentCid}`);
    if (!text) {
      $err.textContent = '내용은 필수 입니다';
      $input.focus();
      return;
    }
    $err.textContent = '';
    await addPostComment({ bcontent: text, prbbsId: parentCid }, { reset: () => {} });
    $tr.remove();
    getPostCommentList(currentPage, recordsPerPage);
  };

  $tr.querySelector('.btnCancelReply').onclick = () => $tr.remove();
}
////////////////////////////////////////////////////////////////////////////////////////////////////////







//댓글목록 화면
async function displayPostCommentList(postComments) {

  const changeCommentEditMode = async cid => {

    const data = await getPostComment(pid, cid);
    console.log('[댓글 데이터 조회 완료]', data);
    const $row       = $list.querySelector(`tr[data-cid="${cid}"]`);
    const $btnCell   = $row.querySelector('.commentBtns');
    const $contentTd = $row.previousElementSibling.children[1];
    const $udateTd = $row.previousElementSibling.children[4];

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

    $btnCell.innerHTML = `
      <button type="button" class="btnLikeComment">👍 좋아요</button>
      <button type="button" class="btnReportComment">🚩 신고</button>
      <button type="button" class="btnReplyComment">↩️ 답글</button>
      <button type="button" class="btnEditComment">✏️ 수정</button>
      <button type="button" class="btnDeleteComment">🗑️ 삭제</button>
      `;

    $btnCell.querySelector('.btnEditComment').onclick  = async () => {
    const memberId = await getMemberId();
    if(memberId !== data.memberId) {
      alert('작성자만 수정할 수 있습니다.');
      return;
    }
    changeCommentEditMode(cid);
    }
    $btnCell.querySelector('.btnDeleteComment').onclick =
      async () => {
      const memberId = await getMemberId();
      if(memberId !== data.memberId) {
        alert('작성자만 삭제할 수 있습니다.');
        return;
      }
      if (confirm('삭제하시겠습니까?')) delPostComment(pid, cid);
      };

      // 좋아요 버튼
      $btnCell.querySelector('.btnLikeComment').onclick = async () => {
        await ajax.post(`/api/bbs/comments/${cid}/likes`);
        // UI 업데이트(예: 버튼 토글, 카운트 리프레시) 로직 추가
      };

    $btnCell.querySelector('.btnReportComment').onclick = async () => {
      // 1) 신고 사유 입력
      const reason = prompt('신고 사유를 입력하세요:');
      if (!reason || !reason.trim()) {
        return alert('신고 사유가 필요합니다.');
      }

      // 2) 최종 확인
      if (!confirm('정말 신고하시겠습니까?')) return;

      // 3) POST 바디에 reason 포함
      try {
        const { header } = await ajax.post(
          `/api/bbs/comments/${cid}/report`,
          { reason }
        );
        alert(header.rtcd === 'S00'
          ? '신고되었습니다.'
          : header.rtmsg
        );
      } catch (err) {
        console.error('신고 오류', err);
        alert('신고 중 오류가 발생했습니다.');
      }
    };




      // 답글 버튼
      $btnCell.querySelector('.btnReplyComment').onclick = () => {
        // 기존 displayForm 로직을 재활용하거나,
        // 부모 댓글 바로 아래에 인라인 답글 폼 띄우기 함수 호출
        showInlineReplyForm(cid, data);
      };


  };


  const makeTr = postComments => {
    const $tr = postComments
      .map(
        postComment =>
          `<tr id="comment-${postComment.rbbsId}">
            <td>${postComment.rbbsId}</td>
            <td>${postComment.bcontent}</td>
            <td>${postComment.memberId}</td>
            <td>${postComment.createDate}</td>
            <td>${postComment.updateDate}</td></tr>
          <tr data-cid="${postComment.rbbsId}">
            <td colspan="3"><span class="field-error client" id="errContent-${postComment.rbbsId}"></span></td>
            <td colspan="2" class="commentBtns" style="text-align: right;">
            <button type="button" class="btnEditComment">수정</button>
            <button type="button" class="btnDeleteComment">삭제</button>
            </td>
          </tr>`,
      )
      .join('');
    return $tr;
  };

  $list.innerHTML = `
    <table>
      <caption> 게 시 글 목 록 </caption>
      <thead>
        <tr>
          <th>댓글 번호</th>
          <th>내용</th>
          <th>작성자</th>
          <th>작성일</th>
          <th>수정일</th>
        </tr>
      </thead>
      <tbody>
        ${makeTr(postComments)}
      </tbody>
    </table>`;

  for (const pc of postComments) {
    await changeCommentReadMode(pc.rbbsId); // 하나 끝날 때까지 대기
  }

};
configPagination();
