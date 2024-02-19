package rabbit.umc.com.demo.mission.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import rabbit.umc.com.demo.base.Status;
import rabbit.umc.com.demo.mission.Mission;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MissionRepository extends JpaRepository<Mission,Long> {

    @Query("select m from Mission m join MissionUsers ms on m.id = ms.mission.id where m.isOpen = 0 and DATE(m.endAt) >= :now")
    List<Mission> getHome(@Param(value = "now") LocalDateTime now);

    @Query("select m from Mission m left join fetch m.category where m.category.id = :missionCategoryId and m.isOpen = :isOpen and m.startAt >= :now and m.status = :active order by m.startAt")
    List<Mission> getMissionByMissionCategoryIdOrderByStartAt(@Param("active") Status active, @Param("now") LocalDateTime now, @Param("isOpen") int isOpen, @Param("missionCategoryId") Long missionCategryId, PageRequest pageRequest);
//    @Query("select m from Mission m where m.status = 'ACTIVE' and m.isOpen = 0 order by m.startAt")
//    List<Mission> getMissions(PageRequest pageRequest);
    Mission getMissionByIdAndEndAtIsBeforeOrderByEndAt(Long id, LocalDateTime currentDateTime);

    Mission getMissionById(long missionId);

    List<Mission> getMissionsByIdIsIn(List ids);

    Mission getMissionByIdAndEndAtIsBefore(Long id, LocalDateTime now);
//
//    @Query("SELECT m FROM Mission m " +
//        "LEFT JOIN fetch m.category " +
//        "WHERE m.endAt > :now AND m.isOpen = :isOpen AND m.status = :status " +
//        "ORDER BY m.startAt"
//    )
//    List<Mission> findAllByStatusAndEndAtAfterAndIsOpenOrderByStartAt(@Param("now") LocalDateTime now,
//                              @Param("isOpen") int isOpen,
//                              @Param("status") Status status,
//                                            PageRequest pageRequest);
    List<Mission> findAllByStatusAndStartAtAfterAndIsOpenOrderByStartAt(Status active, LocalDateTime now, int isOpen, PageRequest pageRequest);


    Mission findByIdAndEndAtIsAfterAndStatusAndIsOpenOrderByEndAt(Long id, LocalDateTime currentDateTime, Status status,int isOpen);

    Mission getMissionByTitle(String title);

}
