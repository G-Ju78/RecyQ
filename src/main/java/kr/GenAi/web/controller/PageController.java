package kr.GenAi.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

//페이지 이동용 파일

@Controller
public class PageController {

    @GetMapping("/login")
    public String login() {
        return "login";
    }
    @GetMapping("/join")
    public String join() {
        return "join";
    }
    @GetMapping("/start")
    public String start() {
    	return "start";
    }
    @GetMapping("/main")
    public String main() {
    	return "main";
    }
    @GetMapping("/updateUser")
    public String updateUser() {
    	return "updateUser";
    }
    @GetMapping("/chatbot")
    public String chatbot() {
        return "chatbot";
    }
    @GetMapping("/mypage")
    public String mypage() {
        return "mypage";
    }
    @GetMapping("/pointList")
    public String pointList() {
    	return "pointList";
    }
    @GetMapping("/write")
    public String write() {
        return "write";
    }
    
}