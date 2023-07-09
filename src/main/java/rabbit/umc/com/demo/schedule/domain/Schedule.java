package rabbit.umc.com.demo.schedule.domain;

import lombok.Getter;
import lombok.Setter;
import rabbit.umc.com.demo.Status;
import rabbit.umc.com.demo.schedule.dto.PatchScheduleReq;
import rabbit.umc.com.demo.schedule.dto.PostScheduleReq;
import rabbit.umc.com.demo.user.Domain.User;

import javax.persistence.*;
import java.sql.Date;
import java.sql.Timestamp;

@Entity
@Getter
@Setter
@Table(name = "schedule")
public class Schedule {
    @Id@GeneratedValue
    @Column(name = "schedule_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String title;
    private String content;

    @Column(nullable = false)
    private Timestamp startAt;
    @Column(nullable = false)
    private Timestamp endAt;

    @Enumerated(EnumType.STRING)
    private Status status;
    @Column
    private Timestamp createdAt;

    private Timestamp updatedAt;

    public void setSchedule(PostScheduleReq postScheduleReq,Long userId){
        this.content = postScheduleReq.getContent();
        this.title = postScheduleReq.getTitle();
        this.createdAt = postScheduleReq.getCreatedAt();
        this.endAt = postScheduleReq.getEndAt();
        this.startAt = postScheduleReq.getStartAt();
        this.updatedAt = postScheduleReq.getUpdatedAt();
        this.user.setId(userId);
    }


}
