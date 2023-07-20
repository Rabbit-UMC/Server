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
@Table(name = "main_mission_users")
public class MainMissionUsers extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = IDENTITY)
    @Column(name = "main_mission_user_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private int score;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "main_mission_id")
    private MainMission mainMission;


    @Enumerated(EnumType.STRING)
    private Status status = Status.ACTIVE;

}
