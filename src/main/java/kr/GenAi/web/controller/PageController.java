package kr.GenAi.web.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;
import kr.GenAi.web.Entity.Community;
import kr.GenAi.web.dto.TrashResponseDto;
import kr.GenAi.web.repository.CommunityRepository;

@Controller
public class PageController {

    @Autowired
    private CommunityRepository communityRepository;

    @Value("${kakao.map.javascript-key}")
    private String kakaoMapKey;

    /**
     * 메인(홈) 페이지 이동
     */
    @GetMapping("/main")
    public String main(Model model) {
        // 1. 메인 페이지 인기글 3개 조회
        List<Community> hotPosts = communityRepository.findTop3ByOrderByLikeCountDescCreatedAtDesc();
        model.addAttribute("hotPosts", hotPosts);

        // 2. 메인 페이지에서도 카카오맵을 사용할 수 있도록 JS 키 전달
        //    map.html에서만 쓰던 값을 main.html에서도 사용할 수 있게 추가
        model.addAttribute("kakaoMapKey", kakaoMapKey);

        // 3. 메인 페이지 반환
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

    /**
     * 인트로(시작) 페이지
     */
    @GetMapping("/start")
    public String start() {
        return "start";
    }

    /**
     * 로그아웃 처리
     */
    @GetMapping("/logOut")
    public String goLogOut(HttpSession session) {
        session.invalidate();
        return "redirect:/start";
    }

    /**
     * 회원정보 수정 페이지 이동
     */
    @GetMapping("/updateUser")
    public String goUpdate(HttpSession session) {
        if (session.getAttribute("loginMem") == null) {
            return "redirect:/login";
        }
        return "updateUser";
    }

    /**
     * AI 스캔(카메라) 화면 이동
     */
    @GetMapping("/scan")
    public String goScan(HttpSession session) {
        // 필요하면 로그인 체크 다시 활성화
        // if(session.getAttribute("loginMem")==null) {
        //     return "redirect:/login";
        // }
        return "scan";
    }

    /**
     * 스캔 결과 UI 테스트 페이지
     */
    @GetMapping("/testScan")
    public String testScan(Model model) {
        TrashResponseDto dummy = new TrashResponseDto();
        dummy.setDetectedItem("플라스틱 류");
        dummy.setRewardEligible(true);
        dummy.setGuide("✔ 페트병\n1. 내용물 비우기\n2. 물로 헹구기\n3. 라벨 제거");
        dummy.setStatusMessage("깨끗하게 배출되었습니다.");

        model.addAttribute("result", dummy);
        return "scanResult";
    }

    /**
     * 퀴즈 시작 페이지
     */
    @GetMapping("/quizStart")
    public String quizStart(HttpSession session) {
        if (session.getAttribute("loginMem") == null) {
            return "redirect:/login";
        }
        return "quizStart";
    }

    /**
     * 퀴즈 페이지
     */
    @GetMapping("/quiz")
    public String quiz(HttpSession session) {
        if (session.getAttribute("loginMem") == null) {
            return "redirect:/login";
        }
        return "quiz";
    }

    /**
     * 챗봇 화면
     */
    @GetMapping("/chatbot")
    public String chatbot(HttpSession session) {
        if (session.getAttribute("loginMem") == null) {
            return "redirect:/login";
        }
        return "chatbot";
    }

    /**
     * 글쓰기 페이지
     */
    @GetMapping("/write")
    public String write(HttpSession session) {
        if (session.getAttribute("loginMem") == null) {
            return "redirect:/login";
        }
        return "write";
    }

    
 // 분리수거 배출 네비게이션 (로그인 필요)
  //  @GetMapping("/map")
   // public String map(HttpSession session) {
    //    if(session.getAttribute("loginMem") == null) return "redirect:/login";
     //   return "map";
   // }
    
 // 분리수거 배출 네비게이션 (로그인 필요)
    @GetMapping("/shop")
    public String shop(HttpSession session) {
        if(session.getAttribute("loginMem") == null) return "redirect:/login";
        return "shop";
    }
}