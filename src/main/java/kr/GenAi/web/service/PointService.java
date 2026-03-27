package kr.GenAi.web.service;

import kr.GenAi.web.dto.PointDTO;
import kr.GenAi.web.Entity.PointLog;
import kr.GenAi.web.repository.PointLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PointService {

    @Autowired
    private PointLogRepository pointLogRepository;

    public int getTodayPoint(String userId) {
        return pointLogRepository.sumTodayPoint(userId);
    }

    public int getMonthPoint(String userId) {
        return pointLogRepository.sumMonthPoint(userId);
    }

 // 🌟 파라미터에 int currentTotal 추가됨
    public List<PointDTO> getRecentPointList(String userId, int currentTotal) {
        List<PointLog> logs = pointLogRepository.findTop5ByUser_IdOrderByCreatedAtDesc(userId);
        List<PointDTO> dtoList = new ArrayList<>();
        
        // 🌟 현재 총 포인트부터 시작해서 과거로 거슬러 내려감
        int runningTotal = currentTotal; 
        
        for(PointLog log : logs) {
            dtoList.add(new PointDTO(
                log.getPointIdx(),         
                log.getUser().getId(),     
                log.getLogDetail(),        
                log.getRecPoint(),         
                runningTotal,              // 🌟 계산된 그 당시의 총 포인트 삽입!
                log.getCreatedAt()         
            ));
            
            // 🌟 다음 과거 내역을 계산하기 위해, 지금 얻은 포인트를 다시 빼줌
            runningTotal = runningTotal - log.getRecPoint();
        }
        return dtoList;
    }
    
 // 🌟 어제 만든 getRecentPointList 와 로직은 똑같고, Repository 메서드만 findAll 로 바꿨습니다.
    public List<PointDTO> getAllPointList(String userId, int currentTotal) {
        List<PointLog> logs = pointLogRepository.findAllByUser_IdOrderByCreatedAtDesc(userId);
        List<PointDTO> dtoList = new ArrayList<>();
        
        int runningTotal = currentTotal; 
        for(PointLog log : logs) {
            dtoList.add(new PointDTO(
                log.getPointIdx(), log.getUser().getId(), log.getLogDetail(),
                log.getRecPoint(), runningTotal, log.getCreatedAt()
            ));
            runningTotal -= log.getRecPoint();
        }
        return dtoList;
    }
    
    
    
    
    
    
    
    
    
}