package kr.GenAi.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.stereotype.Controller;

import kr.GenAi.web.repository.UserRepository;

@Controller
public class UserController {

    @Autowired
    UserRepository repository;

    // 아이디 중복 체크용
    @GetMapping("/recyq/checkId")
    @ResponseBody
    public String checkId(String id){

        // null 방지
        if(id == null || id.trim().isEmpty()){
            return "fail";
        }

        // DB에 존재하면 fail, 없으면 ok
        if(repository.findById(id).isPresent()){
            return "fail";
        }else{
            return "ok";
        }
    }
}