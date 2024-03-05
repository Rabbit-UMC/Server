package rabbit.umc.com.demo.schedule.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import rabbit.umc.com.demo.base.BaseTimeEntity;
import rabbit.umc.com.demo.base.Status;
import rabbit.umc.com.demo.mission.Mission;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "mission_schedule")
public class MissionSchedule extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = IDENTITY)
    @Column(name = "mission_schedule_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id")
    private Schedule schedule;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mission_id")
    private Mission mission;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status = Status.ACTIVE;

    public MissionSchedule(Schedule schedule, Mission mission, Status status) {
        if(schedule != null)
            this.schedule = schedule;
        else
            this.schedule = null;
        if(mission != null)
            this.mission = mission;
        else
            this.mission = null;

        this.status = status;
    }

    public void updateMission(Mission mission){
        this.mission = mission;
    }

    public void deleteMissionSchedule(Mission mission, Schedule schedule){
        if(mission.getId() == this.mission.getId() && schedule.getId() == this.schedule.getId())
            this.mission = null;
    }
}

