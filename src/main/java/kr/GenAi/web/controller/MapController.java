package kr.GenAi.web.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpSession; // 세션 확인을 위해 추가
import kr.GenAi.web.Entity.Location;
import kr.GenAi.web.dto.MapDto;
import kr.GenAi.web.repository.LocationRepository;

@Controller
public class MapController {

    @Value("${kakao.map.javascript-key}")
    private String kakaoMapKey;

    @Autowired
    private LocationRepository locationRepository;

    /**
     * [기능 1] 지도 페이지 이동 (권한 체크 포함)
     * - 세션에 'loginMem' 정보가 없으면 로그인 페이지로 튕겨냅니다.
     */
    @GetMapping("/map")
    public String showMapPage(HttpSession session, Model model) {
        
        // 1. 로그인 체크: 세션에 로그인 정보가 있는지 확인
        if (session.getAttribute("loginMem") == null) {
            // 로그인 안 되어 있으면 로그인 페이지로 강제 리다이렉트
            return "redirect:/login";
        }

        // 2. 로그인된 사용자에게만 API 키를 전달하고 지도 페이지를 보여줌
        model.addAttribute("kakaoMapKey", kakaoMapKey);
        return "map"; 
    }

    /**
     * [기능 2] 모든 위치 데이터를 JSON으로 반환 (REST API)
     * - JS의 fetch('/api/locations')가 호출하는 부분입니다.
     */
    @GetMapping("/api/locations")
    @ResponseBody 
    public List<MapDto> getLocations() {
        
        // DB에서 위치 엔티티 리스트 조회
        List<Location> entityList = locationRepository.findAll();

        // 엔티티 -> DTO 변환 (화면에서 쓰기 편하게 가공)
        return entityList.stream()
                .map(loc -> new MapDto(
                        loc.getLocName(),       
                        loc.getAddress(),       
                        loc.getRecycleItems(),  
                        loc.getLat(),           
                        loc.getLon()            // DB 컬럼명이 lon인 것 확인됨
                ))
                .collect(Collectors.toList());
    }
}