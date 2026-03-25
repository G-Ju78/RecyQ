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
@Table(name="tb_point_log")
public class PointLog {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) // 로그 고유 번호(1,2,3..) 자동 증가
	@Column(name="point_idx")
	private Integer pointIdx; // 재활용 고유 번호
	
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="loc_idx",nullable=true)
	private Location location;
	
	
	@ManyToOne(fetch=FetchType.LAZY) 
	// ManyToOne -> 유저별로 누구의 로그인지 연결(여러 재활용 기록이 한 명의 유저에 속함)
	// fetch=FetchType.LAZY-> 유저 정보가 필요할때만 불러옴(매번 유저 테이블 데이터 다 가져오면 과부하)
	@JoinColumn(name="user_code",nullable=false)
	private User user; // 어떤 유저의 포인트 인지 연결(join)
	
	
	@Column(name="log_type", length=100, nullable=false)
	private String logType; // 활동 내역 (RECYCLE, QUIZ, SHOP_BUY 등)
	
	
	@Column(name="log_detail", length=255, nullable=false)
	private String logDetail; // 획득 포인트 상세 내역
	
	
	@Column(name="rec_point", nullable=false)
	private Integer recPoint;  // 점수, 변동 포인트 
	// 적립은 +, 사용은 -
	
	
	@Column(name="created_at", updatable=false)
	@CreationTimestamp
	private LocalDateTime createdAt; // 기록 시간
	
	
	
}
