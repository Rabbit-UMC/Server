package rabbit.umc.com.demo.mainmission;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import rabbit.umc.com.demo.article.domain.Category;
import rabbit.umc.com.demo.mainmission.domain.MainMission;

import rabbit.umc.com.demo.Status;
import java.util.List;
@Repository
public interface MainMissionRepository extends JpaRepository<MainMission, Long> {



//    @Query(value = "select m  " +
//            "from MainMission m  " +
//            "WHERE m.status = 'ACTIVE' " )
//    List<MainMission> findProgressMission();

    @Query("SELECT m FROM MainMission m " +
            "LEFT JOIN FETCH m.category " +
            "WHERE m.status = :status")
    List<MainMission> findProgressMissionByStatus(@Param("status") Status status);

    MainMission findMainMissionByCategoryAndStatus(Category category, Status status);

}