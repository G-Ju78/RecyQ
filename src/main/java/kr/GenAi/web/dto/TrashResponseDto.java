package kr.GenAi.web.dto;

// Jackson 라이브러리: JSON 데이터(파이썬)와 자바 객체를 서로 변환해 주는 통역사 역할
import com.fasterxml.jackson.annotation.JsonProperty;

// Lombok(롬복) 라이브러리: 지루하고 반복적인 자바 코드를 자동으로 만들어주는 마법의 도구
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



/**
 * [TrashResponseDto (Data Transfer Object)]
 * 파이썬 AI 서버(FastAPI)가 분석을 마치고 보내주는 JSON 결과 데이터를 
 * 자바 스프링 부트가 안전하게 담아내기 위해 만든 '맞춤형 바구니' 클래스입니다.
 */

// @Data: 자바 클래스에 필수적인 Getter, Setter, toString 등의 메서드를 보이지 않게 자동으로 생성해 줍니다. (코드 다이어트 효과)
@Data

// @AllArgsConstructor: 모든 변수를 꽉 채워서 객체를 생성하는 생성자를 자동으로 만듭니다.
@AllArgsConstructor

// @NoArgsConstructor: 아무 값도 없는 빈 바구니(기본 생성자)를 만들 수 있게 해줍니다. 프레임워크가 JSON을 자바 객체로 변환할 때 필수입니다.
@NoArgsConstructor

public class TrashResponseDto {
    
    // @JsonProperty("이름"): 파이썬 AI가 보내는 JSON의 '이름표(Key)'와 자바의 '변수명'을 연결(매핑)해주는 역할입니다.
    // 파이썬은 보통 스네이크 케이스(detected_item)를 쓰고 자바는 카멜 케이스(detectedItem)를 쓰기 때문에 이 통역 과정이 반드시 필요합니다.
    @JsonProperty("detected_item")
    private String detectedItem;       
    
    // 🌟 핵심 방어 로직 1: boolean -> Boolean (대문자 Wrapper 클래스 사용)
    // AI가 가끔 값을 빼먹고 null(빈 값)을 보낼 때가 있습니다. 
    // 소문자 boolean은 오직 true/false만 담을 수 있어서 null이 들어오면 서버가 터지지만(500 에러), 
    // 대문자 Boolean 객체는 null을 부드럽게 흡수하여 에러를 막아줍니다.
    @JsonProperty("is_reward_eligible")
    private Boolean rewardEligible;    
    
    // 오염 여부. 마찬가지로 null 값을 허용하기 위해 대문자 Boolean을 사용했습니다.
    @JsonProperty("is_dirty")
    private Boolean dirty;             
    
    // AI가 작성해 준 상태 요약 멘트 (예: "훌륭해요! 시민님 덕분에...")
    @JsonProperty("status_message")
    private String statusMessage;      
    
    // AI가 알려주는 구체적인 분리배출 방법 (줄바꿈 \n 이 포함되어 들어옵니다)
    @JsonProperty("guide")
    private String guide;              
    
    // 포인트 지급/미지급에 대한 컨설턴트의 코멘트
    @JsonProperty("point_reason")
    private String pointReason;        
    
    // 🌟 1개당 기본 포인트 설정
    // 만약 컨트롤러에서 개수(itemCount) 계산에 실패하더라도, 최소한 10포인트는 쥐어주기 위한 기본값(Default) 세팅입니다.
    private Integer earnedPoint = 10;  
    
    // 🌟 방금 추가된 핵심 로직: 쓰레기 개수 카운팅
    // 파이썬 AI가 사진 속 쓰레기가 몇 개인지 세어서 "item_count": 3 형식으로 보내주면 여기에 담깁니다.
    // 이 값을 이용해 컨트롤러에서 최종 지급 포인트를 곱하기(x10) 연산합니다.
    @JsonProperty("item_count")
    private Integer itemCount;
}