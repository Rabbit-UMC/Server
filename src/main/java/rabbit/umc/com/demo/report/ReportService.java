package rabbit.umc.com.demo.report;


import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
    public boolean checkInactivation(Long mainMissionProofId, MainMissionProof mainMissionProof){
        List<Report> countReport = reportRepository.findAllByMainMissionProofId(mainMissionProofId);
        return countReport.size() >= REPORT_REMIT;
    }
}
