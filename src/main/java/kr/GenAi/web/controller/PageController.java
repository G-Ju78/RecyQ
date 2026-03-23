package kr.GenAi.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;


@Controller
public class PageController {

	// 메인 페이지 이동
	@GetMapping("/main")
	public String Home() {
		return "main";
	}
	
	// 로그인 페이지 이동
	@GetMapping("/login")
	public String goLogin() {
		return "login";
	}
	
	// 로그아웃 시 메인 페이지로 이동
	@GetMapping("/logOut")
	public String goLogOut(HttpSession session) {
		session.invalidate();
		return "redirect:/main";
	}
	
	// 회원가입 페이지 이동
	@GetMapping("/join")
	public String goJoin() {
		return "join";
	}
	
	
	// 회원정보 수정 페이지 이동
	@GetMapping("/updateUser")
	public String goUpdate(HttpSession session) {
		
		// 세션에 로그인 정보가 없으면 로그인 페이지로 이동
		if(session.getAttribute("loginMem")==null) {
			return "redirect:/login";
		}
		return "updateUser";
	}
	
	
	// 스캔 화면 이동
	@GetMapping("/scan")
	public String goScan(HttpSession session) {
		
		if(session.getAttribute("loginMem")==null) {
			return "redirect:/login";
		}	
		return "scan";
	}
	
	
}