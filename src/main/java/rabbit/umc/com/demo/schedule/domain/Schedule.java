package rabbit.umc.com.demo.schedule.domain;

import lombok.*;
import rabbit.umc.com.demo.base.BaseTimeEntity;
import rabbit.umc.com.demo.base.Status;
import rabbit.umc.com.demo.schedule.dto.PostScheduleReq;
import rabbit.umc.com.demo.user.Domain.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
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

    public Schedule(User user, String title, String content, LocalDateTime startAt, LocalDateTime endAt, Status status) {
        this.user = user;
        this.title = title;
        this.content = content;
        this.startAt = startAt;
        this.endAt = endAt;
        this.status = status;
    }

    public static Schedule toSchedule(User user,PostScheduleReq postScheduleReq){

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime startAt = LocalDateTime.parse(postScheduleReq.getWhen()+ " " +postScheduleReq.getStartAt(),formatter);
        LocalDateTime endAt = LocalDateTime.parse(postScheduleReq.getWhen()+ " " +postScheduleReq.getEndAt(),formatter);

        return new Schedule(
                user,
                postScheduleReq.getTitle(),
                postScheduleReq.getContent(),
                endAt,
                startAt,
                Status.ACTIVE
        );
    }


}
