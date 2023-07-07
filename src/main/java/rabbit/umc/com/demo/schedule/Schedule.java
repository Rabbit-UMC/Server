package rabbit.umc.com.demo.schedule;

import lombok.Getter;
import lombok.Setter;
import rabbit.umc.com.demo.Status;
import rabbit.umc.com.demo.user.Domain.User;

import javax.persistence.*;
import java.sql.Date;
import java.sql.Timestamp;

@Entity
@Getter@Setter
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

}
