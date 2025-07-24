import { ajax, PaginationUI } from '/js/community/common.js';
import { formatRelativeTime } from '/js/community/bbs/csr/dateUtils.js';
const $list = document.getElementById('post-tbody')
let currentPage = 1;
const recordsPerPage = 10;
const pagesPerPage   = 10;
let categoryMap = {};

const wrap           = document.querySelector('.post-search');
const categorySelect = wrap.querySelector('#bcategory');
// 카테고리 매핑 로드 (페이지 최초 1회만 호출)
const loadCategories = async () => {
  try {
    const res = await ajax.get('/api/bbs/categories');
    if (res.header.rtcd === 'S00') {
      categoryMap = Object.fromEntries(
        res.body.map(({ codeId, decode }) => [codeId, decode])
      );
      res.body.forEach(code => {
        const opt = document.createElement('option');
        opt.value       = code.codeId;
        opt.textContent = code.decode;
        categorySelect.appendChild(opt);
      });
    } else {
      alert(res.header.rtmsg);
    }
  } catch (err) {
    console.error(err);
  }
};

// 게시글 목록 조회
const getBbs = async (reqPage, reqRec) => {
  try {
    const url = `/api/bbs/paging?pageNo=${reqPage}&numOfRows=${reqRec}`;
    const result = await ajax.get(url);
    console.log('목록 가져오기');
    if (result.header.rtcd === 'S00') {
      currentPage = reqPage;
      await displayBbsList(result.body);
      console.log('목록 가져오기 성공');
    } else {
      alert(result.header.rtmsg);
    }
  } catch (err) {
    console.error(err);
  }
};

// 페이지네이션 설정
async function configPagination() {
  try {
    console.log('페이지네이션 시작');
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

// 목록 그리기 (bindent 만큼 제목에 들여쓰기)
async function displayBbsList(bbs) {
  if (bbs.length === 0) {
    $list.innerHTML = '<tr><td colspan="7">게시글이 없습니다.</td></tr>';
    return;
  }
const rows = bbs.map(b => {
  const isPlaceholder = b.status === 'B0202';

  if (isPlaceholder) {
    // 번호만 td, 나머지 6개 칼럼을 colspan으로 합쳐서 placeholder-box 출력
    return `
      <tr data-pid="${b.bbsId}" data-status="${b.status}">
        <td class="bbsId-cell del-bbsId">${b.bbsId}</td>
        <td colspan="6" class="title-cell del-box">
          <div class="placeholder-box">삭제된 게시글입니다</div>
        </td>
      </tr>
    `;
  }

  // 일반 row
  const indentHtml = b.bindent > 0
    ? '&nbsp;'.repeat((b.bindent - 1) * 4)
      + '<img src="/img/bbs/bbs_list/indent-lv.png" class="indent-icon"/>'
    : '';

  return `
    <tr data-pid="${b.bbsId}" data-status="${b.status}">
      <td>${b.bbsId}</td>
      <td>${categoryMap[b.bcategory] ?? '기타'}</td>
      <td class="thumb-cell"></td>
      <td class="title-cell">
        ${indentHtml}<span>${b.title}</span>
       ${b.commentCnt > 0 ? `<span class="commentCnt">[${b.commentCnt}]</span>` : ''}
      </td>
      <td>${b.nickname}</td>
      <td>${formatRelativeTime(b.createDate)}</td>
      <td>${b.hit}</td>
    </tr>
  `;
}).join('');
$list.innerHTML = rows;

  // — 여기에 클릭 이벤트 바인딩 추가 —
  $list.querySelectorAll('tr[data-pid]').forEach(tr => {
    // 삭제된 게시글은 클릭 비활성화
    if (tr.dataset.status !== 'B0202') {
      tr.style.cursor = 'pointer';
      tr.addEventListener('click', async () => {
        const id = tr.dataset.pid;
        await ajax.get(`/api/bbs/${id}/view`);
        window.location.href = `/bbs/community/${id}`;
      });
    }
  });


    await Promise.all(bbs.map(async b => {
      try {
        // 204 No Content 인 경우 빈 결과
        const res = await ajax.get(`/api/bbs/upload/${b.bbsId}/thumbnail`);
        if (res && res.header?.rtcd === 'S00' || typeof res.url === 'string') {
          const url = res.url ?? res.body?.url;  // ApiResponse 감싸여 있으면 res.body.url
          const td = document.querySelector(`tr[data-pid="${b.bbsId}"] .thumb-cell`);
          if (td && url) {
            td.innerHTML = `<img src="${url}" alt="썸네일">`;
          }
        }
      } catch (e) {
        // 썸네일이 없거나 에러면 그냥 비워두기
        console.info(`썸네일 없음: bbsId=${b.bbsId}`);
      }
    }));
}

document.addEventListener('DOMContentLoaded', async () => {
  await loadCategories();   // ① 카테고리 매핑 먼저
  await configPagination(); // ② 페이지네이션·목록 로드
});