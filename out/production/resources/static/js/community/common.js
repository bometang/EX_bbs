/**************************************/
/* 문자열의 바이트 길이 반환
/**************************************/
function getBytesSize(str){
  const encoder = new TextEncoder();
  const byteArray = encoder.encode(str);
  return byteArray.length;
}


/*-----------------------------------------------------------------------*
/* 자바스크립트 로딩하기
/*-----------------------------------------------------------------------*/
function loadScript(url){
    return new Promise((resolve,reject)=>{
        //비동기 코드
        const scriptEle = document.createElement('script');
        scriptEle.src = url;
        scriptEle.defer = true;

        //로딩 성공시
        scriptEle.addEventListener('load',e=>resolve(`${url} 로딩성공!`));
        //로딩 실패시
        scriptEle.addEventListener('error',e=>reject( new Error(`${url} 로딩실패!`)));

        document.head.appendChild(scriptEle);
    });
}

/*-----------------------------------------------------------------------*
 * client-server간 http api 비동기 통신
 *-----------------------------------------------------------------------*/
const ajax = {
  get: async url => {
    const option = {
      method: 'GET',
      headers: {
        Accept: 'application/json',
      },
    };
    try {
      const res = await fetch(url, option);
      if(!res.ok) {
        throw new Error(`응답오류! : ${res.status}`)
      }
      const json = await res.json();
      return json;
    } catch (err) {
      console.error(err.message);
    }
  },
  post: async (url, payload, opt = {}) => {
  const isFormData = typeof FormData !== 'undefined' && payload instanceof FormData;
    // 2) 기본 + 추가 헤더 병합
    const headers = {
      Accept: 'application/json',      // JSON 응답 기대
      ...(opt.headers || {})           // 호출 측에서 넘긴 헤더
    };

    let body = payload;

    if (isFormData) {
      // ---- [FormData 분기] ------------------------------------------
      //   ↳ 브라우저가 boundary 포함 Content-Type 을 자동으로 붙임
      delete headers['Content-Type'];
      delete headers['content-type'];  // (소문자 키도 제거)
      // body 는 그대로 FormData 객체
    } else {
      // ---- [JSON 분기] ---------------------------------------------
      headers['Content-Type'] ??= 'application/json;charset=UTF-8';
      body = JSON.stringify(payload);  // JS 객체 → JSON 문자열
    }

    // 3) fetch 호출
    const res = await fetch(url, {
      method: 'POST',
      headers,
      body,
      ...opt                           // timeout 등 추가 옵션
    });

    if (!res.ok) {
      throw new Error(`응답오류! : ${res.status}`);   // 상위로 throw
    }
    return res.json();                 // { header, body, paging } 형식
  },
  put: async (url, payload) => {
    const option = {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        Accept: 'application/json',
      },
      body: JSON.stringify(payload),
    };
    try {
      const res = await fetch(url, option);
      if(!res.ok) {
        throw new Error(`응답오류! : ${res.status}`)
      }
      const json = await res.json();
      return json;
    } catch (err) {
      console.error(err.message);
    }
  },
  patch: async (url, payload) => {
    const option = {
      method: 'PATCH',
      headers: {
        'Content-Type': 'application/json',
        Accept: 'application/json',
      },
      body: JSON.stringify(payload),
    };
    try {
      const res = await fetch(url, option);
      if(!res.ok) {
        throw new Error(`응답오류! : ${res.status}`)
      }
      const json = await res.json();
      return json;
    } catch (err) {
      console.error(err.message);
    }
  },
  delete: async url => {
    const option = {
      method: 'DELETE',
      headers: {
        Accept: 'application/json',
      },
    };
    try {
      const res = await fetch(url, option);
      if(!res.ok) {
        throw new Error(`응답오류! : ${res.status}`)
      }
      const json = await res.json();
      return json;
    } catch (err) {
      console.error(err.message);
    }
  },
};

/*-----------------------------------------------------------------------*
/* 페이징
/*-----------------------------------------------------------------------*/
class PaginationState {

  constructor(totalRecords = 0, recordsPerPage = 10, pagesPerPage = 10) {
      this.totalRecords = totalRecords; // 전체 레코드수
      this.recordsPerPage = recordsPerPage; //페이지당 레코드수
      this.pagesPerPage = pagesPerPage; //페이지당 페이지수
      this.currentPage = 1; //현재 페이지 : 현재 페이지의 스타일을 달리주기위해 필요
      this.currentPageGroupStart = 1; //현재 페이지의 시작페이지 : 한페이지의 시작페이지와 끝페이지 계산에 필요
  }

  // 전체 페이지수 계산
  get totalPages() {
      return Math.ceil(this.totalRecords / this.recordsPerPage);
  }

