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
@Entity
@Table(name = "tb_comment")
public class Comment {

    /* 댓글 고유번호 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Integer commentId;

    /* 게시글 번호 */
    @Column(name = "board_id", nullable = false)
    private Integer boardId;

    /* 작성자 아이디 */
    @Column(name = "user_id", length = 50, nullable = false)
    private String userId;

    /* 댓글 내용 */
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    /* 댓글 추천수 */
    @Column(name = "like_count", nullable = false)
    private Integer likeCount = 0;

    /* 작성일 */
    @Column(name = "created_at", updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
}