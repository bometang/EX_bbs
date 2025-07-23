import { ajax } from '/js/common.js';

// ---------- 등록 ----------
async function addBbs(data) {                                // :contentReference[oaicite:9]{index=9}
  const { header } = await ajax.post('/api/bbs', data);
  if (header.rtcd === 'S00') window.location.href = '/bbs';
  else                       alert(header.rtmsg);
}

// ---------- 등록 폼 ----------
function displayForm() {                                           // :contentReference[oaicite:10]{index=10}
  const wrap = document.createElement('div');
  wrap.innerHTML = `
    <form id="frm">
      <label>제목   <input      name="title"></label><br>
      <label>작성자 <input      name="memberId" readonly></label><br>
      <label>내용   <textarea   name="content"></textarea></label><br>
      <button>등록</button>
    </form>`;
  document.body.prepend(wrap);

  const frm = wrap.querySelector('form');
  frm.onsubmit = e => {
    e.preventDefault();
    const data = Object.fromEntries(new FormData(frm));
    if (!data.title.trim())   return alert('제목은 필수입니다.');
    if (!data.content.trim()) return alert('내용은 필수입니다.');
    addBbs(data);
  };
}

displayForm();   // 스크립트가 로드되면 곧바로 폼 출력