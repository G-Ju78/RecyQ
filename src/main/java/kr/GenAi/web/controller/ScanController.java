package kr.GenAi.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import jakarta.servlet.http.HttpSession;
import kr.GenAi.web.Entity.User;
import kr.GenAi.web.dto.TrashResponseDto;
import kr.GenAi.web.repository.PointLogRepository;
import kr.GenAi.web.Entity.PointLog;
import kr.GenAi.web.repository.UserRepository;
import lombok.RequiredArgsConstructor;

/**
 * [ScanController] 사용자가 업로드한 쓰레기 사진을 파이썬 AI 서버로 전송하고, 그 결과를 받아 DB(포인트 적립) 처리 후
 * 결과 화면(HTML)으로 띄워주는 핵심 컨트롤러입니다.
 */
// @RestController가 아닌 @Controller를 사용한 이유:
// 데이터를 단순히 JSON으로 뱉는 게 아니라, 완성된 HTML 화면(scanResult.html)을 사용자에게 반환하기 위함입니다.

@RequiredArgsConstructor // (Autowired)대신 생성자 주입을 자동으로 만들어줌
@Controller
public class ScanController {

	// final을 붙이면 이 객체는 필수, 절대 바꿀수 없다는 뜻(불변성/안정성)
	private final PointLogRepository pointLogRepository;
	private final UserRepository userRepository;
	
	/**
	 * 🌟 [추가된 부분: 카메라 화면 띄우기]
	 * 메인 페이지에서 '스캔하여 분리배출 하기' 버튼을 누르면 이쪽으로 와서 camera.html을 엽니다.
	 */
	@GetMapping("/camera")
	public String showCameraPage(HttpSession session) {
		// 보안 체크: 로그인하지 않은 유저가 강제로 접근하면 로그인 페이지로 쫓아냅니다.
		User loginMem = (User) session.getAttribute("loginMem");
		if (loginMem == null) {
			return "redirect:/login";
		}
		// templates/camera.html 화면을 렌더링해서 보여줌
		return "camera"; 
	}
	
