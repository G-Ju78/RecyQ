package kr.GenAi.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import kr.GenAi.web.Entity.User;
import kr.GenAi.web.repository.UserRepository;

@Controller
public class UserController {

	@Autowired
	UserRepository repository;
	

	// 회원가입
	@PostMapping("/joinProcess")
	public String joinProcess(User joinMem, @RequestParam("pwCheck") String pwCheck) {
		
		if(joinMem.getPw() == null || !joinMem.getPw().equals(pwCheck)) {
			return "join";
			
		}
		repository.save(joinMem);
		return "redirect:/main";
		
	}
	
	
	// 로그인
	@PostMapping("/loginProcess")
	public String loginProcess(User loginMem, HttpSession session) {
		
		User user = repository.findByIdAndPw
				(loginMem.getId(), loginMem.getPw());
		
		if(user!=null) {
			session.setAttribute("loginMem", user);
			return "redirect:/main";
		}else {
			return "redirect:/login?error=true";
		}
		
	}
	
	
	// 회원 정보 수정(아이디는 고정, 비밀번호만 수정)
	@PostMapping("/updateUser")
	public String updateUser(User updateMem, HttpSession session) {
		
		User loginMem = (User) session.getAttribute("loginMem");
		// 다른 사람의 아이디로 비밀번호를 수정 할 수 있기 때문에 세션에 담긴 정보를 기준으로
		// 수정하기 위해 loginMem 세션을 가져옴
		
		
		User changePw = repository.findById(loginMem.getUserCode()).orElse(null);
		// DB에서 고유번호(UserCode)에 맞는 모든 정보 가져와서 유저타입의 changePw에 담아줌
		// 이때 findById로 가져온 데이터는 Optional 타입이라 서로 타입이 다름
		// 이를 해결하기 위해 orElse(null) 사용
		
		
		if(changePw!=null) {
			changePw.setPw(updateMem.getPw());
			
			repository.save(changePw);
			
			session.setAttribute("loginMem", changePw);
		}
		
		return "redirect:/main";
	}
	
	
	// 회원 정보 삭제
	@PostMapping("/deleteUser")
	public String deleteUser(HttpSession session) {
		User deleteMem = (User)session.getAttribute("loginMem");
		
		
		if(deleteMem!=null) {
			repository.deleteById(deleteMem.getUserCode());
			
			session.invalidate();
		}
		
		
		return "redirect:/main";
		
	}
	
	
}
