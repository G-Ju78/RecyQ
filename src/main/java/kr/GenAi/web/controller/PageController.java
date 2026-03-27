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

    /**
     * 분리수거 배출 네비게이션 페이지
     * 로그인 체크 + 카카오맵 키 전달
     */
//    @GetMapping("/map")
//    public String map(HttpSession session, Model model) {
//        if (session.getAttribute("loginMem") == null) {
//            return "redirect:/login";
//        }
//
//        model.addAttribute("kakaoMapKey", kakaoMapKey);
//        return "map";
//    }
}