	/**
	 * [doScan 메서드: 스캔 및 AI 분석 요청 처리]
	 * * @param file    사용자가 업로드/촬영한 이미지 파일
	 * @param lat     위도 (현재 위치)
	 * @param lon     경도 (현재 위치)
	 * @param session 현재 로그인한 유저의 세션 정보
	 * @param model   HTML 화면(View)으로 데이터를 넘겨주기 위한 객체
	 */
	@PostMapping("/doScan")
	public String doScan(@RequestParam("file") MultipartFile file, @RequestParam("lat") Double lat,
			@RequestParam("lon") Double lon, HttpSession session, Model model) {

		// 1. 보안 체크: 로그인하지 않은 유저가 강제로 접근하면 로그인 페이지로 쫓아냅니다.
		User loginMem = (User) session.getAttribute("loginMem");
		if (loginMem == null)
			return "redirect:/login";

		try {
			// ====================================================================
			// [STEP 1] FastAPI(Python AI 서버)와 통신 준비
			// ====================================================================
			// 내부망(로컬)에서 실행 중인 파이썬 AI 서버의 엔드포인트 주소
			String fastapiUrl = "http://10.1.2.7:8000/api/scan";

			// RestTemplate: 스프링 부트에서 다른 서버(외부 API)로 HTTP 요청을 보낼 때 사용하는 도구
			RestTemplate restTemplate = new RestTemplate();

			// MultiValueMap: 폼(Form) 데이터 형식으로 파일과 텍스트 데이터를 함께 담기 위한 특수 바구니
			MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
			body.add("file", file.getResource()); // 실제 사진 데이터
			body.add("lat", lat); // 위치 정보
			body.add("lon", lon);

			// 🌟 핵심: AI가 "ㅇㅇㅇ님 훌륭해요!"라고 부를 수 있도록 로그인한 유저의 ID(이름)를 파이썬으로 넘겨줌
			body.add("userId", loginMem.getId());

			// postForObject: 파이썬 서버로 body(데이터)를 POST 방식으로 던지고,
			// 그 결과(JSON)를 우리가 만든 자바 객체(TrashResponseDto) 모양에 맞춰서 받아옴
			TrashResponseDto result = restTemplate.postForObject(fastapiUrl, body, TrashResponseDto.class);

			// ====================================================================
			// [STEP 2] DB 에러 방어 및 포인트 적립 로직 (Business Logic)
			// ====================================================================
			// result가 null이 아니고, AI가 보상 적격(rewardEligible)을 true로 판정했을 때만 포인트 지급!
			// 주의: Boolean.TRUE.equals()를 쓴 이유는, AI가 실수로 null을 보냈을 때 NullPointerException 서버
			// 다운을 막기 위한 완벽한 방어책입니다.
			if (result != null && Boolean.TRUE.equals(result.getRewardEligible())) {

				// AI가 사진 속 쓰레기 개수(itemCount)를 세어준 것을 가져옴. (null이거나 0이면 최소 1개로 간주)
				int count = (result.getItemCount() != null && result.getItemCount() > 0) ? result.getItemCount() : 1;

				// 🌟 핵심 정책: 쓰레기 1개당 10포인트씩 곱해서 최종 지급할 포인트 계산
				int finalPoint = count * 10;

				// 2-1. 유저의 총 포인트 업데이트
				// 기존 포인트가 null이면 0으로 치환하여 계산 에러 방지
				int currentPoint = (loginMem.getTotalPoint() != null) ? loginMem.getTotalPoint() : 0;
				loginMem.setTotalPoint(currentPoint + finalPoint);
				userRepository.save(loginMem); // DB에 유저 정보 덮어쓰기(Save)

				// 2-2. 포인트 지급 내역(Log) 기록
				PointLog log = new PointLog();
				log.setUser(loginMem);
				log.setLogType("RECYCLE"); // 로그 타입 명시
				// 로그 내역 예시: "플라스틱 병 3개 분리배출 보상"
				log.setLogDetail(result.getDetectedItem() + " " + count + "개 분리배출 보상");
				log.setRecPoint(finalPoint); // 지급된 포인트
				pointLogRepository.save(log); // DB에 로그 한 줄 추가(Insert)

				// 2-3. HTML 화면에 "+ 30 P" 처럼 띄워주기 위해, 방금 계산한 진짜 포인트를 DTO에 다시 세팅함
				result.setEarnedPoint(finalPoint);
			}

			// ====================================================================
			// [STEP 3] 화면(HTML) 렌더링을 위한 데이터 세팅
			// ====================================================================
			// 🌟 Base64 인코딩: 업로드한 사진을 서버 하드디스크에 저장하지 않고,
			// 글자(텍스트) 형태로 변환해서 HTML <img> 태그에 바로 쏴주는 기술. (서버 용량 절약 및 속도 향상)
			String base64Image = java.util.Base64.getEncoder().encodeToString(file.getBytes());
			model.addAttribute("uploadedImage", "data:image/jpeg;base64," + base64Image);

			// 🌟 디버깅용 로그: 파이썬 AI가 제대로 JSON을 만들어서 보냈는지 이클립스 콘솔에서 확인하기 위함
			System.out.println("\n--- [AI 분석 결과 확인] ---");
			System.out.println(result);
			System.out.println("---------------------------\n");

			// 타임리프(HTML)에서 쓸 수 있도록 AI 분석 결과 객체를 model에 담아서 넘겨줌
			model.addAttribute("result", result);

			// templates 폴더 안의 scanResult.html 파일로 이동
			return "scanResult";

		} catch (Exception e) {
			// ====================================================================
			// [STEP 4] 예외(에러) 처리
			// ====================================================================
			// 통신 실패, 데이터 파싱 오류 등 어떤 에러가 발생하더라도 사용자 화면이 하얗게 멈추지 않고(Whitelabel Error 방지)
			// 메인 화면으로 돌려보내면서 "분석 실패" 알림을 띄우기 위한 방어막(catch)입니다.

			System.out.println("\n\n=======================================");
			System.out.println("🚨🚨 스캔 처리 중 치명적 오류 발생! 🚨🚨");
			System.out.println("=======================================");
			e.printStackTrace(); // 진짜 에러 원인을 빨간 글씨로 출력
			System.out.println("=======================================\n\n");

			// 에러 발생 시 튕기면서 URL 뒤에 파라미터를 붙임 (프론트에서 이 파라미터를 읽고 알림창을 띄울 수 있음)
			return "redirect:/main?error=api_fail";
		}
	}
}