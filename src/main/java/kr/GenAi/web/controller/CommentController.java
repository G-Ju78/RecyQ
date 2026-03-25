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

    /* ============================= */
    /* [1] 댓글 작성 기능 */
    /* ============================= */
    @PostMapping("/comment/write")
    public String writeComment(
            @RequestParam("boardId") Integer boardId,   // 게시글 번호 받기
            @RequestParam("content") String content,    // 댓글 내용 받기
            HttpSession session) {

        // 1. 로그인 정보 가져오기
        User loginMem = (User) session.getAttribute("loginMem");

        // 로그인 안 되어 있으면 로그인 페이지로 이동
        if (loginMem == null) {
            return "redirect:/login";
        }

        // 2. 게시글 찾기
        Community post = communityRepository.findById(boardId).orElse(null);

        // 게시글 없으면 목록으로 이동
        if (post == null) {
            return "redirect:/board";
        }

        // 3. 댓글 객체 생성 및 값 세팅
        Comment comment = new Comment();

        comment.setContent(content);     // 댓글 내용
        comment.setUser(loginMem);       // 작성자 (User 객체)
        comment.setCommunity(post);      // 어떤 게시글인지 연결
        comment.setLikeCount(0);         // 초기 좋아요 0

        // 4. 댓글 저장
        commentRepository.save(comment);

        // 5. 게시글 댓글 수 증가
        int currentReply = (post.getReplyCount() == null) ? 0 : post.getReplyCount();
        post.setReplyCount(currentReply + 1);
        communityRepository.save(post);

        // 6. 다시 해당 게시글 상세 페이지로 이동
        return "redirect:/post/" + boardId;
    }

    /* ============================= */
    /* [2] 댓글 좋아요 기능 */
    /* ============================= */
    @PostMapping("/comment/like")
    public String likeComment(
            @RequestParam("commentId") Integer commentId,
            @RequestParam("boardId") Integer boardId) {

        // 1. 댓글 찾기
        Comment comment = commentRepository.findById(commentId).orElse(null);

        if (comment != null) {
            // 2. 좋아요 +1 증가 (null 방어 포함)
            int currentLike = (comment.getLikeCount() == null) ? 0 : comment.getLikeCount();
            comment.setLikeCount(currentLike + 1);

            // 3. 저장
            commentRepository.save(comment);
        }

        // 4. 다시 게시글 상세 페이지로 이동
        return "redirect:/post/" + boardId;
    }
}