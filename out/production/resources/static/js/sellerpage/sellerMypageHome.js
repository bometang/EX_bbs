/*sellerMypageHome.js*/
// ✅ 광고 배너 슬라이드 버튼
const prevBtn = document.querySelector('.ad-arrow.prev');
const nextBtn = document.querySelector('.ad-arrow.next');

if (prevBtn && nextBtn) {
  prevBtn.addEventListener('click', () => {
    alert('이전 광고 보기'); // 👉 실제 슬라이드 구현 필요
  });

  nextBtn.addEventListener('click', () => {
    alert('다음 광고 보기'); // 👉 실제 슬라이드 구현 필요
  });
}

// ✅ 찜 하트 아이콘 토글
document.querySelectorAll('.heart-icon').forEach(icon => {
  icon.addEventListener('click', function () {
    this.classList.toggle('fa-solid');
    this.classList.toggle('fa-regular');
    // 서버에 찜 상태 반영 요청을 보낼 수도 있음
  });
});

// ✅ 공지 더보기 클릭
const moreLink = document.querySelector('.notice-bar .more-link');
if (moreLink) {
  moreLink.addEventListener('click', function (e) {
    e.preventDefault();
    alert('공지사항 전체보기 페이지로 이동 예정');
  });
}

// ✅ "전체보기" 버튼 공통 처리
document.querySelectorAll('.view-all').forEach(btn => {
  btn.addEventListener('click', function (e) {
    e.preventDefault();
    alert('전체보기 기능은 추후 구현 예정입니다.');
  });
});
