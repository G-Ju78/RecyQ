package kr.GenAi.web.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.GenAi.web.Entity.Comment;

public interface CommentRepository extends JpaRepository<Comment, Integer> {

    /* [1] 특정 게시글의 댓글 목록 조회                             */
    /* - Comment 엔티티 안의 community.boardIdx 기준                */
    /* - 작성 시간 오름차순                                         */
    List<Comment> findByCommunity_BoardIdxOrderByCreatedAtAsc(Integer boardIdx);


    /* [2] 특정 게시글의 댓글 개수 조회                              */
    int countByCommunity_BoardIdx(Integer boardIdx);

    /* [3] 특정 게시글의 댓글 전체 삭제                              */
    /* - 게시글 삭제 전에 사용                                       */
    void deleteByCommunity_BoardIdx(Integer boardIdx);
}