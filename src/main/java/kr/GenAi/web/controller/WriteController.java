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

    /* 게시글 작성 */
    @PostMapping("/write")
    public String writePost(Community community,
                            MultipartFile imageFile,
                            HttpSession session) throws IOException {

        User loginMem = (User) session.getAttribute("loginMem");

        if (loginMem == null) {
            return "redirect:/login";
        }

        /* 작성자 아이디 자동 저장 */
        community.setUserId(loginMem.getId());

        /* 기본값 세팅 */
        if (community.getViewCount() == null) {
            community.setViewCount(0);
        }

        if (community.getLikeCount() == null) {
            community.setLikeCount(0);
        }

        if (community.getReplyCount() == null) {
            community.setReplyCount(0);
        }

        /* 이미지 업로드 처리 */
        if (imageFile != null && !imageFile.isEmpty()) {

            String uploadDir = "C:/recyq_upload/";

            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            String originalName = imageFile.getOriginalFilename();
            String ext = originalName.substring(originalName.lastIndexOf("."));
            String savedName = UUID.randomUUID().toString() + ext;

            File saveFile = new File(uploadDir + savedName);
            imageFile.transferTo(saveFile);

            /* DB에는 브라우저 접근용 경로 저장 */
            community.setImageUrl("/upload/" + savedName);
        }

        /* 게시글 저장 */
        communityRepository.save(community);

        return "redirect:/board";
    }
}