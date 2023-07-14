package rabbit.umc.com.demo.mission.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import rabbit.umc.com.demo.mission.Mission;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MissionRepository extends JpaRepository<Mission,Long> {

    @Query("select m from Mission m join MissionUsers ms on m.id = ms.mission.id where m.isOpen = 0")
    List<Mission> getHome();
    List<Mission> getMissionByMissionCategoryId(@Param("missionCategoryId") Long missionCategryId);
    Mission getMissionByIdAndEndAtIsAfter(Long id, LocalDateTime currentDateTime);

    Mission getMissionById(long missionId);

    List<Mission> getMissionsByIdIsIn(List ids);
}
