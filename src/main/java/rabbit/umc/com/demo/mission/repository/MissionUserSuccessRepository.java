package rabbit.umc.com.demo.mission.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rabbit.umc.com.demo.mission.MissionUserSuccess;

import java.util.List;

public interface MissionUserSuccessRepository extends JpaRepository<MissionUserSuccess, Long> {

    MissionUserSuccess getMissionUserSuccessByMissionIdAndUserId(Long id, Long userId);

    List<MissionUserSuccess> getMissionUserSuccessByMissionId(Long id);


//    List<MissionUserSuccess> getMissionUserSuccessByMissionId(Long id);
}
