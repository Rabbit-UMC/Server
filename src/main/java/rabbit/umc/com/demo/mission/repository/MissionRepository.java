package rabbit.umc.com.demo.mission.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rabbit.umc.com.demo.mission.Mission;

import java.util.List;

@Repository
public interface MissionRepository extends JpaRepository<Mission,Long> {
    List<Mission> getHome();
}
