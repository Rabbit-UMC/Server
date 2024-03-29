package rabbit.umc.com.demo.report;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import rabbit.umc.com.demo.base.BaseTimeEntity;
import rabbit.umc.com.demo.base.Status;
import rabbit.umc.com.demo.community.domain.Article;
import rabbit.umc.com.demo.community.domain.Comment;
import rabbit.umc.com.demo.mainmission.domain.mapping.MainMissionProof;
import rabbit.umc.com.demo.mission.Mission;
import rabbit.umc.com.demo.user.Domain.User;
import javax.persistence.*;
import static javax.persistence.GenerationType.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "report")
public class Report extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = IDENTITY)
    @Column(name = "report_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id")
    private Article article;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "main_mission_proof_id")
    private MainMissionProof mainMissionProof;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private Comment comment;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(15) DEFAULT 'ACTIVE'")
    private Status status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mission_id")
    private Mission mission;




    //Setter
    public void setReport(User user, MainMissionProof mainMissionProof){
        this.user = user;
        this.mainMissionProof = mainMissionProof;
    }
}
