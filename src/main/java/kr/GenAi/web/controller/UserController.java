package kr.GenAi.web.controller;

import java.lang.reflect.Member;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import org.springframework.ui.Model;
import jakarta.servlet.http.HttpSession;
import kr.GenAi.web.Entity.User;
import kr.GenAi.web.dto.PointDTO;
import kr.GenAi.web.repository.UserRepository;
import kr.GenAi.web.service.PointService;

@Controller
public class UserController {

	@Autowired
	UserRepository repository;

	@Autowired
	private PointService pointService; // (팀에서 사용하는 서비스 이름에 맞게 수정하세요)
	
	// 회원가입 처리
	@PostMapping("/joinProcess")
	public String joinProcess(User joinMem, @RequestParam("pwCheck") String pwCheck) {

		// 1. 비밀번호 일치 확인 (입력한 두 비밀번호가 같은지)
		if (joinMem.getPw() == null || !joinMem.getPw().equals(pwCheck)) {
			return "redirect:/join?error=pw";
		}

		// 2. 비밀번호 조건 검사 (최소 8자, 영문+숫자 포함)
		// 이 정규식은 보안을 위해 본인 코드에 있던 것을 그대로 유지하는 게 좋습니다.
		if (!joinMem.getPw().matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$")) {
			return "redirect:/join?error=rule";
		}

		// 3. 최종 아이디 중복 방어
		// 화면에서 중복 체크를 했더라도, 서버에서 한 번 더 확인하는 것이 정석입니다.
		if (repository.findById(joinMem.getId()) != null) {
			return "redirect:/join?error=id";
		}

		// 4. 모든 검사 통과 시 저장
		repository.save(joinMem);

		// 가입 완료 후 바로 메인보다는 로그인을 유도하는 것이 일반적입니다.
		return "redirect:/login";
	}

	
	
	
	// 로그인
	@PostMapping("/loginProcess")
	public String loginProcess(User loinMem, HttpSession session) {

		User user = repository.findByIdAndPw(loinMem.getId(), loinMem.getPw());

		if (user != null) {
			session.setAttribute("loginMem", user);
			return "redirect:/main";
		} else {
			return "redirect:/login?error=true";
		}

	}

	
	
	
	// 아이디 중복 체크용
	@GetMapping("/recyq/checkId")
	@ResponseBody
	public String checkId(String id) {

		// null 방지
		if (id == null || id.trim().isEmpty()) {
			return "fail";
		}

		// DB에 존재하면 fail, 없으면 ok
		if (repository.findById(id) != null) {
			return "fail";
		} else {
			return "ok";
		}
	}

	
	
	
	
	// 회원 정보 수정 (아이디 고정, 비밀번호 및 전화번호 수정)
	@PostMapping("/updateUser")
	public String updateUser(User updateMem, HttpSession session) {

		// 1. 세션에서 로그인 정보를 가져옴
		User loginMem = (User) session.getAttribute("loginMem");

		if (loginMem != null) {
			// 2. PK(UserCode)를 이용해 DB에서 기존 정보를 조회
			User changeMem = repository.findById(loginMem.getUserCode()).orElse(null);

			if (changeMem != null) {
				// 3. 변경할 정보들만 세팅
				changeMem.setPw(updateMem.getPw()); // 비밀번호 변경
				changeMem.setPhone(updateMem.getPhone()); // 전화번호 변경 (추가된 부분!)

				// 4. DB 저장
				repository.save(changeMem);

				// 5. 세션에도 최신 정보(변경된 비번, 전번 포함)를 다시 덮어씌움
				session.setAttribute("loginMem", changeMem);
			}
		}

		return "redirect:/main";
	}

	
	
	
	// 회원 정보 삭제
	@PostMapping("/deleteUser")
	public String deleteUser(HttpSession session) {
		User deleteMem = (User) session.getAttribute("loginMem");

		repository.deleteById(deleteMem.getUserCode());

		session.invalidate();

		return "redirect:/main";
	}

	// 마이페이지 컨트롤러 예시
	@GetMapping("/mypage")
    public String mypage(HttpSession session, Model model) {
        // 🌟 2. Member 대신 우리 팀의 Entity인 User로 변경!
        User loginUser = (User) session.getAttribute("loginMem");

        // (안전장치) 로그인이 안 되어있으면 로그인 페이지로 튕겨내기
        if (loginUser == null) {
            return "redirect:/login"; // 팀의 로그인 페이지 경로에 맞게 수정하세요
        }

        // 🌟 3. 대문자 PointService를 소문자 pointService로 변경
        int todayPoint = pointService.getTodayPoint(loginUser.getId());
        int monthPoint = pointService.getMonthPoint(loginUser.getId());
        List<PointDTO> pointList = pointService.getRecentPointList(loginUser.getId(), loginUser.getTotalPoint());

        // 타임리프(HTML)로 데이터 넘기기
        model.addAttribute("todayPoint", todayPoint);
        model.addAttribute("monthPoint", monthPoint);
        model.addAttribute("pointList", pointList);

        return "mypage";
    }
	
	
	
}