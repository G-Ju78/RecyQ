package kr.GenAi.web.controller;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpSession;
import kr.GenAi.web.Entity.Community;
import kr.GenAi.web.Entity.User;
import kr.GenAi.web.repository.CommunityRepository;

@Controller
public class WriteController {

    @Autowired
    private CommunityRepository communityRepository;

    /* 게시글 작성 처리 (POST 방식) */
    @PostMapping("/write")
    public String writePost(Community community,
                            MultipartFile imageFile,
                            HttpSession session) throws IOException {

        // 1. 세션에서 로그인한 유저 정보 가져오기
        User loginMem = (User) session.getAttribute("loginMem");

        // 2. 로그인 안 되어 있으면 로그인 페이지로 리다이렉트
        if (loginMem == null) {
            return "redirect:/login";
        }

        // 3. 현재 로그인한 유저를 게시글의 작성자로 설정
        community.setUser(loginMem);

        // 4. 조회수, 좋아요, 댓글 수 등 수치 데이터 기본값(0) 세팅
        if (community.getViewCount() == null) community.setViewCount(0);
        if (community.getLikeCount() == null) community.setLikeCount(0);
        if (community.getReplyCount() == null) community.setReplyCount(0);

        /* --- 이미지 업로드 처리 시작 --- */
        if (imageFile != null && !imageFile.isEmpty()) {

            // 실제 파일이 저장될 물리적 경로 설정
            String uploadDir = "C:/recyq_upload/";

            // 폴더가 없으면 생성
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // 파일명 중복 방지를 위해 UUID 생성 및 확장자 추출
            String originalName = imageFile.getOriginalFilename();
            String ext = "";

            if (originalName != null && originalName.contains(".")) {
                ext = originalName.substring(originalName.lastIndexOf("."));
            }
            
            // 설정한 경로에 실제 파일 저장
            String savedName = UUID.randomUUID().toString() + ext;

            File saveFile = new File(uploadDir, savedName);
            imageFile.transferTo(saveFile);

         // DB에는 /upload/파일명 형태로 저장
            community.setImageUrl("/upload/" + savedName);
        }
        /* --- 이미지 업로드 처리 종료 --- */

        // 5. 모든 데이터 세팅 완료된 게시글 객체를 DB에 저장
        communityRepository.save(community);

        // 6. 작성 완료 후 게시판 목록 페이지로 이동
        return "redirect:/board";
    }
}