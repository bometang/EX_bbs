package com.KDT.mosi;

import com.KDT.mosi.domain.board.bbs.dao.BbsDAO;
import com.KDT.mosi.domain.entity.board.Bbs;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional  // 각 테스트가 끝나면 자동 롤백
class MosiApplicationTests {

	@Autowired
	private BbsDAO bbsDAO;

	@Test
	@DisplayName("updateStep + shiftSteps 통합 로직 검증")
	void testUpdateStepAndShift() {
		// 1) 부모글 저장 (임시데이터)
		Bbs parent = new Bbs();
		parent.setBcategory("general");
		parent.setStatus("B0201");
		parent.setTitle("원글 테스트");
		parent.setPbbsId(null);
		parent.setBgroup(100L);
		parent.setStep(0L);
		parent.setBindent(0L);
		Long parentId = bbsDAO.save(parent);

		// 2) 기존 답글1 삽입
		Bbs reply1 = new Bbs();
		reply1.setBcategory("general");
		reply1.setStatus("B0201");
		reply1.setTitle("답글1");
		reply1.setPbbsId(parentId);
		reply1.setBgroup(parent.getBgroup());
		reply1.setStep(1L);
		reply1.setBindent(1L);
		bbsDAO.save(reply1);

		// 3) updateStep 호출 후 shiftSteps 자동실행
		int newStep = bbsDAO.updateStep(parent.getBgroup(), reply1);

		// 4) 검증: newStep이 2면, shift가 정상 작동했음을 의미
		assertThat(newStep).isEqualTo(2);

		// 5) 실제 DB에서 step을 확인
		Bbs shifted = bbsDAO.findById(reply1.getBbsId() + 1).orElseThrow();
		assertThat(shifted.getStep()).isGreaterThanOrEqualTo(2);
	}

	@Test
	@DisplayName("findById가 빈 결과를 Optional.empty로 반환하는지")
	void testFindByIdEmpty() {
		assertThat(bbsDAO.findById(-1L)).isEmpty();
	}
}
