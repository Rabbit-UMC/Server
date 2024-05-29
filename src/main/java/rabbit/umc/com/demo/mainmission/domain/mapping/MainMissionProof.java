package rabbit.umc.com.demo.mainmission.domain.mapping;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import rabbit.umc.com.demo.base.BaseTimeEntity;
import rabbit.umc.com.demo.base.Status;
import rabbit.umc.com.demo.mainmission.domain.MainMission;
import rabbit.umc.com.demo.user.Domain.User;
import javax.persistence.*;
import java.util.List;

import static javax.persistence.CascadeType.*;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
    @Column(columnDefinition = "VARCHAR(15) DEFAULT 'ACTIVE'")
    @Builder.Default
    private Status status = Status.ACTIVE;

    //비즈니스 로직
    public void inActive(){
        status = Status.INACTIVE;
    }
}
