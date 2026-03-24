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

    /* [1] 게시판 목록 조회 및 검색 */
    @GetMapping("/board")
    public String board(
            @RequestParam(value = "keyword", required = false) String keyword,
            Model model) {

        List<Community> list;

        // 검색어가 없으면 전체 목록을 최신순으로 가져옴
        if (keyword == null || keyword.trim().isEmpty()) {
            list = repository.findAllByOrderByCreatedAtDesc();
        } 
        // 검색어가 있으면 제목(Title)에 키워드가 포함된 글만 최신순으로 가져옴
        else {
            list = repository.findByTitleContainingOrderByCreatedAtDesc(keyword.trim());
        }

        model.addAttribute("list", list);    // 게시글 목록 전달
        model.addAttribute("keyword", keyword); // 검색창에 입력했던 단어 유지용

        return "board";
    }

    /* [2] 게시글 상세 보기 (조회수 증가 + 댓글 목록 불러오기) */
    @GetMapping("/post/{id}")
    public String postDetail(@PathVariable("id") Integer id, Model model) {

        // 해당 ID의 게시글 찾기 (없으면 목록으로 튕겨냄)
        Community post = repository.findById(id).orElse(null);
        if (post == null) return "redirect:/board";

        // 2-1. 상세 페이지 진입 시 조회수(ViewCount) 1 증가 후 저장
        post.setViewCount(post.getViewCount() + 1);
        repository.save(post);

        // 2-2. 해당 게시글에 달린 댓글들 가져오기 (작성순)
        List<Comment> commentList = commentRepository.findByBoardIdOrderByCreatedAtAsc(id);

        // 2-3. 실제 DB의 댓글 수와 게시글 객체의 댓글 수 동기화 (정확한 카운트 보정)
        int replyCount = commentRepository.countByBoardId(id);
        if (post.getReplyCount() == null || post.getReplyCount() != replyCount) {
            post.setReplyCount(replyCount);
            repository.save(post);
        }

        model.addAttribute("post", post);
        model.addAttribute("commentList", commentList);

        return "post";
    }

    /* [3] 게시글 추천(좋아요) 처리 */
    @PostMapping("/post/like")
    public String likePost(@RequestParam("boardId") Integer boardId) {

        Community post = repository.findById(boardId).orElse(null);

        if (post != null) {
            // 기존 좋아요가 null이면 0으로 처리, 아니면 +1 증가
            int currentLike = (post.getLikeCount() == null) ? 0 : post.getLikeCount();
            post.setLikeCount(currentLike + 1);
            repository.save(post);
        }

        // 추천 완료 후 다시 보던 상세 페이지로 이동
        return "redirect:/post/" + boardId;
    }
}