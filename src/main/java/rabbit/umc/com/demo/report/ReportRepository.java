package rabbit.umc.com.demo.report;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportRepository extends JpaRepository<Report ,Long> {

    Long findReportByArticleId(Long id);
    Report findReportByUserIdAndArticleId(Long userId, Long articleId);
}
