package rabbit.umc.com.demo.mainmission.domain;

import lombok.Getter;
import lombok.Setter;
import rabbit.umc.com.config.BaseTimeEntity;
import rabbit.umc.com.demo.Status;
import rabbit.umc.com.demo.user.Domain.User;
import javax.persistence.*;
import java.util.List;

import static javax.persistence.CascadeType.*;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@Setter
@Table(name = "main_mission_proof")
public class MainMissionProof extends BaseTimeEntity {
    @Id@GeneratedValue(strategy = IDENTITY)
    @Column(name = "main_mission_proof_id")
    private Long id;

    @Column(nullable = false)
    private String proofImage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "main_mission_id", nullable = false)
    private MainMission mainMission;

    @OneToMany(mappedBy = "mainMissionProof", cascade = ALL)
    private List<LikeMissionProof> likeMissionProofs;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    private Status status = Status.ACTIVE;

    public void setMainMissionProof(String filePath, User user, MainMission mainMission){
        proofImage = filePath;
        this.user = user;
        this.mainMission = mainMission;
    }
}