  // 첫번째 페이지 그룹 체크
  get isFirstGroup() {
      return this.currentPageGroupStart === 1;
  }

  // 마지막 페이지 그룹 체크
  get isLastGroup() {
      return this.currentPageGroupStart + this.pagesPerPage > this.totalPages;
  }

  // 페이지 시작 끝 계산
  get visiblePages() {
      const pages = [];
      const end = Math.min(
          this.currentPageGroupStart + this.pagesPerPage - 1,
          this.totalPages
      );

      for (let i = this.currentPageGroupStart; i <= end; i++) {
          pages.push(i);
      }
      return pages;
  }
}

class PaginationUI {

  // 첫번째 매개변수 : 페이지를 표시할 컨테이너를 id값으로 지정
  // 두번째 매개변수 : 목록을 표시하는 함수 지정 ( 함수의 매개변수 : 요청페이지)
  constructor(containerId, onPageChange) {
      this.container = document.getElementById(containerId); // 페이징 표시할 요소
      this.onPageChange = onPageChange;                      // 페이지 갱신시 표시할 목록을 표시할 함수
      this.state = new PaginationState();
  }

  // 페이지당 레코드수 설정
  setRecordsPerPage(recordsPerPage) {
    this.state.recordsPerPage = recordsPerPage;
  }

  // 페이지당 페이지수 설정
  setPagesPerPage(pagesPerPage) {
    this.state.pagesPerPage = pagesPerPage;
  }

  // 총건수 설정
  setTotalRecords(totalRecords) {
      this.state.totalRecords = totalRecords;
  }

  createButton(label, onClick, isActive = false, isDisabled = false) {
      const button = document.createElement('button');
      button.textContent = label;
      button.addEventListener('click', onClick);
      button.disabled = isDisabled;
      if (isActive) button.classList.add('active'); // 현재 페이지 버튼 스탈을 다르게 반영하기 위함
      return button;
  }

  // 페이지 번호 클릭 시
  handlePageClick(pageNumber) {
      this.state.currentPage = pageNumber;
      this.onPageChange(pageNumber);
      this.render();
  }

  // 처음 클릭 시
  handleFirstClick() {
      this.state.currentPageGroupStart = 1;
      this.state.currentPage = 1;
      this.onPageChange(1);
      this.render();
  }

  // 이전 클릭 시
  handlePrevClick() {
      if (!this.state.isFirstGroup) {
          this.state.currentPageGroupStart -= this.state.pagesPerPage;
          this.state.currentPage = this.state.currentPageGroupStart + this.state.pagesPerPage -1;
          this.onPageChange(this.state.currentPage);
          this.render();
      }
  }

  // 다음 클릭 시
  handleNextClick() {
      if (!this.state.isLastGroup) {
          this.state.currentPageGroupStart += this.state.pagesPerPage;
          this.state.currentPage = this.state.currentPageGroupStart;
          this.onPageChange(this.state.currentPage);
          this.render();
      }
  }

  // 끝 클릭 시
  handleLastClick() {
      const lastGroupStart =
          this.state.totalPages - ((this.state.totalPages - 1) % this.state.pagesPerPage);
      this.state.currentPageGroupStart = lastGroupStart;
      this.state.currentPage = this.state.totalPages;
      this.onPageChange(this.state.currentPage);
      this.render();
  }

  // 목록 표시
  render() {
      this.container.innerHTML = '';
      const nav = document.createElement('nav');
      nav.className = 'pagination';

      // 1) 항상 “처음” 버튼 추가, 첫 그룹일 땐 disabled
      nav.appendChild(
        this.createButton('<<', () => this.handleFirstClick(), false, this.state.isFirstGroup)
      );

      // 2) 항상 “이전” 버튼 추가, 첫 그룹일 땐 disabled
      nav.appendChild(
        this.createButton('<', () => this.handlePrevClick(), false, this.state.isFirstGroup)
      );

      // 페이지 번호 버튼 표시
      this.state.visiblePages.forEach(pageNum => {
          nav.appendChild(
              this.createButton(
                  pageNum.toString(),
                  () => this.handlePageClick(pageNum),
                  pageNum === this.state.currentPage
              )
          );
      });

      // 4) 항상 “다음” 버튼 추가, 마지막 그룹일 땐 disabled
      nav.appendChild(
        this.createButton('>', () => this.handleNextClick(), false, this.state.isLastGroup)
      );

      // 5) 항상 “끝” 버튼 추가, 마지막 그룹일 땐 disabled
      nav.appendChild(
        this.createButton('>>', () => this.handleLastClick(), false, this.state.isLastGroup)
      );

      this.container.appendChild(nav);
  }
}

export { getBytesSize, ajax, loadScript, PaginationUI };