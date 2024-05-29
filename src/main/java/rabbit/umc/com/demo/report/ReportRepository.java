package rabbit.umc.com.demo.report;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rabbit.umc.com.demo.community.domain.Article;
import rabbit.umc.com.demo.mainmission.domain.mapping.MainMissionProof;
import rabbit.umc.com.demo.user.Domain.User;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report ,Long> {

    Optional<Report> findReportByUserIdAndAndMainMissionProofId(Long userId, Long mainMissionProofId);

    List<Report> findAllByMainMissionProof(MainMissionProof mainMissionProof);

    int countByArticle(Article article);

    Boolean existsByUserAndArticle(User user, Article article);

    Report findReportByUserIdAndMissionId(long userId, long missionId);


}
