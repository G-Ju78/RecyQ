package kr.GenAi.web.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import kr.GenAi.web.Entity.Comment;
import kr.GenAi.web.Entity.Community;
import kr.GenAi.web.repository.CommentRepository;
import kr.GenAi.web.repository.CommunityRepository;

@Controller
public class BoardController {

    @Autowired
    private CommunityRepository repository;

    @Autowired
    private CommentRepository commentRepository;

    /* 게시판 목록 + 검색 기능 */
    @GetMapping("/board")
    public String board(
            @RequestParam(value = "keyword", required = false) String keyword,
            Model model) {

        List<Community> list;

        if (keyword == null || keyword.trim().isEmpty()) {
            list = repository.findAllByOrderByCreatedAtDesc();
        } else {
            list = repository.findByTitleContainingOrderByCreatedAtDesc(keyword.trim());
        }

        model.addAttribute("list", list);
        model.addAttribute("keyword", keyword);

        return "board";
    }

    /* 게시글 상세 + 조회수 증가 + 댓글 목록 */
    @GetMapping("/post/{id}")
    public String postDetail(@PathVariable("id") Integer id, Model model) {

        Community post = repository.findById(id).orElse(null);

        if (post == null) {
            return "redirect:/board";
        }

        /* 조회수 증가 */
        post.setViewCount(post.getViewCount() + 1);
        repository.save(post);

        /* 댓글 목록 */
        List<Comment> commentList = commentRepository.findByBoardIdOrderByCreatedAtAsc(id);

        /* 실제 댓글 개수 동기화 */
        int replyCount = commentRepository.countByBoardId(id);
        if (post.getReplyCount() == null || post.getReplyCount() != replyCount) {
            post.setReplyCount(replyCount);
            repository.save(post);
        }

        model.addAttribute("post", post);
        model.addAttribute("commentList", commentList);

        return "post";
    }

    /* 게시글 추천 기능 */
    @PostMapping("/post/like")
    public String likePost(@RequestParam("boardId") Integer boardId) {

        Community post = repository.findById(boardId).orElse(null);

        if (post != null) {
            int currentLike = (post.getLikeCount() == null) ? 0 : post.getLikeCount();
            post.setLikeCount(currentLike + 1);
            repository.save(post);
        }

        return "redirect:/post/" + boardId;
    }
}