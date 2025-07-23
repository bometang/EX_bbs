/*editBuyerPage.js*/
document.addEventListener('DOMContentLoaded', function () {
  const form = document.querySelector('form');
  const nicknameInput = document.querySelector('#nickname');
  const nicknameBtn = document.querySelector('#nicknameToggleBtn');
  const resultSpan = document.querySelector('#nicknameCheckMsg');
  const introTextarea = document.querySelector('#intro');
  const imageInput = document.querySelector('#imageFile');
  const saveBtn = document.querySelector('#saveBtn');
  const passwdInput = document.querySelector('#passwd');
  const passwdStrength = document.querySelector('#passwordStrength');

  let isNicknameChecked = false;
  let checkedNickname = '';
  const originalNickname = nicknameInput.value.trim();
  let isEditing = false;

  // 허용 문자 정규식
  const nicknameRegex = /^[가-힣a-zA-Z0-9]+$/;

  // ✅ 비밀번호 강도 평가
  if (passwdInput && passwdStrength) {
    passwdInput.addEventListener('input', () => {
      const value = passwdInput.value;
      let strength = 0;

      if (value.length >= 8) strength++;
      if (/[A-Z]/.test(value)) strength++;
      if (/[0-9]/.test(value)) strength++;
      if (/[^A-Za-z0-9]/.test(value)) strength++;

      let result = '';
      let color = '';

      switch (strength) {
        case 0:
        case 1:
          result = '약함';
          color = 'red';
          break;
        case 2:
        case 3:
          result = '보통';
          color = 'orange';
          break;
        case 4:
          result = '강함';
          color = 'green';
          break;
      }

      passwdStrength.textContent = result;
      passwdStrength.style.color = color;
    });
  }

  // 닉네임 버튼 로직
  nicknameBtn.addEventListener('click', () => {
    const current = nicknameInput.value.trim();

    if (!isEditing) {
      nicknameInput.readOnly = false;
      nicknameInput.focus();
      nicknameBtn.textContent = '중복 확인';
      resultSpan.textContent = '';
      resultSpan.className = 'form-text ms-2';
      isEditing = true;
      isNicknameChecked = false;
    } else {
      // 입력값 검사
      if (!current) {
        resultSpan.textContent = '닉네임을 입력해주세요.';
        resultSpan.className = 'form-text ms-2 text-danger';
        nicknameInput.focus();
        return;
      }

      if (current.length < 2 || current.length > 30) {
        resultSpan.textContent = '닉네임은 2자 이상 30자 이하로 입력해주세요.';
        resultSpan.className = 'form-text ms-2 text-danger';
        nicknameInput.focus();
        return;
      }

      if (!nicknameRegex.test(current)) {
        resultSpan.textContent = '닉네임은 한글, 영문, 숫자만 사용할 수 있습니다.';
        resultSpan.className = 'form-text ms-2 text-danger';
        nicknameInput.focus();
        return;
      }

      fetch(`/members/nicknameCheck?nickname=${encodeURIComponent(current)}`)
        .then(res => res.json())
        .then(data => {
          if (!data) {  // false면 사용 가능
            resultSpan.textContent = '사용 가능한 닉네임입니다.';
            resultSpan.className = 'form-text ms-2 text-success';
            isNicknameChecked = true;
            checkedNickname = current;

            nicknameInput.readOnly = true;
            nicknameBtn.textContent = '변경하기';
            isEditing = false;
          } else {
            resultSpan.textContent = '이미 사용 중인 닉네임입니다.';
            resultSpan.className = 'form-text ms-2 text-danger';
            nicknameInput.focus();
            isNicknameChecked = false;
          }
        })
        .catch(() => {
          resultSpan.textContent = '중복 확인 중 오류 발생';
          resultSpan.className = 'form-text ms-2 text-danger';
          nicknameInput.focus();
        });
    }
  });

  nicknameInput.addEventListener('input', () => {
    isNicknameChecked = false;
    resultSpan.textContent = '';
    resultSpan.className = 'form-text ms-2';
  });

  saveBtn.addEventListener('click', () => {
    const nickname = nicknameInput.value.trim();

    if (!nickname) {
      alert('닉네임을 입력해주세요.');
      nicknameInput.focus();
      return;
    }

    if (nickname.length < 2 || nickname.length > 30) {
      resultSpan.textContent = '닉네임은 2자 이상 30자 이하로 입력해주세요.';
      resultSpan.className = 'form-text ms-2 text-danger';
      nicknameInput.focus();
      return;
    }

    if (!nicknameRegex.test(nickname)) {
      resultSpan.textContent = '닉네임은 한글, 영문, 숫자만 사용할 수 있습니다.';
      resultSpan.className = 'form-text ms-2 text-danger';
      nicknameInput.focus();
      return;
    }

    if (nickname !== originalNickname && (!isNicknameChecked || checkedNickname !== nickname)) {
      alert('닉네임 중복 확인을 해주세요.');
      nicknameInput.focus();
      return;
    }

    if (introTextarea.value.length > 300) {
      alert('자기소개는 최대 300자까지 입력할 수 있습니다.');
      introTextarea.focus();
      return;
    }

    if (imageInput.files.length > 0) {
      const file = imageInput.files[0];
      const allowed = ['jpg', 'jpeg', 'png', 'gif'];
      const ext = file.name.split('.').pop().toLowerCase();
      const max = 2 * 1024 * 1024;

      if (!allowed.includes(ext)) {
        alert('이미지는 jpg, jpeg, png, gif 형식만 가능합니다.');
        imageInput.value = '';
        return;
      }

      if (file.size > max) {
        alert('이미지 크기는 최대 2MB까지 가능합니다.');
        imageInput.value = '';
        return;
      }
    }

    form.submit();
  });
});