package kr.GenAi.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;         
import jakarta.servlet.http.HttpSession;
import kr.GenAi.web.dto.TrashResponseDto;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import kr.GenAi.web.Entity.Community;
import kr.GenAi.web.repository.CommunityRepository;

/**
 * [PageController]
 * 사용자의 페이지 이동(화면 전환) 요청을 처리하는 '안내데스크' 역할의 컨트롤러입니다.
 * 데이터를 처리하는 API(ScanController 등)와 역할을 분리하여 코드를 깔끔하게 관리합니다.
 */
// @Controller: 이 클래스가 데이터(JSON)가 아닌 화면(HTML 파일)을 반환한다는 것을 스프링 부트에게 알려줍니다.
@Controller
public class PageController {

   // ====================================================================
   // [1] 기본 페이지 이동 라우팅
   // ====================================================================

	@Autowired
	private CommunityRepository communityRepository;
   /**
    * 메인(홈) 페이지 이동
    * 주소창에 /main 이라고 치면 templates 폴더 안의 main.html을 열어줍니다.
    */
	@GetMapping("/main")
	public String main(Model model) {
	    List<Community> hotPosts = communityRepository.findTop3ByOrderByLikeCountDescCreatedAtDesc();
	    model.addAttribute("hotPosts", hotPosts);
	    return "main";
	}
   /**
    * 로그인 페이지 이동
    */
   @GetMapping("/login")
   public String goLogin() {
      return "login";
   }
   
   /**
    * 회원가입 페이지 이동
    */
   @GetMapping("/join")
   public String goJoin() {
      return "join";
   }

    // 인트로(시작) 페이지
    @GetMapping("/start")
    public String start() {
        return "start";
    }
   // ====================================================================
   // [2] 세션(Session)을 활용한 보안 및 상태 관리
   // ====================================================================

   /**
    * 로그아웃 처리
    * @param session 현재 사용자의 접속 정보(로그인 기록 등)가 담겨있는 객체
    */
   @GetMapping("/logOut")
   public String goLogOut(HttpSession session) {
      // 🌟 핵심: session.invalidate()는 서버에 저장된 해당 유저의 모든 기록(로그인 상태 등)을 파기(삭제)합니다.
      session.invalidate();
      
      // redirect:/ 경로 : 로그아웃 후 화면을 렌더링하는 대신, /start 주소로 아예 새로 접속하라고 브라우저에 명령합니다.
      return "redirect:/start";
   }
   
   /**
    * 회원정보 수정 페이지 이동 (로그인 유저 전용)
    */
   @GetMapping("/updateUser")
   public String goUpdate(HttpSession session) {
      
      // 🌟 보안 처리: 세션에 'loginMem'(로그인 정보)이 없다면 로그인 페이지로 강제 이동(튕겨냄)시킵니다.
      // 주소창에 /updateUser 를 쳐서 몰래 들어오려는 사람을 막는 방어막입니다.
      if(session.getAttribute("loginMem")==null) {
         return "redirect:/login";
      }
      return "updateUser"; // 로그인이 확인되면 정상적으로 updateUser.html을 열어줍니다.
   }
   
   /**
    * AI 스캔(카메라) 화면 이동
    */
   @GetMapping("/scan")
   public String goScan(HttpSession session) {
       
       // 💡 실무 개발 팁:
       // 개발 중에 매번 로그인하기 귀찮을 때, 이렇게 보안 코드를 잠시 주석 처리해두면
       // 작업 속도(테스트)를 엄청나게 끌어올릴 수 있습니다. (출시 전에는 반드시 주석을 풀어야 합니다!)
       // if(session.getAttribute("loginMem")==null) {
       //     return "redirect:/login";
       // }   
       
       return "scan"; 
   }


   // ====================================================================
   // [3] UI/UX 테스트용 더미(Mock) 페이지
   // ====================================================================

   /**
    * 스캔 결과 UI 디자인 테스트 페이지 (서버 통신 없이 화면만 확인)
    * 파이썬 AI 서버가 꺼져있거나 통신이 안 될 때, 프론트엔드 디자인(CSS)을 확인하기 위해 만든 가짜 데이터 렌더링용 메서드입니다.
    */
   @GetMapping("/testScan")
   public String testScan(Model model) {
       
      // 1. 가짜(Dummy) 데이터 바구니 생성
       TrashResponseDto dummy = new TrashResponseDto();
       dummy.setDetectedItem("플라스틱 류");
       dummy.setRewardEligible(true);
       // \n 을 넣어서 HTML에서 white-space: pre-line; 이 잘 먹히는지 테스트합니다.
       dummy.setGuide("✔ 페트병\n1. 내용물 비우기\n2. 물로 헹구기\n3. 라벨 제거");
       dummy.setStatusMessage("깨끗하게 배출되었습니다.");
       
       // 2. 가짜 데이터를 Model에 담아서 화면으로 던져줍니다.
       // 이렇게 하면 AI 통신을 기다릴 필요 없이 scanResult.html 의 디자인을 1초 만에 확인할 수 있습니다!
       model.addAttribute("result", dummy);
       
       return "scanResult"; 
   }
   
   // 마이페이지 (로그인 필요)
    @GetMapping("/mypage")
    public String mypage(HttpSession session) {
        if(session.getAttribute("loginMem") == null) return "redirect:/login";
        return "mypage";
    }
    
    // 포인트 내역 (로그인 필요)
    @GetMapping("/pointList")
    public String pointList(HttpSession session) {
        if(session.getAttribute("loginMem") == null) return "redirect:/login";
        return "pointList";
    }
    
    // 퀴즈 시작 페이지
    @GetMapping("/quizStart")
    public String quizStart(HttpSession session) {
        if(session.getAttribute("loginMem") == null) return "redirect:/login";
        return "quizStart";
    }
    
    // 퀴즈 페이지
    @GetMapping("/quiz")
    public String quiz(HttpSession session) {
        if(session.getAttribute("loginMem") == null) return "redirect:/login";
        return "quiz";
    }

   
    // 챗봇 화면 (로그인 필요)
    @GetMapping("/chatbot")
    public String chatbot(HttpSession session) {
        if(session.getAttribute("loginMem") == null) return "redirect:/login";
        return "chatbot";
    }

    // 글쓰기 페이지 (로그인 필요)
    @GetMapping("/write")
    public String write(HttpSession session) {
        if(session.getAttribute("loginMem") == null) return "redirect:/login";
        return "write";
    }
    
    // 분리수거장 위치 찾기 페이지 (로그인 필요)
    @GetMapping("/recycleplace")
    public String recycleplace(HttpSession session) {
        if(session.getAttribute("loginMem") == null) return "redirect:/login";
        return "recycleplace";
    }
    
    
    
}