package kr.GenAi.web.controller;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpSession;
import kr.GenAi.web.Entity.Community;
import kr.GenAi.web.Entity.User;
import kr.GenAi.web.repository.CommunityRepository;

@Controller
public class WriteController {

	// 게시글 DB 작업용 Repository
	@Autowired
	private CommunityRepository communityRepository;

	/* ========================================================= */
	/* [1] 게시글 작성 */
	/* - 로그인한 사용자만 작성 가능 */
	/* - 이미지 첨부 가능 */
	/* ========================================================= */
	@PostMapping("/write")
	public String writePost(Community community, MultipartFile imageFile, HttpSession session) throws IOException {

		// 1. 로그인 사용자 정보 가져오기
		User loginMem = (User) session.getAttribute("loginMem");

		// 로그인 안 되어 있으면 로그인 페이지로 이동
		if (loginMem == null) {
			return "redirect:/login";
		}

		// 2. 글 작성자를 현재 로그인한 사용자로 설정
		community.setUser(loginMem);

		// 3. 숫자 기본값 세팅
		if (community.getViewCount() == null)
			community.setViewCount(0);
		if (community.getLikeCount() == null)
			community.setLikeCount(0);
		if (community.getReplyCount() == null)
			community.setReplyCount(0);

		// 4. 이미지가 첨부된 경우 파일 저장 후 DB에 경로 저장
		if (imageFile != null && !imageFile.isEmpty()) {
			String imageUrl = saveImage(imageFile);
			community.setImageUrl(imageUrl);
		}

		// 5. 게시글 저장
		communityRepository.save(community);

		// 6. 게시판 목록으로 이동
		return "redirect:/board";
	}

	/* ========================================================= */
	/* [2] 게시글 수정 */
	/* - 본인 글만 수정 가능 */
	/* - 새 이미지 없으면 기존 이미지 유지 */
	/* ========================================================= */
	@PostMapping("/post/update")
	public String updatePost(@RequestParam("boardIdx") Integer boardIdx, @RequestParam("title") String title,
			@RequestParam("content") String content,
			@RequestParam(value = "existingImageUrl", required = false) String existingImageUrl,
			MultipartFile imageFile, HttpSession session) throws IOException {

		// 1. 로그인 사용자 확인
		User loginMem = (User) session.getAttribute("loginMem");

		if (loginMem == null) {
			return "redirect:/login";
		}

		// 2. 수정할 게시글 조회
		Community post = communityRepository.findById(boardIdx).orElse(null);

		if (post == null) {
			return "redirect:/board";
		}

		// 3. 본인 글만 수정 가능
		if (post.getUser() == null || !post.getUser().getUserCode().equals(loginMem.getUserCode())) {
			return "redirect:/post/" + boardIdx;
		}

		// 4. 제목/내용 수정
		post.setTitle(title);
		post.setContent(content);

		// 5. 새 이미지가 있으면 새 이미지 저장
		if (imageFile != null && !imageFile.isEmpty()) {
			String imageUrl = saveImage(imageFile);
			post.setImageUrl(imageUrl);
		} else {
			// 새 이미지가 없으면 기존 이미지 유지
			post.setImageUrl(existingImageUrl);
		}

		// 6. 수정 내용 저장
		communityRepository.save(post);

		// 7. 수정된 게시글 상세 페이지로 이동
		return "redirect:/post/" + boardIdx;
	}

	/* ========================================================= */
	/* [3] 이미지 저장 공통 메서드 */
	/* - 중복 파일명 방지를 위해 UUID 사용 */
	/* - 실제 저장 경로: C:/recyq_upload/ */
	/* - DB 저장 경로: /upload/파일명 */
	/* ========================================================= */
	private String saveImage(MultipartFile imageFile) throws IOException {

		// 실제 파일이 저장될 폴더 경로
		String uploadDir = "C:/recyq_upload/";

		// 폴더가 없으면 생성
		File dir = new File(uploadDir);
		if (!dir.exists()) {
			dir.mkdirs();
		}

		// 원본 파일명 가져오기
		String originalName = imageFile.getOriginalFilename();

		// 확장자 추출용 변수
		String ext = "";

		// 파일명에 . 이 있으면 확장자 추출
		if (originalName != null && originalName.contains(".")) {
			ext = originalName.substring(originalName.lastIndexOf("."));
		}

		// UUID를 붙여서 저장 파일명 생성
		String savedName = UUID.randomUUID().toString() + ext;

		// 실제 저장될 파일 객체 생성
		File saveFile = new File(uploadDir, savedName);

		// 파일 저장
		imageFile.transferTo(saveFile);

		// DB에는 웹에서 접근 가능한 경로로 저장
		return "/upload/" + savedName;
	}
}