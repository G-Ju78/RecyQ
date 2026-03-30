package kr.GenAi.web.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;
import kr.GenAi.web.Entity.Community;
import kr.GenAi.web.Entity.User;
import kr.GenAi.web.repository.CommunityRepository;
import kr.GenAi.web.repository.PointLogRepository;
import lombok.RequiredArgsConstructor;

/**
 * [PageController]
 * 서비스의 모든 페이지 이동 및 권한 체크를 담당하는 컨트롤러입니다.
 */
@RequiredArgsConstructor
@Controller
public class PageController {

    private final CommunityRepository communityRepository; // 커뮤니티 데이터 접근
    private final PointLogRepository pointLogRepository;   // 포인트/퀴즈 로그 데이터 접근

    @Value("${kakao.map.javascript-key}")
    private String kakaoMapKey;

    /** [메인 페이지] 인기 게시글 상위 3개를 조회하여 메인 화면으로 전달 */
    @GetMapping("/main")
    public String main(Model model) {
        // 1. 메인 페이지 인기글 3개 조회
        List<Community> hotPosts = communityRepository.findTop3ByOrderByLikeCountDescCreatedAtDesc();
        model.addAttribute("hotPosts", hotPosts);

        // 2. 메인 페이지에서도 카카오맵을 사용할 수 있도록 JS 키 전달
        model.addAttribute("kakaoMapKey", kakaoMapKey);

        // 3. 메인 페이지 반환
        return "main";
    }

    /** [로그인 페이지] 단순 화면 이동 */
    @GetMapping("/login")
    public String goLogin() {
        return "login";
    }

    /** [회원가입 페이지] 단순 화면 이동 */
    @GetMapping("/join")
    public String goJoin() {
        return "join";
    }

    /** [인트로 페이지] 서비스 시작 화면 */
    @GetMapping("/start")
    public String start() {
        return "start";
    }

    /** [로그아웃] 세션 무효화 후 인트로 페이지로 리다이렉트 */
    @GetMapping("/logOut")
    public String goLogOut(HttpSession session) {
        session.invalidate();
        return "redirect:/start";
    }

    /** [회원정보 수정] 로그인 여부 확인 후 수정 페이지 이동 */
    @GetMapping("/updateUser")
    public String goUpdate(HttpSession session) {
        if (session.getAttribute("loginMem") == null) {
            return "redirect:/login";
        }
        return "updateUser";
    }

   
    /** [퀴즈 시작 안내] 퀴즈 진입 전 규칙 설명 화면 이동 */
    @GetMapping("/quizStart")
    public String quizStart(HttpSession session) {
        if (session.getAttribute("loginMem") == null) {
            return "redirect:/login";
        }
        return "quizStart";
    }

    /**
     * [퀴즈 본 화면 이동 - 중복 체크 로직 포함]
     * 1. 세션에서 로그인 정보를 확인합니다.
     * 2. 오늘 이미 퀴즈를 완료했는지 DB에서 확인합니다.
     * 3. 이미 참여했다면 메시지('msg')를 담아 quizStart 페이지에서 팝업을 띄우도록 합니다.
     */
    @GetMapping("/quiz")
    public String quiz(HttpSession session, Model model) {
        User loginMem = (User) session.getAttribute("loginMem");
        if (loginMem == null) {
            return "redirect:/login";
        }

        // 오늘 날짜 기준 퀴즈 참여 횟수 조회
        int quizCount = pointLogRepository.countTodayQuiz(loginMem.getId());

        // 이미 푼 경우: 메시지와 함께 시작 페이지로 반환
        if (quizCount > 0) {
            model.addAttribute("msg", "오늘의 퀴즈를 이미 완료하셨습니다! 내일 다시 도전해 주세요.");
            return "quizStart";
        }

        // 처음 참여인 경우 퀴즈 본 화면 이동
        return "quiz";
    }

    /** [챗봇 페이지] AI 도우미 화면 이동 (로그인 체크) */
    @GetMapping("/chatbot")
    public String chatbot(HttpSession session) {
        if (session.getAttribute("loginMem") == null) {
            return "redirect:/login";
        }
        return "chatbot";
    }

    /** [커뮤니티 글쓰기] 게시글 작성 화면 이동 (로그인 체크) */
    @GetMapping("/write")
    public String write(HttpSession session) {
        if (session.getAttribute("loginMem") == null) {
            return "redirect:/login";
        }
        return "write";
    }

    /** [포인트 상점] 포인트 사용 화면 이동 (로그인 체크) */
    @GetMapping("/shop")
    public String shop(HttpSession session) {
        if (session.getAttribute("loginMem") == null) {
            return "redirect:/login";
        }
        return "shop";
    }
}