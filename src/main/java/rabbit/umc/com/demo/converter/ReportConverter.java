package rabbit.umc.com.demo.converter;

import rabbit.umc.com.demo.base.Status;
import rabbit.umc.com.demo.community.domain.Article;
import rabbit.umc.com.demo.mainmission.domain.mapping.MainMissionProof;
import rabbit.umc.com.demo.report.Report;
import rabbit.umc.com.demo.user.Domain.User;

public class ReportConverter {

    public static Report toArticleReport(User user, Article article){
        return Report.builder()
                .user(user)
                .article(article)
                .build();
    }

    public static Report toMissionProofReport(User user, MainMissionProof mainMissionProof){
        return Report.builder()
                .user(user)
                .mainMissionProof(mainMissionProof)
                .status(Status.ACTIVE)
                .build();
    }
}
