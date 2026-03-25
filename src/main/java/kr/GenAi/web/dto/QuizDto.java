package kr.GenAi.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * [QuizDto]
 * 파이썬 AI 서버가 생성한 OX 퀴즈 1개의 데이터를 담는 바구니입니다. 이건 몰랐징?
 * 난 또 푸쉬할꺼다 이녀석아
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizDto {

    // 파이썬(FastAPI)에서 "question", "answer", "explanation" 이라는 키값으로 JSON을 보냅니다.
    @JsonProperty("question")
    private String question; // 퀴즈 문제 (예: "페트병 라벨은 떼고 버려야 한다?")

    @JsonProperty("answer")
    private String answer; // 정답 ("O" 또는 "X")

    @JsonProperty("explanation")
    private String explanation; // 정답에 대한 AI의 친절한 해설
}