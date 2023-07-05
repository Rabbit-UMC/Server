package rabbit.umc.com.demo.mainmission.domain;

import lombok.Getter;
import lombok.Setter;
import rabbit.umc.com.demo.Status;
import rabbit.umc.com.demo.user.User;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Getter
@Setter
@Table(name = "main_mission_proof")
public class MainMissionProof {
    @Id@GeneratedValue
    @Column(name = "main_mission_proof_id")
    private Long id;

    @Column(nullable = false)
    private String proofImage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "main_mission_id", nullable = false)
    private MainMission mainMission;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;
    @Column(nullable = false)
    private Timestamp createdAt;
    @Column(nullable = false)
    private Timestamp updatedAt;
}
