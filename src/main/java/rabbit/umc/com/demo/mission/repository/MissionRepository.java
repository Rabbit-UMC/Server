package rabbit.umc.com.demo.mission.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import rabbit.umc.com.demo.mission.Mission;

import java.util.List;

@Repository
public interface MissionRepository extends JpaRepository<Mission,Long> {

    @Query("select m from Mission m join MissionUsers ms on m.id = ms.mission.id")
    List<Mission> getHome();

    @Query("select m from Mission m join MissionUsers ms on m.id = ms.mission.id where m.category.id = :categoryId")
    List<Mission> getMissionByCategoryId(@Param("categoryId") Long categoryId);
//    @Query("select m from Mission m join MissionUsers ms on m.id = ms.mission.id")
//    List<Mission> getMissionHome();

}
