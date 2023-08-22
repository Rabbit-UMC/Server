package rabbit.umc.com.demo.report;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rabbit.umc.com.demo.community.domain.Article;
import rabbit.umc.com.demo.user.Domain.User;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report ,Long> {

    Long findReportByArticleId(Long id);

    Report findReportByUserIdAndArticleId(Long userId, Long articleId);

    Report findReportByUserIdAndAndMainMissionProofId(Long userId, Long mainMissionProofId);

    List<Report> findAllByArticleId(Long articleId);
    List<Report> findAllByMainMissionProofId(Long mainMissionProofId);

    int countByArticleId(Long articleId);

    Boolean existsByUserAndArticle(User user, Article article);

    Report findReportByUserIdAndMissionId(long userId, long missionId);


}
