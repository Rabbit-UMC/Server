package rabbit.umc.com.demo.mission;

import lombok.Getter;
import lombok.Setter;
import rabbit.umc.com.config.BaseTimeEntity;
import rabbit.umc.com.demo.Status;
import rabbit.umc.com.demo.user.Domain.User;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "mission_user_success")
public class MissionUserSuccess extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mission_id", nullable = false)
    private Mission mission;
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status = Status.ACTIVE;

}
