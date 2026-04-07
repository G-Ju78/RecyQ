package kr.GenAi.web.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import kr.GenAi.web.Entity.Comment;
import kr.GenAi.web.Entity.Community;
import kr.GenAi.web.Entity.User;
import kr.GenAi.web.repository.CommentRepository;
import kr.GenAi.web.repository.CommunityRepository;

@Controller
public class BoardController {

	// 게시글 관련 DB 작업용 Repository
	@Autowired
	private CommunityRepository repository;

	// 댓글 관련 DB 작업용 Repository
	@Autowired
	private CommentRepository commentRepository;

	/* ========================================================= */
	/* [1] 게시판 목록 페이지 */
	/* - 검색어가 없으면 전체 글 최신순 */
	/* - 검색어가 있으면 제목 검색 결과 최신순 */
	/* ========================================================= */
	@GetMapping("/board")
	public String board(@RequestParam(value = "keyword", required = false) String keyword, Model model) {

		// 화면에 뿌릴 게시글 목록 변수
		List<Community> list;

		// 검색어가 비어 있으면 전체 목록 조회
		if (keyword == null || keyword.trim().isEmpty()) {
			list = repository.findAllByOrderByCreatedAtDesc();
		} else {
			// 검색어가 있으면 제목에 해당 문자열이 포함된 글만 조회
			list = repository.findByTitleContainingOrderByCreatedAtDesc(keyword.trim());
		}

		// HTML(Thymeleaf) 쪽으로 데이터 전달
		model.addAttribute("list", list);
		model.addAttribute("keyword", keyword);

		return "board";
	}

	/* ========================================================= */
	/* [2] 게시글 상세 페이지 */
	/* - 게시글 조회 */
	/* - 조회수 증가 */
	/* - 댓글 목록 조회 */
	/* - 로그인한 사용자가 글 작성자인지 판별 */
	/* - 로그인한 사용자의 userCode를 화면으로 전달 */
	/* ========================================================= */
	@GetMapping("/post/{id}")
	public String postDetail(@PathVariable("id") Integer id, Model model, HttpSession session) {

		// 1. 게시글 번호(board_idx)로 게시글 조회
		Community post = repository.findById(id).orElse(null);

		// 2. 게시글이 존재하지 않으면 목록으로 돌려보냄
		if (post == null) {
			return "redirect:/board";
		}

		// 3. 조회수 증가
		// null인 경우도 대비해서 0부터 시작하도록 처리
		int currentView = (post.getViewCount() == null) ? 0 : post.getViewCount();
		post.setViewCount(currentView + 1);

		// 증가된 조회수 저장
		repository.save(post);

		// 4. 해당 게시글의 댓글 목록 조회
		// Comment 엔티티 안에 community가 있고
		// 그 안의 boardIdx 기준으로 찾아오는 구조
		List<Comment> commentList = commentRepository.findByCommunity_BoardIdxOrderByCreatedAtAsc(id);

		// 5. 실제 댓글 개수 조회
		int replyCount = commentRepository.countByCommunity_BoardIdx(id);

		// 6. 게시글 테이블의 reply_count 값과 실제 댓글 수가 다르면 보정
		if (post.getReplyCount() == null || !post.getReplyCount().equals(replyCount)) {
			post.setReplyCount(replyCount);
			repository.save(post);
		}

		// 7. 세션에서 로그인한 사용자 정보 꺼내기
		User loginMem = (User) session.getAttribute("loginMem");

		// 8. 로그인한 사용자가 이 게시글의 작성자인지 판별할 변수
		boolean isOwner = false;

		// 9. 로그인한 상태이고, 게시글 작성자 정보도 있으면 작성자 여부 비교
		if (loginMem != null && post.getUser() != null && post.getUser().getUserCode() != null) {
			isOwner = post.getUser().getUserCode().equals(loginMem.getUserCode());
		}

		// 10. 댓글 삭제 버튼 표시를 위해 로그인 유저의 userCode도 전달
		Integer loginUserCode = null;
		if (loginMem != null) {
			loginUserCode = loginMem.getUserCode();
		}

		// 11. 화면으로 데이터 전달
		model.addAttribute("post", post); // 게시글 정보
		model.addAttribute("commentList", commentList); // 댓글 목록
		model.addAttribute("isOwner", isOwner); // 본인 글 여부
		model.addAttribute("loginUserCode", loginUserCode); // 로그인 유저 코드

		return "post";
	}

