package rabbit.umc.com.demo.schedule.domain;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import rabbit.umc.com.demo.Status;
import rabbit.umc.com.demo.mission.Mission;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Getter@Setter
@Table(name = "mission_schedule")
public class MissionSchedule {
    @Id @GeneratedValue
    @Column(name = "mission_schedule_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id")
    private Schedule schedule;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mission_id")
    private Mission mission;

    @Enumerated(EnumType.STRING)
    @ColumnDefault(value = "ACTIVE")
    private Status status;
    @Column
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public void setMissionAndSchedule(Long missionId,Long scheduleId){
        this.mission.setId(missionId);
        this.schedule.setId(scheduleId);
    }

}

