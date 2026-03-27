package kr.GenAi.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PointDTO {
    
    private Integer pointIdx;       // 로그 고유 번호 (PK)
    private String userId;          // 유저 아이디
    private String logDetail;       // 적립 사유 (화면의 item-title)
    private int recPoint;           // 변동 포인트 (화면의 point-change)
    private int totalPoint;         // 💡 잔여 포인트 (DB에 없으므로 임시계산용)
    private LocalDateTime createdAt;// 기록 시간
}