package rabbit.umc.com.demo.mission.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import rabbit.umc.com.demo.mission.Mission;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MissionRepository extends JpaRepository<Mission,Long> {

    @Query("select m from Mission m join MissionUsers ms on m.id = ms.mission.id where m.isOpen = 0 and DATE(m.endAt) >:now")
    List<Mission> getHome(@Param(value = "now") LocalDateTime now);

    List<Mission> getMissionByMissionCategoryIdOrderByEndAt(@Param("missionCategoryId") Long missionCategryId);
    Mission getMissionByIdAndEndAtIsBeforeOrderByEndAt(Long id, LocalDateTime currentDateTime);

    Mission getMissionById(long missionId);

    List<Mission> getMissionsByIdIsIn(List ids);

    Mission getMissionByIdAndEndAtIsBefore(Long id, LocalDateTime now);

    List<Mission> getMissionsByEndAtAfterAndIsOpenOrderByEndAt(LocalDateTime now,int isOpen);

    Mission getMissionByIdAndEndAtIsAfterOrderByEndAt(Long id, LocalDateTime currentDateTime);
}
