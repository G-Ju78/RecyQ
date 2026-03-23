package kr.GenAi.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.servlet.http.HttpSession;
import kr.GenAi.web.Entity.User;
import kr.GenAi.web.repository.UserRepository;

@Controller
public class DBController {

	@Autowired
	UserRepository repository;
	
	
	
	// 회원가입
	@PostMapping("/joinProcess")
	public String joinProcess(User joinMem, String pwCheck) {

	    // 1. 비밀번호 확인 검사
	    if(!joinMem.getPw().equals(pwCheck)){
	        return "redirect:/join?error=pw";
	    }

	    // 2. 아이디 중복 검사
	    if(repository.findById(joinMem.getId()).isPresent()){
	        return "redirect:/join?error=id";
	    }

	    // 3. 비밀번호 조건 검사 (선택 but 추천)
	    if(!joinMem.getPw().matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$")){
	        return "redirect:/join?error=rule";
	    }

	    // 4. 저장 (암호화 없음)
	    repository.save(joinMem);

	    return "redirect:/main";
	}
	
	// 로그인
	@PostMapping("/loginProcess")
	public String loginProcess(User loinMem, HttpSession session) {
		
		User user = repository.findByIdAndPw
				(loinMem.getId(), loinMem.getPw());
		
		if(user!=null) {
			session.setAttribute("loginMem", user);
			return "redirect:/main";
		}else {
			return "redirect:/login?error=true";
		}
		
	}
	
	
	// 회원 정보 수정
	@PostMapping("/updateUser")
	public String updateUser(User updateMem, HttpSession session) {
		
		repository.save(updateMem);
		
		session.setAttribute("loginMem", updateMem);
		
		return "redirect:/main";
	}
	
	
	// 회원 정보 삭제
	@PostMapping("/deleteUser")
	public String deleteUser(HttpSession session) {
		User deleteMem = (User)session.getAttribute("loginMem");
		
		repository.deleteById(deleteMem.getId());
		
		session.invalidate();
		
		return "redirect:/main";
		
	}
	
	
	
}
