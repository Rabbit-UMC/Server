package rabbit.umc.com.demo.schedule.domain;

import lombok.Getter;
import lombok.Setter;
import rabbit.umc.com.config.BaseTimeEntity;
import rabbit.umc.com.demo.Status;
import rabbit.umc.com.demo.schedule.dto.PatchScheduleReq;
import rabbit.umc.com.demo.schedule.dto.PostScheduleReq;
import rabbit.umc.com.demo.user.Domain.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@Setter
@Table(name = "schedule")
public class Schedule extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = IDENTITY)
    @Column(name = "schedule_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String title;
    private String content;

    @Column(nullable = false)
    private LocalDateTime startAt;
    @Column(nullable = false)
    private LocalDateTime endAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status = Status.ACTIVE;

    public void setSchedule(PostScheduleReq postScheduleReq){

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime startAt = LocalDateTime.parse(postScheduleReq.getWhen()+ " " +postScheduleReq.getStartAt(),formatter);
        LocalDateTime endAt = LocalDateTime.parse(postScheduleReq.getWhen()+ " " +postScheduleReq.getEndAt(),formatter);

        this.content = postScheduleReq.getContent();
        this.title = postScheduleReq.getTitle();
        this.endAt = endAt;
        this.startAt = startAt;
    }


}
