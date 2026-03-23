package kr.GenAi.web.Entity;



import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

@EntityListeners(AuditingEntityListener.class)
// 작성 시간 자동 기록용

@Entity
@Table(name = "tb_community")
public class Community {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) // (자동생성, 증가)
	@Column(name = "board_id")
	private Integer boardId; // 사용자 고유번호

	@Column(length = 255, nullable = false)
	private String title;

	
	@Column(columnDefinition = "TEXT", nullable = false) // 내용 (긴 문장 저장용)
	private String content; // 게시글 내용

	@Column(name = "image_url", length = 500) // 이미지 경로
	private String imageUrl; // 이미지 경로

	// image_21.png의 설계대로 기본값을 0으로 설정
	@Column(name = "view_count", nullable = false)
	private Integer viewCount = 0; // 조회수

	@Column(name = "like_count", nullable = false)
	private Integer likeCount = 0; // 좋아요 수

	@Column(name = "reply_count", nullable = false)
	private Integer replyCount = 0; // 댓글 수

	@Column(name = "created_at", updatable = false)
	@CreationTimestamp
	private LocalDateTime createdAt; // 가입 일자
	
	@Column(name = "user_id", length = 50, nullable = false)
	private String userId; // 작성자 id
} 
