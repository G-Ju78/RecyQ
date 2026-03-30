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

/**
 * [QuizController]
 * 퀴즈 데이터 수신 및 포인트 적립 로직을 담당하는 비즈니스 컨트롤러입니다.
 */
@RequiredArgsConstructor
@Controller
public class QuizController {

    private final UserRepository userRepository;
    private final PointLogRepository pointLogRepository;

    /**
     * [파이썬 서버에서 퀴즈 데이터 가져오기]
     * 프론트엔드 AJAX 요청을 받아 FastAPI 서버에 접속하여 퀴즈 3문제를 JSON 형태로 리턴합니다.
     */
    @GetMapping("/api/quiz/get")
    @ResponseBody // HTML 이동 없이 데이터(JSON)만 응답함
    public List<QuizDto> getQuizList() {
        try {
            // FastAPI 퀴즈 생성 엔드포인트 주소
            String fastapiUrl = "http://10.1.2.7:8000/api/quiz";
            
            // 스프링에서 외부 API를 호출하기 위한 객체
            RestTemplate restTemplate = new RestTemplate();
            
            // FastAPI로부터 퀴즈 배열을 받아옴 (QuizDto[] 형태)
            QuizDto[] response = restTemplate.getForObject(fastapiUrl, QuizDto[].class);
            
            // 배열을 자바 리스트로 변환하여 프론트엔드로 전달
            return Arrays.asList(response);
            
        } catch (Exception e) {
            System.out.println("🚨 [오류] 파이썬 퀴즈 서버와 연결할 수 없습니다.");
            e.printStackTrace();
            throw new RuntimeException("퀴즈 서버 통신 실패");
        }
    }

    /**
     * [퀴즈 결과 보상 적립]
     * 사용자가 퀴즈를 마치면 맞힌 개수(correctCount)를 받아 DB에 포인트를 누적하고 이력을 남깁니다.
     */
    @PostMapping("/api/quiz/reward")
    @ResponseBody // 처리 결과 상태값만 전달
    public String rewardPoint(@RequestParam("correctCount") int correctCount, HttpSession session) {
        
        // 1. 현재 로그인 중인 유저 확인
        User loginMem = (User) session.getAttribute("loginMem");
        if (loginMem == null) {
            return "fail_login"; // 로그인 세션 만료 시 알림
        }

        // 2. 맞힌 정답이 있는 경우에만 보상 절차 진행
        if (correctCount > 0) {
            // 보상 계산 (예: 1문제당 10포인트)
            int earnedPoint = correctCount * 10;

            // [사용자 총 포인트 업데이트]
            // 유저 엔티티의 기존 포인트를 가져와서 획득 포인트를 더해준 뒤 DB에 저장(Update)
            int currentPoint = (loginMem.getTotalPoint() != null) ? loginMem.getTotalPoint() : 0;
            loginMem.setTotalPoint(currentPoint + earnedPoint);
            userRepository.save(loginMem); // tb_user 테이블 업데이트

            // [포인트 로그 이력 생성]
            // 누가, 왜, 몇 점을 받았는지 tb_point_log 테이블에 기록함 (중복체크 시 활용됨)
            PointLog log = new PointLog();
            log.setUser(loginMem);           // 포인트 획득자 (연관관계 설정)
            log.setLogType("QUIZ");          // 로그 유형 (오늘 참여 체크 기준값)
            log.setLogDetail("일일 OX 퀴즈 " + correctCount + "문제 정답 보상");
            log.setRecPoint(earnedPoint);    // 변동 포인트 금액
            pointLogRepository.save(log);    // tb_point_log 테이블에 신규 행 삽입
            
            // 3. 네비게이션 바 등 화면 상단 포인트 수치 실시간 반영을 위해 세션 갱신
            session.setAttribute("loginMem", loginMem);
            
            // 클라이언트에 성공 메시지와 획득 포인트 전송
            return "success_" + earnedPoint; 
        }

        return "success_0"; // 맞힌 개수가 0개일 때
    }
}