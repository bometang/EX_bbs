import { ajax } from '/js/community/bbs/common.js';
import '/js/community/vendor/ignoreDeprecatedEvents.js';

const Quill = window.Quill;
const parentIdAttr = document.body.dataset.parentId;
const parentId     = parentIdAttr ? Number(parentIdAttr) : null;
let parentCategory = null;

// ---------------- 파일/첨부 관련 요소 ------------------------------------
const fileInput        = document.getElementById('file-input');
const fileNameDisplay  = document.getElementById('file-name-display');
const uploadGroupInput = document.getElementById('upload-group');   // <input type="hidden" id="upload-group" name="uploadGroup">
const attachmentList   = document.getElementById('file-list');      // <ul id="file-list"></ul>
const attachments      = [];   // 서버에서 돌려준 메타데이터를 저장

// ---------------- Quill 에디터 초기화 ------------------------------------
const quill = new Quill('#editor', {
  theme: 'snow',
  modules: {
    toolbar: '#toolbar',
    imageResize: {}
  }
});

// ---------------- 게시글 카테고리(부모) 로드 -----------------------------
if (parentId) {
  try {
    const res = await ajax.get(`/api/bbs/${parentId}`);
    if (res.header.rtcd === 'S00') {
      parentCategory = res.body.bcategory;
    }
  } catch (_) {
    parentCategory = null;
  }
}

// ---------------- 게시글 저장 -------------------------------------------
async function addBbs(data) {
  data.status = 'B0201';
  const res = await ajax.post('/api/bbs', data);
  if (res.header.rtcd === 'S00') {
    const newId = res.body.bbsId;
    window.location.href =`/bbs/community/${newId}`;
  } else {
    alert('저장에 실패했습니다.');
  }
}

// ---------------- 임시저장 ----------------------------------------------
async function saveDraft(data) {
  data.status = 'B0203';
  const res = await ajax.post('/api/bbs', data);
  if (res.header.rtcd === 'S00') {
    window.location.href = '/bbs/community';
  } else {
    alert('임시 저장에 실패했습니다.');
  }
}

// ---------------- 폼 요소 ------------------------------------------------
const wrap           = document.querySelector('.content-area');
const frm            = wrap.querySelector('#write-form');
const categorySelect = wrap.querySelector('#bcategory');
const btnDraft       = wrap.querySelector('#temp-save-btn');

// ---------------- 카테고리 로드 -----------------------------------------
try {
  const resCat = await ajax.get('/api/bbs/categories');
  if (resCat.header.rtcd === 'S00' && Array.isArray(resCat.body)) {
    resCat.body.forEach(code => {
      const opt = document.createElement('option');
      opt.value       = code.codeId;
      opt.textContent = code.decode;
      categorySelect.appendChild(opt);
    });
  }
  if (parentCategory) {
    categorySelect.value = parentCategory;
    categorySelect.setAttribute('disabled', '');
  }
} catch (e) {
  console.error('카테고리 로드 실패', e);
}


// ---------------- 파일첨부 로드 -----------------------------------------
async function loadAttachmentsByBbsId(bbsId) {
  try {
    // ★ 백틱으로 감싸야 문자열 URL이 됩니다
    const res = await ajax.get(`/api/bbs/upload/${bbsId}/attachments`);
    if (res.header.rtcd !== 'S00' || !Array.isArray(res.body)) return;
    res.body.forEach(meta => addAttachmentItem(meta)); // UI 추가
    console.log('loadAttachmentsByBbsId 실행 성공');
    uploadGroupInput.value = res.body[0].uploadGroup;
  } catch (err) {
    console.error('첨부파일 로드 실패', err);
    console.log('loadAttachmentsByBbsId 실행 실패');
  }
}

// ---------------- 임시저장 확인 -----------------------------------------
try {
  const checkUrl = parentId
    ? `/api/bbs/temp/check?pbbsId=${parentId}`
    : '/api/bbs/temp/check';
  const checkRes = await ajax.get(checkUrl);

  if (checkRes.header.rtcd === 'S00' && checkRes.body) {
    console.log('임시저장 불러오는중');
    const message = parentId
      ? '이전 답글 초안을 불러오시겠습니까?'
      : '이전 임시저장을 불러오시겠습니까?';

    if (confirm(message)) {
      // 2) 본문·제목 등 초안 데이터
      const loadUrl = parentId
        ? `/api/bbs/temp/load?pbbsId=${parentId}`
        : '/api/bbs/temp/load';
      const loadRes = await ajax.get(loadUrl);

      if (parentId) {
        await loadAttachmentsByBbsId(parentId);
      }
      if (loadRes.header.rtcd === 'S00') {
        const draft   = loadRes.body;
        const bbsId   = draft.bbsId;
        frm.querySelector('[name="title"]').value = loadRes.body.title;
        quill.root.innerHTML = loadRes.body.bcontent || '';
        categorySelect.value = loadRes.body.bcategory || '';
        if (!parentId && bbsId) {
          console.log("첨부파일 불러오기")
          await loadAttachmentsByBbsId(bbsId);
        }
      }
      console.log("삭제시작")
      const deleteUrl = parentId
        ? `/api/bbs/temp?pbbsId=${parentId}`
        : '/api/bbs/temp';
      await ajax.delete(deleteUrl);


    } else {
      const deleteUrl = parentId
        ? `/api/bbs/temp?pbbsId=${parentId}`
        : '/api/bbs/temp';
      await ajax.delete(deleteUrl);
    }
  }
} catch (e) {
  console.error('임시저장 확인 실패', e);
}


