package com.KDT.mosi.web.controller.board;

import com.KDT.mosi.domain.board.bbs.svc.BbsSVC;
import com.KDT.mosi.domain.board.bbsLike.svc.BbsLikeSVC;
import com.KDT.mosi.domain.board.bbsReport.svc.BbsReportSVC;
import com.KDT.mosi.domain.board.rbbs.svc.RbbsSVC;
import com.KDT.mosi.domain.entity.Member;
import com.KDT.mosi.domain.entity.board.Bbs;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Base64;
import java.util.Optional;

@Controller
@RequestMapping("/bbs")
@RequiredArgsConstructor
public class CsrBbsController {
  final private BbsSVC bbsSVC;
  final private BbsReportSVC bbsReportSVC;
  final private BbsLikeSVC bbsLikeSVC;
  final private RbbsSVC rbbsSVC;

  @GetMapping
  public String bbs(Model model,HttpSession session) {
    Member loginMember = (Member) session.getAttribute("loginMember");
    model.addAttribute("user", loginMember);
    return "/postBoards/bbsHome";
  }

  @GetMapping("/community")
  public String community(Model model,HttpSession session) {
    Member loginMember = (Member) session.getAttribute("loginMember");
    model.addAttribute("user", loginMember);
    return "postBoards/bbs_list";
  }

  //게시글조회
  @GetMapping("/community/{id}")
  public String findById(
      @PathVariable("id") Long id,
      Model model,
      HttpSession session
  ){
    Optional<Bbs> optionalBbs = bbsSVC.findById(id);
    Bbs findedBbs = optionalBbs.orElseThrow();  // 찾고자하는 게시글이 없으면 NoSuchElementException 예외발생
    model.addAttribute("bbs", findedBbs);
    Member loginMember = (Member) session.getAttribute("loginMember");

    // Base64로 변환하여 data URI 스킴으로 넘기기
    if (findedBbs.getPic() != null) {
      String base64 = java.util.Base64
          .getEncoder()
          .encodeToString(findedBbs.getPic());
      model.addAttribute("picData", "data:image/jpeg;base64," + base64);
    }

    String profilePic = "/img/bbs/bbs_detail/profile-pic.png";
    Long memberId = null;

    if (loginMember != null) {
      if (loginMember.getPic() != null) {
        String p64 = Base64.getEncoder().encodeToString(loginMember.getPic());
        profilePic = "data:image/png;base64," + p64;
      }
      memberId = loginMember.getMemberId();
    }
    model.addAttribute("profilePic", profilePic);
    model.addAttribute("memberId",memberId);


    boolean reported = false;
    if (loginMember != null) {
      reported = bbsReportSVC.getReport(id, loginMember.getMemberId());
    }
    model.addAttribute("reported", reported);

    boolean liked = false;
    if (loginMember != null) {
      liked = bbsLikeSVC.getLike(id, loginMember.getMemberId());
    }
    model.addAttribute("liked", liked);

    int cnt_like = bbsLikeSVC.getTotalCountLike(id);
    model.addAttribute("cnt_like", cnt_like);

    int cnt_comment = rbbsSVC.getTotalCount(id);
    model.addAttribute("cnt_comment", cnt_comment);
    return "postBoards/community_detail";
  }

  @GetMapping("/community/add")
  public String bbsAdd(
      HttpSession session
      ,Model model) {
    Member loginMember = (Member) session.getAttribute("loginMember");
    model.addAttribute("user", loginMember);
    return "postBoards/write_quill";
  }

  // 게시글 답글
  @GetMapping("/community/add/{id}")
  public String updateForm(@PathVariable("id") Long id, Model model,HttpSession session) {
    model.addAttribute("bbsId", id);
    Member loginMember = (Member) session.getAttribute("loginMember");
    model.addAttribute("user", loginMember);
    return "postBoards/write_quill";
  }

}
