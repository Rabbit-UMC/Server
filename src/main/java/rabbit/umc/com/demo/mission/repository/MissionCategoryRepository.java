package rabbit.umc.com.demo.mission.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rabbit.umc.com.demo.Status;
import rabbit.umc.com.demo.mission.MissionCategory;

import java.util.List;

@Repository
public interface MissionCategoryRepository extends JpaRepository<MissionCategory, Long> {
    List<MissionCategory> getAllByStatusIs(Status status);
}
