package rabbit.umc.com.demo.mainmission.domain;

import lombok.Getter;
import lombok.Setter;
import rabbit.umc.com.config.BaseTimeEntity;
import rabbit.umc.com.demo.Status;
import rabbit.umc.com.demo.user.Domain.User;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

import static javax.persistence.CascadeType.*;

@Entity
@Getter
@Setter
@Table(name = "main_mission_proof")
public class MainMissionProof extends BaseTimeEntity {
    @Id@GeneratedValue
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
    @Column(nullable = false)
    private Status status = Status.ACTIVE;

}
