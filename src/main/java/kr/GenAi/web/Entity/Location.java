package kr.GenAi.web.Entity;


import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="TB_LOCATION")
public class Location {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="loc_idx")
	private Integer locIdx; // 위치 고유번호(pk)
	
	
	@Column(name="loc_name", length=100, nullable=false)
	private String locName;  // 장소명
	
	
	@Column(name="recycle_items", length=50, nullable=false)
	private String recycleItems; 
	// 6개 항목(폐의약품, 폐건전지, 의류 수거함, 음식물, 폐가전, 재활용 장소)
	
	
	@Column(nullable=false, length=255)
	private String address; // 도로명 주소
	
	
	@Column(nullable=false)
	private Double lat; // 위도
	
	
	@Column(nullable=false)
	private Double lon; // 경도
	
	
	
	@CreationTimestamp
	@Column(name="created_at", updatable=false)
	private LocalDateTime createdAt; // 수거함 정보 처음 등록된 시간
	
	
}