	/* ========================================================= */
	/* [3] 게시글 좋아요(추천) */
	/* ========================================================= */
	@PostMapping("/post/like")
	public String likePost(@RequestParam("boardId") Integer boardId) {

		// 1. 게시글 조회
		Community post = repository.findById(boardId).orElse(null);

		// 2. 게시글이 존재하면 좋아요 수 증가
		if (post != null) {
			int currentLike = (post.getLikeCount() == null) ? 0 : post.getLikeCount();
			post.setLikeCount(currentLike + 1);
			repository.save(post);
		}

		// 3. 다시 해당 게시글 상세 페이지로 이동
		return "redirect:/post/" + boardId;
	}

	/* ========================================================= */
	/* [4] 게시글 수정 페이지 진입 */
	/* - 로그인한 사용자만 가능 */
	/* - 그리고 본인 글일 때만 가능 */
	/* - 수정 화면은 기존 write.html을 재사용 */
	/* ========================================================= */
	@GetMapping("/post/edit/{id}")
	public String editPostPage(@PathVariable("id") Integer id, Model model, HttpSession session) {

		// 1. 로그인 정보 확인
		User loginMem = (User) session.getAttribute("loginMem");

		// 로그인 안 되어 있으면 로그인 페이지로 이동
		if (loginMem == null) {
			return "redirect:/login";
		}

		// 2. 수정할 게시글 조회
		Community post = repository.findById(id).orElse(null);

		// 게시글이 없으면 목록으로 이동
		if (post == null) {
			return "redirect:/board";
		}

		// 3. 본인 글인지 검사
		// 작성자가 아니면 수정 페이지 못 들어가게 막음
		if (post.getUser() == null || !post.getUser().getUserCode().equals(loginMem.getUserCode())) {
			return "redirect:/post/" + id;
		}

		// 4. write.html 에서 수정 모드로 동작하게 값 전달
		model.addAttribute("editMode", true);
		model.addAttribute("postData", post);

		return "write";
	}

	/* [5] 게시글 삭제 */
	/* - 로그인한 사용자만 가능 */
	/* - 본인 글일 때만 삭제 가능 */
	/* - 게시글 삭제 전 댓글 전체 먼저 삭제 */
	@PostMapping("/post/delete")
	public String deletePost(@RequestParam("boardId") Integer boardId, HttpSession session) {

	    // 1. 로그인 사용자 확인
	    User loginMem = (User) session.getAttribute("loginMem");

	    if (loginMem == null) {
	        return "redirect:/login";
	    }

	    // 2. 삭제할 게시글 조회
	    Community post = repository.findById(boardId).orElse(null);

	    // 게시글이 없으면 목록으로 이동
	    if (post == null) {
	        return "redirect:/board";
	    }

	    // 3. 본인 글인지 확인
	    if (post.getUser() == null || !post.getUser().getUserCode().equals(loginMem.getUserCode())) {
	        return "redirect:/post/" + boardId;
	    }

	    // 4. 댓글 먼저 삭제
	    // FK 제약 때문에 댓글이 남아 있으면 게시글 삭제가 안 될 수 있음
	    commentRepository.deleteByCommunity_BoardIdx(boardId);

	    // 5. 게시글 삭제
	    repository.delete(post);

	    // 6. 게시판 목록으로 이동
	    return "redirect:/board";
	}
}