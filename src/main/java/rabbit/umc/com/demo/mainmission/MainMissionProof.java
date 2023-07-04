package rabbit.umc.com.demo.mainmission;

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

    private String proofImage;

    @ManyToOne(fetch = FetchType.LAZY)
    private MainMission mainMission;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Enumerated(EnumType.STRING)
    private Status status;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
