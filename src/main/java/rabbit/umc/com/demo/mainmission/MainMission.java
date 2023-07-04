package rabbit.umc.com.demo.mainmission;

import lombok.Getter;
import lombok.Setter;
import rabbit.umc.com.demo.Status;
import rabbit.umc.com.demo.article.Category;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Getter@Setter
@Table(name = "main_mission")
public class MainMission {
    @Id @GeneratedValue
    @Column(name = "main_mission_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    private Boolean lastMission;

    private String title;
    private String content;

    private Timestamp startAt;
    private Timestamp endAt;

    @Enumerated(EnumType.STRING)
    private Status status;
    private Timestamp createdAt;
    private Timestamp updatedAt;




}
