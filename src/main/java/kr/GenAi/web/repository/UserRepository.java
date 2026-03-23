package kr.GenAi.web.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import kr.GenAi.web.Entity.User;


public interface UserRepository extends JpaRepository<User, String> {

	public User findByIdAndPw(String id, String pw);
}
