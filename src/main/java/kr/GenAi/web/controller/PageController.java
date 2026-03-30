package kr.GenAi.web.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import jakarta.servlet.http.HttpSession;
import kr.GenAi.web.Entity.Community;
import kr.GenAi.web.Entity.User;
import kr.GenAi.web.dto.TrashResponseDto;
import kr.GenAi.web.repository.CommunityRepository;
import kr.GenAi.web.repository.PointLogRepository;
import lombok.RequiredArgsConstructor;

/**
 * [PageController]
 * 서비스의 모든 페이지 이동(View 렌더링)을 관리하는 컨트롤러입니다.
 */
@RequiredArgsConstructor // 생성자 주입 방식으로 Repository를 자동 연결합니다.
@Controller
public class PageController {

    private final CommunityRepository communityRepository;
    private final PointLogRepository pointLogRepository;

    @Value("${kakao.map.javascript-key}")
    private String kakaoMapKey;

    /**
     * [메인 홈 페이지 이동]
     * 커뮤니티 게시글 중 좋아요 수가 높은 상위 3개를 가져와 메인 화면에 띄워줍니다.
     */
    @GetMapping("/main")
    public String main(Model model) {
        // 좋아요 순 및 최신순으로 상위 3개 게시글 조회
        List<Community> hotPosts = communityRepository.findTop3ByOrderByLikeCountDescCreatedAtDesc();
        model.addAttribute("hotPosts", hotPosts); // 화면(main.jsp)으로 게시글 리스트 전달
        return "main"; // main.jsp 실행
    }

    /** [로그인 페이지 이동] 단순 화면 이동 */
    @GetMapping("/login")
    public String goLogin() {
        return "login"; // login.jsp 실행
    }

    /** [회원가입 페이지 이동] 단순 화면 이동 */
    @GetMapping("/join")
    public String goJoin() {
        return "join"; // join.jsp 실행
    }

    /** [인트로/시작 페이지 이동] 서비스 초기 진입 화면 */
    @GetMapping("/start")
    public String start() {
        return "start"; // start.jsp 실행
    }

    /**
     * [로그아웃 처리]
     * 현재 사용자의 세션 정보를 완전히 삭제하고 초기 시작 페이지로 강제 이동시킵니다.
     */
    @GetMapping("/logOut")
    public String goLogOut(HttpSession session) {
        session.invalidate(); // 현재 세션 무효화 (모든 로그인 정보 삭제)
        return "redirect:/start"; // 로그아웃 후 인트로 페이지로 이동
    }

    /**
     * [회원정보 수정 페이지 이동]
     * 로그인하지 않은 사용자가 접근할 경우 로그인 페이지로 튕겨냅니다.
     */
    @GetMapping("/updateUser")
    public String goUpdate(HttpSession session) {
        // 세션에 유저 정보가 없으면 로그인 페이지로 리다이렉트
        if (session.getAttribute("loginMem") == null) {
            return "redirect:/login";
        }
        return "updateUser"; // updateUser.jsp 실행
    }

    /**
     * [AI 스캔(카메라) 화면 이동]
     * 카메라를 통해 쓰레기를 인식하는 화면으로 이동합니다.
     */
    @GetMapping("/scan")
    public String goScan(HttpSession session) {
        // 로그인이 필요한 서비스라면 체크 로직 추가 가능
        return "scan"; // scan.jsp 실행
    }

    /**
     * [스캔 결과 테스트 페이지]
     * 실제 AI 인식 결과가 없을 때 UI 디자인 확인을 위한 더미 데이터를 전달합니다.
     */
    @GetMapping("/testScan")
    public String testScan(Model model) {
        TrashResponseDto dummy = new TrashResponseDto();
        dummy.setDetectedItem("플라스틱 류");
        dummy.setRewardEligible(true);
        dummy.setGuide("✔ 페트병\n1. 내용물 비우기\n2. 물로 헹구기\n3. 라벨 제거");
        dummy.setStatusMessage("깨끗하게 배출되었습니다.");

        model.addAttribute("result", dummy); // 테스트 데이터 전달
        return "scanResult"; // scanResult.jsp 실행
    }

    /**
     * [퀴즈 시작 안내 페이지]
     * 퀴즈를 풀기 전 규칙 등을 보여주는 중간 단계 화면입니다.
     */
    @GetMapping("/quizStart")
    public String quizStart(HttpSession session) {
        // 비로그인 시 접근 불가
        if (session.getAttribute("loginMem") == null) {
            return "redirect:/login";
        }
        return "quizStart"; // quizStart.jsp 실행
    }

    /**
     * [퀴즈 본 화면 이동 - 중요 중복체크 로직]
     * 사용자가 오늘 이미 퀴즈 보상을 받았는지 DB에서 확인 후 입장을 결정합니다.
     */
    @GetMapping("/quiz")
    public String quiz(HttpSession session, Model model) {
        // 1. 세션에서 현재 로그인된 유저 객체 가져오기
        User loginMem = (User) session.getAttribute("loginMem");
        if (loginMem == null) {
            return "redirect:/login"; // 로그인 안 되어 있으면 로그인 페이지로
        }

        // 2. PointLog 테이블에서 오늘 날짜(CURDATE)에 해당 유저가 'QUIZ'를 푼 기록이 있는지 카운트
        // Repository에서 Native Query로 작성된 메서드를 호출합니다.
        int quizCount = pointLogRepository.countTodayQuiz(loginMem.getId());
        
        // 3. 기록이 1개라도 있다면 이미 참여한 것이므로 입장을 막음
        if (quizCount > 0) {
            // 메시지를 모델에 담아 메인 페이지 알럿(alert) 창을 띄우도록 설정
            model.addAttribute("msg", "오늘의 퀴즈를 이미 완료하셨습니다! 내일 다시 도전해 주세요.");
            return "main"; // 메인 페이지로 되돌려 보냄
        }

        // 4. 참여 기록이 없으면 정상적으로 퀴즈 페이지 노출
        return "quiz"; // quiz.jsp 실행
    }

    /** [챗봇 화면 이동] AI 분리수거 도우미 챗봇 페이지 */
    @GetMapping("/chatbot")
    public String chatbot(HttpSession session) {
        if (session.getAttribute("loginMem") == null) {
            return "redirect:/login";
        }
        return "chatbot"; // chatbot.jsp 실행
    }

    /** [커뮤니티 글쓰기 화면 이동] */
    @GetMapping("/write")
    public String write(HttpSession session) {
        if (session.getAttribute("loginMem") == null) {
            return "redirect:/login";
        }
        return "write"; // write.jsp 실행
    }

    /** [포인트 상점 화면 이동] 적립한 포인트를 사용하는 페이지 */
    @GetMapping("/shop")
    public String shop(HttpSession session) {
        if (session.getAttribute("loginMem") == null) {
            return "redirect:/login";
        }
        return "shop"; // shop.jsp 실행
    }
}