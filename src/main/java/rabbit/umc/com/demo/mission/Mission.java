package rabbit.umc.com.demo.mission;

import lombok.Getter;
import lombok.Setter;
import rabbit.umc.com.demo.base.BaseTimeEntity;
import rabbit.umc.com.demo.base.Status;
import rabbit.umc.com.demo.community.domain.Category;
import rabbit.umc.com.demo.mission.dto.PostMissionReq;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "missions")
public class Mission extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "missions_id")
    private Long id;
    private String title;
    private String content;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id",nullable = false)
    private Category category;

    @OneToMany(mappedBy = "mission",cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MissionUsers> missionUsers;

    @OneToMany(mappedBy = "mission",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<MissionUserSuccess> missionUserSuccessList;

    @Column
    private int isOpen;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status = Status.ACTIVE;

    @Column(nullable = false)
    private LocalDateTime startAt;
    @Column(nullable = false)
    private LocalDateTime endAt;

    public void toMission(PostMissionReq postMissionReq, Category category){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime startAt = LocalDate.parse(postMissionReq.getStartAt(),formatter).atStartOfDay();
        LocalDateTime endAt = LocalDate.parse(postMissionReq.getEndAt(),formatter).atStartOfDay();

        this.title = postMissionReq.getTitle();
        this.content = postMissionReq.getContent();
        this.startAt = startAt;
        this.endAt = endAt;
        this.isOpen = postMissionReq.getIsOpen();
        this.category = category;
    }



}
