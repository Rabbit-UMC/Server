package rabbit.umc.com.demo.report;


import static rabbit.umc.com.config.apiPayload.BaseResponseStatus.FAILED_TO_REPORT;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rabbit.umc.com.config.apiPayload.BaseException;
import rabbit.umc.com.demo.community.domain.Article;
import rabbit.umc.com.demo.converter.ReportConverter;
import rabbit.umc.com.demo.mainmission.domain.mapping.MainMissionProof;
import rabbit.umc.com.demo.user.Domain.User;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReportService {
    private static final int REPORT_REMIT = 15;

    private final ReportRepository reportRepository;

    public boolean isReport(Long userId, Long mainMissionProofId){
        Optional<Report> findReport = reportRepository.findReportByUserIdAndAndMainMissionProofId(userId, mainMissionProofId);
        return findReport.isPresent();
    }
    @Transactional
    public void reportMissionProof(Report report){
        reportRepository.save(report);
    }

    @Transactional
    public boolean checkInactivation(MainMissionProof mainMissionProof){
        List<Report> countReport = reportRepository.findAllByMainMissionProof(mainMissionProof);
        return countReport.size() >= REPORT_REMIT;
    }

    @Transactional
    public void reportArticle(User user, Article article) throws BaseException {
        Boolean isReportExists = reportRepository.existsByUserAndArticle(user, article);
        if (isReportExists) {
            throw new BaseException(FAILED_TO_REPORT);
        } else {
            Report report = ReportConverter.toArticleReport(user, article);
            reportRepository.save(report);

            // 신고 횟수 15회 이상 시 게시물 status 변경 로직  [ACTIVE -> INACTIVE]
            int reportCount = reportRepository.countByArticle(article);
            if (reportCount >= REPORT_REMIT) {
                article.setInactive();
            }
        }
    }
}
