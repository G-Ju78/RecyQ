package kr.GenAi.web.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.GenAi.web.Entity.Location;

public interface LocationRepository extends JpaRepository<Location, Integer> {

}
