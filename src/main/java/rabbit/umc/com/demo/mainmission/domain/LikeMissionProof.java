package rabbit.umc.com.demo.mainmission.domain;

import lombok.Getter;
import lombok.Setter;
import rabbit.umc.com.config.BaseTimeEntity;
import rabbit.umc.com.demo.Status;
import rabbit.umc.com.demo.user.Domain.User;
import javax.persistence.*;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@Setter
@Table(name = "like_mission_proof")
public class LikeMissionProof extends BaseTimeEntity {
    @Id@GeneratedValue(strategy = IDENTITY)
    @Column(name ="like_mission_proof_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "main_mission_proof_id", nullable = false)
    private MainMissionProof mainMissionProof;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    private Status status= Status.ACTIVE;



    //Setter
    public void setLikeMissionProof(User user, MainMissionProof mainMissionProof){
        this.user = user;
        this.mainMissionProof = mainMissionProof;
    }

}
