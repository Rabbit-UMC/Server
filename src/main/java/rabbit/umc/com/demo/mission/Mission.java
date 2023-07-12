package rabbit.umc.com.demo.mission;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import rabbit.umc.com.config.BaseTimeEntity;
import rabbit.umc.com.demo.Status;
import rabbit.umc.com.demo.article.domain.Category;
import rabbit.umc.com.demo.mission.dto.PostMissionReq;
import rabbit.umc.com.demo.schedule.domain.MissionSchedule;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Entity
@Getter
@Setter
public class Mission extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mission_id")
    private Long id;

    private String title;
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mission_category_id")
    private MissionCategory missionCategory;

    @OneToMany(mappedBy = "mission",cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MissionUsers> missionUsers;

    @Column
    private int isOpen;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", columnDefinition = "varchar(20) default 'ACTIVE'")
    private Status status;

    @Column(nullable = false)
    private LocalDateTime startAt;
    @Column(nullable = false)
    private LocalDateTime endAt;

    public void setMission(PostMissionReq postMissionReq){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime startAt = LocalDate.parse(postMissionReq.getStartAt(),formatter).atStartOfDay();
        LocalDateTime endAt = LocalDate.parse(postMissionReq.getEndAt(),formatter).atStartOfDay();

        this.title = postMissionReq.getTitle();
        this.content = postMissionReq.getContent();
        this.startAt = startAt;
        this.endAt = endAt;
        this.isOpen = postMissionReq.getIsOpen();
    }


}
