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

	// 댓글 DB 작업용 Repository
	@Autowired
	private CommentRepository commentRepository;

	// 게시글 DB 작업용 Repository
	@Autowired
	private CommunityRepository communityRepository;

	/* ========================================================= */
	/* [1] 댓글 작성 */
	/* - 로그인한 사용자만 댓글 작성 가능 */
	/* - 작성 후 해당 게시글의 reply_count 증가 */
	/* ========================================================= */
	@PostMapping("/comment/write")
	public String writeComment(@RequestParam("boardId") Integer boardId, // 어떤 게시글에 댓글 다는지
			@RequestParam("content") String content, // 댓글 내용
			HttpSession session) {

		// 1. 세션에서 로그인 사용자 정보 가져오기
		User loginMem = (User) session.getAttribute("loginMem");

		// 로그인 안 되어 있으면 로그인 페이지로 이동
		if (loginMem == null) {
			return "redirect:/login";
		}

		// 2. 댓글을 달 게시글 찾기
		Community post = communityRepository.findById(boardId).orElse(null);

		// 게시글이 존재하지 않으면 게시판 목록으로 이동
		if (post == null) {
			return "redirect:/board";
		}

		// 3. 공백 댓글 방지
		if (content == null || content.trim().isEmpty()) {
			return "redirect:/post/" + boardId;
		}

		// 4. 댓글 객체 생성
		Comment comment = new Comment();

		// 5. 댓글 내용, 작성자, 어느 게시글 댓글인지 세팅
		comment.setContent(content.trim());
		comment.setUser(loginMem);
		comment.setCommunity(post);

		// 6. 좋아요 수 기본값 0
		comment.setLikeCount(0);

		// 7. 댓글 저장
		commentRepository.save(comment);

		// 8. 게시글 댓글 수(reply_count) 증가
		int currentReply = (post.getReplyCount() == null) ? 0 : post.getReplyCount();
		post.setReplyCount(currentReply + 1);
		communityRepository.save(post);

		// 9. 다시 게시글 상세 페이지로 이동
		return "redirect:/post/" + boardId;
	}

	/* ========================================================= */
	/* [2] 댓글 좋아요(추천) */
	/* ========================================================= */
	@PostMapping("/comment/like")
	public String likeComment(@RequestParam("commentId") Integer commentId, @RequestParam("boardId") Integer boardId) {

		// 1. 좋아요 누른 댓글 찾기
		Comment comment = commentRepository.findById(commentId).orElse(null);

		// 2. 댓글이 존재하면 좋아요 수 증가
		if (comment != null) {
			int currentLike = (comment.getLikeCount() == null) ? 0 : comment.getLikeCount();
			comment.setLikeCount(currentLike + 1);
			commentRepository.save(comment);
		}

		// 3. 다시 원래 게시글 상세 페이지로 복귀
		return "redirect:/post/" + boardId;
	}

	/* ========================================================= */
	/* [3] 댓글 한개 삭제 */
	/* - 로그인한 사용자만 가능 */
	/* - 본인 댓글일 때만 삭제 가능 */
	/* - 삭제 후 게시글 reply_count 감소 */
	/* ========================================================= */
	@PostMapping("/comment/delete")
	public String deleteComment(@RequestParam("commentId") Integer commentId, @RequestParam("boardId") Integer boardId,
			HttpSession session) {

		// 1. 로그인 사용자 정보 확인
		User loginMem = (User) session.getAttribute("loginMem");

		if (loginMem == null) {
			return "redirect:/login";
		}

		// 2. 삭제할 댓글 조회
		Comment comment = commentRepository.findById(commentId).orElse(null);

		// 댓글이 없으면 그냥 게시글 상세 페이지로 이동
		if (comment == null) {
			return "redirect:/post/" + boardId;
		}

		// 3. 댓글 작성자 확인
		// 본인 댓글이 아니면 삭제 못 하게 막음
		if (comment.getUser() == null || !comment.getUser().getUserCode().equals(loginMem.getUserCode())) {
			return "redirect:/post/" + boardId;
		}

		// 4. 연결된 게시글 객체 가져오기
		Community post = comment.getCommunity();

		// 5. 댓글 삭제
		commentRepository.delete(comment);

		// 6. 게시글의 댓글 수 감소
		if (post != null) {
			int currentReply = (post.getReplyCount() == null) ? 0 : post.getReplyCount();

			// 음수 방지
			if (currentReply > 0) {
				post.setReplyCount(currentReply - 1);
			} else {
				post.setReplyCount(0);
			}

			communityRepository.save(post);
		}

		// 7. 원래 게시글 상세 페이지로 복귀
		return "redirect:/post/" + boardId;
	}
}