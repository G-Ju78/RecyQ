package kr.GenAi.web.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpSession;
import kr.GenAi.web.Entity.Location;
import kr.GenAi.web.dto.MapDto;
import kr.GenAi.web.repository.LocationRepository;
import lombok.RequiredArgsConstructor;

/**
 * [MapController]
 * 지도 관련 페이지 이동(View)과 위치 데이터 제공(API)을 모두 담당합니다.
 */
@RequiredArgsConstructor // 생성자 주입을 위해 추가
@Controller
public class MapController {

    // 1. 카카오 지도 API 키 (application.properties에서 관리)
    @Value("${kakao.map.javascript-key}")
    private String kakaoMapKey;

    // 2. 위치 데이터 접근을 위한 리포지토리
    private final LocationRepository locationRepository;

    /**
     * [기능 1] 지도 페이지 이동 (권한 체크 및 키 전달)
     * - PageController에서 이관된 지도 이동 핵심 로직입니다.
     * - 세션에 'loginMem'이 없으면 접근을 차단합니다.
     */
    @GetMapping("/map")
    public String showMapPage(HttpSession session, Model model) {
        
        // 로그인 체크: 세션에 사용자 정보가 있는지 확인
        if (session.getAttribute("loginMem") == null) {
            return "redirect:/login"; // 로그인 안 되어 있으면 강제 이동
        }

        /** [중요] 타임리프(map.html)에서 사용할 카카오 맵 자바스크립트 키 전달 */
        model.addAttribute("kakaoMapKey", kakaoMapKey);
        
        return "map"; 
    }

    /**
     * [기능 2] 모든 위치 데이터를 JSON으로 반환 (REST API)
     * - map.html의 스크립트(fetch) 요청에 응답하여 마커 정보를 보냅니다.
     */
    @GetMapping("/api/locations")
    @ResponseBody 
    public List<MapDto> getLocations() {
        
        // DB에서 모든 위치(Location) 엔티티 조회
        List<Location> entityList = locationRepository.findAll();

        // 엔티티 리스트를 화면 전용 MapDto 리스트로 변환하여 반환
        return entityList.stream()
                .map(loc -> new MapDto(
                        loc.getLocName(),       
                        loc.getAddress(),       
                        loc.getRecycleItems(),  
                        loc.getLat(),           
                        loc.getLon()            // DB 컬럼명 lon 기준
                ))
                .collect(Collectors.toList());
    }
}