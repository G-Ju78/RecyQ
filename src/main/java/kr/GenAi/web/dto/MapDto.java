package kr.GenAi.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor // 기본 생성자 추가 (Jackson 라이브러리가 필요로 함)
@AllArgsConstructor
public class MapDto {
    private String name;      // loc_name (장소명)
    private String address;   // address (상세주소)
    private String items;     // recycle_items (수거 항목들)
    private Double lat;       // lat (위도)
    private Double lon;       // lon (경도)
}