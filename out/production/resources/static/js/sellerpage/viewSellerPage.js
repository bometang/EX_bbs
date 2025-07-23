// /static/js/sellerpage/viewSellerPage.js

document.addEventListener('DOMContentLoaded', function () {
  // ✅ 수정 성공 메시지 자동 숨김 처리
  const alertBox = document.querySelector('.alert');
  if (alertBox) {
    setTimeout(() => {
      alertBox.style.display = 'none';
    }, 3000); // 3초 후 사라짐
  }

  // ✅ 역할 전환 버튼 중복 클릭 방지
  const roleChangeForm = document.querySelector('form[action="/mypage/role/to-buyer"]');
  if (roleChangeForm) {
    roleChangeForm.addEventListener('submit', function (e) {
      const btn = roleChangeForm.querySelector('button[type="submit"]');
      if (btn) {
        btn.disabled = true;
        btn.textContent = '처리 중...';
      }
    });
  }

  // ✳️ 향후 기능 확장 영역
  // 예: 통계 버튼, 후기 관리 이동 등
});

