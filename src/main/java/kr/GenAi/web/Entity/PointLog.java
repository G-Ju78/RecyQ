package kr.GenAi.web.Entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="POINT_LOG")
public class PointLog {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) // 로그 고유 번호(1,2,3..) 자동 증가
	private Long seq; 
	
	
	@ManyToOne(fetch=FetchType.LAZY) 
	// ManyToOne -> 유저별로 누구의 로그인지 연결
	// fetch=FetchType.LAZY-> 유저 정보가 필요할때만 불러옴(매번 유저 테이블 데이터 다 가져오면 과부하)
	@JoinColumn(name="user_id",nullable=false)
	private User user; // 어떤 유저의 포인트 인지 연결(join)
	
	
	@Column(nullable=false)
	private String activity; // 활동 타입
	
	
	@Column(nullable=false)
	private Integer amount;  // 점수 계산
	
	
	@CreationTimestamp  // 이력이 저장되는 순간의 날짜/시간을 자동으로 기록
	@Column(nullable=false, updatable=false)
	private LocalDateTime createdAt;
	// LocalDateTime -> 로컬의 날짜와 시간을 동시에 저장하는 타입
	
	
}
