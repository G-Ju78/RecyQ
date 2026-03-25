package kr.GenAi.web.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.GenAi.web.Entity.Comment;

public interface CommentRepository extends JpaRepository<Comment, Integer> {

    /* 
     * [중요]
     * Comment 엔티티에는 boardIdx가 직접 없음!
     * -> Comment 안에 있는 community 객체를 타고 들어가야 함
     * -> community.boardIdx 기준으로 조회
     */

    // 특정 게시글(boardIdx)에 달린 댓글 목록 조회 (작성순)
    List<Comment> findByCommunity_BoardIdxOrderByCreatedAtAsc(Integer boardIdx);

    // 특정 게시글(boardIdx)의 댓글 개수 조회
    int countByCommunity_BoardIdx(Integer boardIdx);
}