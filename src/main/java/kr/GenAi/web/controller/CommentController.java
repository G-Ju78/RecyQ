package kr.GenAi.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import kr.GenAi.web.Entity.Comment;
import kr.GenAi.web.Entity.Community;
import kr.GenAi.web.Entity.User;
import kr.GenAi.web.repository.CommentRepository;
import kr.GenAi.web.repository.CommunityRepository;



@Controller
public class CommentController {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private CommunityRepository communityRepository;

    /* [1] 댓글 작성 로직 */
    @PostMapping("/comment/write")
    public String writeComment(Comment comment, HttpSession session) {

        // 1-1. 세션에서 로그인 정보 가져오기 (비로그인 시 로그인 페이지로)
        User loginMem = (User) session.getAttribute("loginMem");
        if (loginMem == null) {
            return "redirect:/login";
        }

        // 1-2. 현재 로그인한 유저의 ID를 댓글 작성자로 자동 설정
        comment.setUserId(loginMem.getId());

        // 1-3. 댓글 데이터를 DB에 저장 (Insert)
        commentRepository.save(comment);

        // 1-4. [게시글 테이블]의 댓글 수(ReplyCount) 컬럼 업데이트
        // 댓글이 달린 게시글을 찾아와서 기존 댓글 수 + 1을 해줌
        Community post = communityRepository.findById(comment.getBoardId()).orElse(null);
        if (post != null) {
            int currentReply = (post.getReplyCount() == null) ? 0 : post.getReplyCount();
            post.setReplyCount(currentReply + 1);
            communityRepository.save(post); // 변경된 개수 반영
        }

        // 작성 후 다시 해당 게시글 상세 페이지로 돌아감
        return "redirect:/post/" + comment.getBoardId();
    }

    /* [2] 댓글 추천(좋아요) 기능 */
    @PostMapping("/comment/like")
    public String likeComment(
            @RequestParam("commentId") Integer commentId,
            @RequestParam("boardId") Integer boardId) {

        // 2-1. ID로 추천할 댓글 찾기
        Comment comment = commentRepository.findById(commentId).orElse(null);

        if (comment != null) {
            // 2-2. 기존 좋아요 수에 +1 증가 (null 체크 포함)
            int currentLike = (comment.getLikeCount() == null) ? 0 : comment.getLikeCount();
            comment.setLikeCount(currentLike + 1);
            commentRepository.save(comment);
        }

        // 추천 완료 후 보고 있던 게시글 상세 페이지로 리다이렉트
        return "redirect:/post/" + boardId;
    }
}