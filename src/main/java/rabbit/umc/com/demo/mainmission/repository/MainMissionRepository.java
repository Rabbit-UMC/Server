package rabbit.umc.com.demo.mainmission.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import rabbit.umc.com.demo.community.domain.Category;
import rabbit.umc.com.demo.mainmission.domain.MainMission;

import rabbit.umc.com.demo.base.Status;

import java.time.LocalDate;
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

    MainMission findMainMissionByCategoryIdAndStatus(Long categoryId, Status status);
    List<MainMission> findMainMissionsByEndAtBeforeAndLastMissionTrue(LocalDate now);

    Optional<MainMission> findMainMissionsByCategoryAndStatus(Category category, Status status );

}