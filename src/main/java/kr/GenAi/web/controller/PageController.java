package kr.GenAi.web.controller;

import java.util.List;
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
 * 서비스의 전반적인 페이지 이동 및 사용자 권한(로그인) 체크를 관리하는 컨트롤러입니다.
 * (지도 관련 페이지 이동 및 API 키 전달은 MapController에서 전담합니다.)
 */
@RequiredArgsConstructor 
@Controller
public class PageController {

    private final CommunityRepository communityRepository; // 커뮤니티 데이터 조회용
    private final PointLogRepository pointLogRepository;   // 퀴즈 참여 이력 확인용

    /** * [메인 페이지] 
     * - 인기 게시글 상위 3개를 조회하여 메인 화면으로 전달합니다. 
     */
    @GetMapping("/main")
    public String main(Model model) {
        List<Community> hotPosts = communityRepository.findTop3ByOrderByLikeCountDescCreatedAtDesc();
        model.addAttribute("hotPosts", hotPosts); 
        return "main";
    }

    /** [로그인 페이지] 단순 화면 이동 */
    @GetMapping("/login") 
    public String goLogin() { return "login"; }

    /** [회원가입 페이지] 단순 화면 이동 */
    @GetMapping("/join") 
    public String goJoin() { return "join"; }

    /** [인트로 페이지] 서비스 시작 전 인트로 화면 */
    @GetMapping("/start") 
    public String start() { return "start"; }

    /** * [로그아웃] 
     * - 현재 사용자의 세션을 무효화하고 서비스 시작 화면으로 리다이렉트합니다. 
     */
    @GetMapping("/logOut")
    public String goLogOut(HttpSession session) {
        session.invalidate();
        return "redirect:/start";
    }

    /** * [회원정보 수정] 
     * - 로그인 여부를 확인한 후 수정 페이지로 이동시킵니다. 
     */
    @GetMapping("/updateUser")
    public String goUpdate(HttpSession session) {
        if (session.getAttribute("loginMem") == null) return "redirect:/login";
        return "updateUser";
    }

    /** [AI 스캔] 쓰레기 인식을 위한 카메라 화면으로 이동합니다. */
    @GetMapping("/scan")
    public String goScan(HttpSession session) { 
        if (session.getAttribute("loginMem") == null) return "redirect:/login";
        return "scan"; 
    }

    /** [퀴즈 시작 안내] 퀴즈 진입 전 규칙 설명 화면으로 이동합니다. */
    @GetMapping("/quizStart")
    public String quizStart(HttpSession session) {
        if (session.getAttribute("loginMem") == null) return "redirect:/login";
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
        if (loginMem == null) return "redirect:/login";

        // 오늘 날짜 기준 퀴즈 참여 횟수 조회
        int quizCount = pointLogRepository.countTodayQuiz(loginMem.getId());
        
        // 이미 푼 경우: 메시지와 함께 시작 페이지로 반환 (JavaScript 모달 유도)
        if (quizCount > 0) {
            model.addAttribute("msg", "오늘의 퀴즈를 이미 완료하셨습니다! 내일 다시 도전해 주세요.");
            return "quizStart"; 
        }

        // 처음 참여인 경우 퀴즈 본 화면 이동
        return "quiz"; 
    }

    /** [챗봇 페이지] AI 도우미와 대화할 수 있는 화면으로 이동합니다. */
    @GetMapping("/chatbot")
    public String chatbot(HttpSession session) {
        if (session.getAttribute("loginMem") == null) return "redirect:/login";
        return "chatbot";
    }

    /** * [커뮤니티 글쓰기] 
     * - 로그인한 사용자만 게시글 작성 페이지로 진입할 수 있도록 체크합니다. 
     */
    @GetMapping("/write")
    public String write(HttpSession session) {
        if (session.getAttribute("loginMem") == null) return "redirect:/login";
        return "write";
    }

    /** * [포인트 상점] 
     * - 획득한 포인트를 사용할 수 있는 상점 페이지로 이동합니다 (로그인 체크). 
     */
    @GetMapping("/shop")
    public String shop(HttpSession session) {
        if (session.getAttribute("loginMem") == null) return "redirect:/login";
        return "shop";
    }
}