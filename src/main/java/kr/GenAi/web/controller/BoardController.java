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

    /* ============================= */
    /* [1] 게시판 목록 조회 + 검색 기능 */
    /* ============================= */
    @GetMapping("/board")
    public String board(
            @RequestParam(value = "keyword", required = false) String keyword,
            Model model) {

        List<Community> list;

        // 1-1. 검색어가 없으면 전체 글을 최신순으로 조회
        if (keyword == null || keyword.trim().isEmpty()) {
            list = repository.findAllByOrderByCreatedAtDesc();
        }
        // 1-2. 검색어가 있으면 제목에 해당 단어가 포함된 글만 최신순 조회
        else {
            list = repository.findByTitleContainingOrderByCreatedAtDesc(keyword.trim());
        }

        // 1-3. 화면으로 게시글 목록과 검색어 전달
        model.addAttribute("list", list);
        model.addAttribute("keyword", keyword);

        return "board";
    }

    /* ========================================= */
    /* [2] 게시글 상세 보기 (조회수 증가 + 댓글 조회) */
    /* ========================================= */
    @GetMapping("/post/{id}")
    public String postDetail(@PathVariable("id") Integer id, Model model) {

        // 2-1. 게시글 번호(id)로 게시글 조회
        Community post = repository.findById(id).orElse(null);

        // 게시글이 없으면 게시판 목록으로 이동
        if (post == null) {
            return "redirect:/board";
        }

        // 2-2. 상세 페이지 들어오면 조회수 +1
        int currentView = (post.getViewCount() == null) ? 0 : post.getViewCount();
        post.setViewCount(currentView + 1);
        repository.save(post);

        /*
         * 2-3. 해당 게시글의 댓글 목록 조회
         * 
         * [중요]
         * 예전에는 Comment 엔티티에 boardId 필드가 있었을 수 있지만,
         * 지금은 Comment 안에 Community community 필드가 있음
         * 따라서 Repository 메서드도
         * findByBoardId... 가 아니라
         * findByCommunity_BoardIdx... 로 불러야 함
         */
        List<Comment> commentList = commentRepository.findByCommunity_BoardIdxOrderByCreatedAtAsc(id);

        /*
         * 2-4. 실제 댓글 수 조회
         * 
         * 이것도 마찬가지로 countByBoardId 가 아니라
         * countByCommunity_BoardIdx 로 바뀌어야 함
         */
        int replyCount = commentRepository.countByCommunity_BoardIdx(id);

        // 2-5. 게시글 객체에 저장된 댓글 수와 실제 댓글 수가 다르면 보정
        if (post.getReplyCount() == null || !post.getReplyCount().equals(replyCount)) {
            post.setReplyCount(replyCount);
            repository.save(post);
        }

        // 2-6. 화면으로 게시글 정보와 댓글 목록 전달
        model.addAttribute("post", post);
        model.addAttribute("commentList", commentList);

        return "post";
    }

    /* ============================= */
    /* [3] 게시글 좋아요(추천) 기능 */
    /* ============================= */
    @PostMapping("/post/like")
    public String likePost(@RequestParam("boardId") Integer boardId) {

        // 3-1. 게시글 조회
        Community post = repository.findById(boardId).orElse(null);

        if (post != null) {
            // 3-2. 현재 좋아요 수 가져오기 (null 방어 포함)
            int currentLike = (post.getLikeCount() == null) ? 0 : post.getLikeCount();

            // 3-3. 좋아요 수 +1 후 저장
            post.setLikeCount(currentLike + 1);
            repository.save(post);
        }

        // 3-4. 다시 해당 게시글 상세 페이지로 이동
        return "redirect:/post/" + boardId;
    }
}