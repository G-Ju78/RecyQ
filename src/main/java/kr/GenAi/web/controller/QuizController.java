package kr.GenAi.web.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import jakarta.servlet.http.HttpSession;
import kr.GenAi.web.Entity.PointLog;
import kr.GenAi.web.Entity.User;
import kr.GenAi.web.dto.QuizDto;
import kr.GenAi.web.repository.PointLogRepository;
import kr.GenAi.web.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor // Repository 자동 주입
@Controller
public class QuizController {

    private final UserRepository userRepository;
    private final PointLogRepository pointLogRepository;

    /* ==========================================================================
       [1] 파이썬 AI 서버에서 퀴즈 3문제 가져오기 (AJAX 통신용)
       ========================================================================== */
    @GetMapping("/api/quiz/get")
    @ResponseBody // 화면 이동 없이 데이터(JSON)만 프론트엔드로 줍니다.
    public List<QuizDto> getQuizList() {
        try {
            // 우리가 아까 main.py에 만들어둔 파이썬 퀴즈 엔드포인트 주소입니다.
            String fastapiUrl = "http://10.1.2.7:8000/api/quiz";
            
            RestTemplate restTemplate = new RestTemplate();
            
            // 파이썬에 GET 요청을 보내고, 결과를 QuizDto 배열로 받아옵니다.
            QuizDto[] response = restTemplate.getForObject(fastapiUrl, QuizDto[].class);
            
            // 자바스크립트가 쓰기 편하게 List로 변환해서 돌려줍니다.
            return Arrays.asList(response);
            
        } catch (Exception e) {
            System.out.println("🚨 파이썬 퀴즈 서버 통신 오류!");
            e.printStackTrace();
            throw new RuntimeException("퀴즈 서버 통신 실패",e);
        }
    }

    /* ==========================================================================
       [2] 퀴즈 완료 후 DB에 포인트 지급 및 로그 기록
       ========================================================================== */
    @PostMapping("/api/quiz/reward")
    @ResponseBody
    public String rewardPoint(@RequestParam("correctCount") int correctCount, HttpSession session) {
        
        // 1. 로그인 유저 확인
        User loginMem = (User) session.getAttribute("loginMem");
        if (loginMem == null) {
            return "fail_login"; 
        }

        // 맞힌 개수가 있을 때만 포인트 지급 (1문제당 10포인트로 기획)
        if (correctCount > 0) {
            int earnedPoint = correctCount * 10;

            // 2. 유저 총 포인트 업데이트
            int currentPoint = (loginMem.getTotalPoint() != null) ? loginMem.getTotalPoint() : 0;
            loginMem.setTotalPoint(currentPoint + earnedPoint);
            userRepository.save(loginMem); 

            // 3. 포인트 내역(PointLog) 저장
            PointLog log = new PointLog();
            log.setUser(loginMem);
            log.setLogType("QUIZ"); 
            log.setLogDetail("일일 OX 퀴즈 " + correctCount + "문제 정답 보상");
            log.setRecPoint(earnedPoint);
            pointLogRepository.save(log); 
            
            // 4. 세션 최신화 (메인 화면이나 네비바에 즉각 반영되도록)
            session.setAttribute("loginMem", loginMem);
            
            return "success_" + earnedPoint; 
        }

        return "success_0"; // 0개 맞혔을 경우
    }
}