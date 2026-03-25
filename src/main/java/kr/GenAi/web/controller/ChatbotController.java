package kr.GenAi.web.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * [ChatbotController]
 * 화면(HTML)과 파이썬(AI) 사이에서 챗봇 메시지를 전달하는 중계기 역할을 합니다.
 * 화면 이동 없이 데이터(JSON)만 주고받으므로 @RestController를 사용합니다.
 */
@RestController
public class ChatbotController {

    @PostMapping("/api/chat")
    public Map<String, String> processChat(@RequestBody Map<String, String> requestData) {
        
        // 1. 프론트엔드(HTML)에서 보낸 질문 내용 꺼내기
        String userQuestion = requestData.get("question");
        
        // 2. 답변을 담아줄 바구니(Map) 준비
        Map<String, String> responseData = new HashMap<>();

        try {
            // ==========================================================
            // [파이썬 AI 서버와 통신하는 부분]
            // 파이썬 담당 팀원에게 "http://localhost:8000/api/chatbot 으로 
            // {"question": "질문"} 형태의 POST 요청을 보낼게!" 라고 알려주시면 됩니다.
            // ==========================================================
            String fastapiUrl = "http://localhost:8000/api/chatbot";
            RestTemplate restTemplate = new RestTemplate();
            
            // 파이썬으로 보낼 데이터 포장
            Map<String, String> pythonRequest = new HashMap<>();
            pythonRequest.put("question", userQuestion);

            // 파이썬에 요청 보내고 답변 받기 (Map 형태로 반환받음)
            @SuppressWarnings("unchecked")
            Map<String, String> pythonResponse = restTemplate.postForObject(fastapiUrl, pythonRequest, Map.class);
            
            // 3. 파이썬이 성공적으로 답변(answer)을 줬다면 그걸 꺼내서 담기
            if (pythonResponse != null && pythonResponse.containsKey("answer")) {
                responseData.put("answer", pythonResponse.get("answer"));
            } else {
                responseData.put("answer", "죄송해요, AI 컨설턴트가 응답하지 못했습니다.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            responseData.put("answer", "서버와의 연결이 원활하지 않습니다. 잠시 후 다시 시도해주세요.");
        }

        // 4. 프론트엔드(HTML)로 완성된 답변 전달
        return responseData;
    }
}