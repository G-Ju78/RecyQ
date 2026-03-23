package kr.GenAi.web.Entity;



import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity

@Table(name="CUSTOMER")
public class User{
	
	@Id
	@Column(length=100)
	private String id; // 아이디
	
	@Column(nullable=false, length=255)
	private String pw; // 비밀번호
	
	@Column(nullable=true)
	private Integer scan=0; // 스캔이력
	
	@Column(nullable=true)
	private Integer point=0; // 총 점수
	
	@Column(nullable=true)
	private String locate; // 유저의 현 위치
	
	

	
}