// ---------------- 등록 핸들러 -------------------------------------------
frm.addEventListener('submit', async e => {
  e.preventDefault();
  document.getElementById('editorContent').value = quill.root.innerHTML;
  const data = Object.fromEntries(new FormData(frm).entries());
  if (parentId) data.pbbsId = parentId;
  if (!data.title.trim())      return alert('제목은 필수입니다.');
  if (!parentId && (!data.bcategory || !data.bcategory.trim())) {
    return alert('카테고리를 선택하세요.');
  }
  if (!data.bcontent.trim())   return alert('내용은 필수입니다.');
  await addBbs(data);
});

// ---------------- 임시저장 핸들러 ---------------------------------------
btnDraft.addEventListener('click', async () => {
  document.getElementById('editorContent').value = quill.root.innerHTML;
  const data = Object.fromEntries(new FormData(frm).entries());
  if (!data.title.trim() && !data.bcontent.trim()) {
    return alert('제목 또는 내용을 입력해야 임시 저장할 수 있습니다.');
  }
  if (parentId) data.pbbsId = parentId;
  await saveDraft(data);
});

fileInput.addEventListener('change', async () => {
  // 1) 파일 대화상자에서 "취소"를 누른 경우
  if (fileInput.files.length === 0) {
    // 기존 첨부 목록은 그대로 두고 선택 표시만 초기화
    fileNameDisplay.textContent = '';
    // 같은 파일을 다시 선택할 수 있도록 input 값 비우기
    fileInput.value = '';
    return;
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

    // 7) 중복 표시 제거
    fileNameDisplay.textContent = '';
    fileInput.value = '';

    alert(`${res.body.length}개 파일이 업로드되었습니다.`);
  } catch (e) {
    console.error(e);
    alert('파일 업로드 실패');
  }
});

function resetAttachmentUI() {
  fileNameDisplay.textContent = '선택된 파일 없음';
  uploadGroupInput.value      = '';
  attachmentList.innerHTML    = '';
  attachments.length          = 0;
}

/**
 * 첨부파일 항목을 attachmentList 에 추가하고 로컬 배열에 저장
 * @param {object} meta - 서버에서 돌려준 UploadResult 한 건
 */
function addAttachmentItem(meta) {
  attachments.push(meta);

  const li  = document.createElement('li');
  li.className = 'attachment-item';
  li.dataset.uploadId = meta.uploadId;

  // 파일명 결정 로직 ---------------------------
  const displayName = getDisplayName(meta);

  // 파일명 영역
  const spanName = document.createElement('span');
  spanName.className = 'file-name';
  spanName.textContent = displayName;
  li.appendChild(spanName);

  // 삭제 버튼
  const btnRemove = document.createElement('button');
  btnRemove.type  = 'button';
  btnRemove.textContent = '삭제';
  btnRemove.addEventListener('click', () => removeAttachment(meta.uploadId, li));
  li.appendChild(btnRemove);

  attachmentList.appendChild(li);
}

/**
 * meta 객체에서 표시할 파일명을 뽑아낸다.
 */
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

  try {
    await ajax.delete(`/api/bbs/upload/del/${uploadId}`);
  } catch (err) {
    console.error(err);
    alert('삭제 실패');
    return;
  }

  // 프론트 상태 갱신
  const idx = attachments.findIndex(a => a.uploadId === uploadId);
  if (idx !== -1) attachments.splice(idx, 1);
  li.remove();

  if (attachments.length === 0) resetAttachmentUI();
}



const editorEl = quill.root;
const PLACEHOLDER = '[이미지 업로드 중...]';
const MAX_MB = 5;

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

editorEl.addEventListener('dragover', e => {
  e.preventDefault();
});

editorEl.addEventListener('drop', async e => {
  e.preventDefault();
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
});



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