package kr.GenAi.web.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.GenAi.web.Entity.Comment;

public interface CommentRepository extends JpaRepository<Comment, Integer> {

    /* 게시글 번호로 댓글 조회 */
    List<Comment> findByBoardIdOrderByCreatedAtAsc(Integer boardId);

    /* 게시글 번호로 댓글 개수 조회 */
    int countByBoardId(Integer boardId);
}