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

    /* 댓글 작성 */
    @PostMapping("/comment/write")
    public String writeComment(Comment comment, HttpSession session) {

        User loginMem = (User) session.getAttribute("loginMem");

        if (loginMem == null) {
            return "redirect:/login";
        }

        /* 작성자 아이디 자동 저장 */
        comment.setUserId(loginMem.getId());

        /* 댓글 저장 */
        commentRepository.save(comment);

        /* 게시글 댓글 수 증가 */
        Community post = communityRepository.findById(comment.getBoardId()).orElse(null);
        if (post != null) {
            int currentReply = (post.getReplyCount() == null) ? 0 : post.getReplyCount();
            post.setReplyCount(currentReply + 1);
            communityRepository.save(post);
        }

        return "redirect:/post/" + comment.getBoardId();
    }

    /* 댓글 추천 */
    @PostMapping("/comment/like")
    public String likeComment(
            @RequestParam("commentId") Integer commentId,
            @RequestParam("boardId") Integer boardId) {

        Comment comment = commentRepository.findById(commentId).orElse(null);

        if (comment != null) {
            int currentLike = (comment.getLikeCount() == null) ? 0 : comment.getLikeCount();
            comment.setLikeCount(currentLike + 1);
            commentRepository.save(comment);
        }

        return "redirect:/post/" + boardId;
    }
}