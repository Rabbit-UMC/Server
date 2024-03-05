package rabbit.umc.com.demo.mission;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import rabbit.umc.com.demo.base.BaseTimeEntity;
import rabbit.umc.com.demo.base.Status;
import rabbit.umc.com.demo.user.Domain.User;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "mission_users")
public class MissionUsers extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = IDENTITY)
    @Column(name = "mission_user_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mission_id")
    private Mission mission;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status = Status.ACTIVE;

    public MissionUsers(User user, Mission mission, Status status) {
        this.user = user;
        this.mission = mission;
        this.status = status;
    }
}
