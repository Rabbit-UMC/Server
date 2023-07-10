package rabbit.umc.com.demo.report;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report ,Long> {

    Long findReportByArticleId(Long id);
    Report findReportByUserIdAndArticleId(Long userId, Long articleId);

    Report findReportByUserIdAndAndMainMissionProofId(Long userId, Long mainMissionProofId);

    List<Report> findAllByArticleId(Long articleId);
    List<Report> findAllByMainMissionProofId(Long mainMissionProofId);
}
