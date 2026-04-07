package kr.GenAi.web.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import kr.GenAi.web.Entity.Comment;

public interface CommentRepository extends JpaRepository<Comment, Integer> {

    // 댓글 목록 조회
    List<Comment> findByCommunity_BoardIdxOrderByCreatedAtAsc(Integer boardIdx);

    // 댓글 개수 조회
    int countByCommunity_BoardIdx(Integer boardIdx);

    // =========================================================
    // 게시글 삭제 시 해당 게시글의 댓글 전체 삭제
    // FK 제약 때문에 게시글보다 먼저 삭제해야 함
    // =========================================================
    @Transactional
    void deleteByCommunity_BoardIdx(Integer boardIdx);
}