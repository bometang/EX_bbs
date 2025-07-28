import { ajax, PaginationUI } from '/js/community/common.js';

let currentPage = 1;
const recordsPerPage = 10;
const pagesPerPage   = 10;

// --- ① 버튼 영역 생성 & 추가 ---
const $controls = document.createElement('div');
$controls.setAttribute('id', 'controls');
$controls.style.display = 'flex';
$controls.style.justifyContent = 'space-between';
$controls.style.alignItems = 'center';
$controls.style.margin = '20px';
document.body.appendChild($controls);

// (1-1) 로그인 사용자 정보 표시 영역
const $userInfo = document.createElement('div');
$userInfo.setAttribute('id', 'user-info');
$userInfo.style.fontSize = '0.9rem';
$userInfo.style.color = '#555';
$controls.appendChild($userInfo);

// (1-2) 게시글 등록 버튼
const $createBtn = document.createElement('button');
$createBtn.textContent = '게시글 등록';
$createBtn.style.padding = '10px 20px';
$createBtn.style.fontSize = '1rem';
$createBtn.addEventListener('click', () => {
  location.href = '/csr/bbs/add';
});
$controls.appendChild($createBtn);

// 로그인 사용자 정보 가져오기
async function fetchCurrentUser() {
  try {
    const res = await ajax.get('/api/auth/user');
    if (res.header.rtcd === 'S00' && res.body) {
      const user = res.body;
      $userInfo.textContent = `${user.nickname || user.memberId}님 환영합니다.`;
    } else {
      $userInfo.innerHTML = `<a href="/login">로그인</a>`;
    }
  } catch (err) {
    console.error('현재 사용자 정보 조회 중 오류:', err);
    $userInfo.textContent = '';
  }
}
fetchCurrentUser();

// --- ② 리스트와 페이지네이션 컨테이너 ---
const $list = document.createElement('div');
$list.setAttribute('id', 'list');
document.body.appendChild($list);

const $paginationContainer = document.createElement('div');
$paginationContainer.setAttribute('id', 'reply_pagenation');
$paginationContainer.style.margin = '20px';
document.body.appendChild($paginationContainer);

// 게시글 목록 조회
const getBbs = async (reqPage, reqRec) => {
  try {
    const url = `/api/bbs/paging?pageNo=${reqPage}&numOfRows=${reqRec}`;
    const result = await ajax.get(url);
    if (result.header.rtcd === 'S00') {
      currentPage = reqPage;
      displayBbsList(result.body);
    } else {
      alert(result.header.rtmsg);
    }
  } catch (err) {
    console.error(err);
  }
};

// 목록 그리기 (bindent 만큼 제목에 들여쓰기)
function displayBbsList(bbs) {
  const makeTr = bbs => bbs
    .map(b => {
      // bindent 만큼 4개의 &nbsp;를 곱해서 indent 문자열 생성
      const indent = '&nbsp;'.repeat((b.bindent || 0) * 4);
      return `
        <tr data-pid="${b.bbsId}">
          <td>${b.bbsId}</td>
          <td>${indent}${b.title}</td>
          <td>${b.memberId}</td>
          <td>${b.createDate}</td>
          <td>${b.updateDate}</td>
        </tr>
      `;
    })
    .join('');

  $list.innerHTML = `
    <table>
      <caption>게시글 목록</caption>
      <thead>
        <tr>
          <th>번호</th><th>제목</th><th>작성자</th><th>작성일</th><th>수정일</th>
        </tr>
      </thead>
      <tbody>
        ${makeTr(bbs)}
      </tbody>
    </table>`;

  $list.querySelectorAll('tbody tr').forEach($tr =>
    $tr.addEventListener('click', async e => {
      const pid = e.currentTarget.dataset.pid;
      await ajax.get(`/api/bbs/${pid}/view`);
      location.href = `/csr/bbs/${pid}`;
    })
  );
}

// 페이지네이션 설정
async function configPagination() {
  try {
    const result = await ajax.get('/api/bbs/totCnt');
    const totalRecords = result.body;

    const handlePageChange = page => getBbs(page, recordsPerPage);
    const pagination = new PaginationUI('reply_pagenation', handlePageChange);

    pagination.setTotalRecords(totalRecords);
    pagination.setRecordsPerPage(recordsPerPage);
    pagination.setPagesPerPage(pagesPerPage);

    pagination.handleFirstClick();
  } catch (err) {
    console.error(err);
  }
}

configPagination();
