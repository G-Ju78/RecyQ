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

@Table(name="tb_user")
public class User{
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) // (자동생성, 증가)
	@Column(name="user_code")
	private Integer userCode; // 사용자 고유번호
	
	
	
	@Column(nullable=false, unique=true, length=100)
	private String id; // 아이디
	
	
	
	@Column(nullable=false, length=255)
	private String pw; // 비밀번호
	
	
	
	@Column(nullable=false, length=20)
	private String phone; // 연락처
	
	
	
	@Column(name="total_point", columnDefinition = "int default 0")
	private Integer totalPoint = 0; // 총 점수
	// columnDefinition: DB 테이블에서 기본값 0으로 세팅
	// = 0: -> 자바에서 객체가 생성될 때, 0점부터 시작
	
	
	
	@Column(name="joined_at", updatable=false)
	@CreationTimestamp
	private LocalDateTime joinedAt; // 가입 일자
	
}
