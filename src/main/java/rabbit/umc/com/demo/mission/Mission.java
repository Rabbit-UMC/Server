package rabbit.umc.com.demo.mission;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import rabbit.umc.com.demo.Status;
import rabbit.umc.com.demo.article.domain.Category;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

@Entity
@Getter
@Setter
public class Mission {
    @Id @GeneratedValue
    @Column(name = "mission_id")
    private Long id;

    private String title;
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "mission",cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MissionUsers> missionUsers;

    @Column(columnDefinition = "boolean default false")
    private Boolean isOpen;

    @Enumerated(EnumType.STRING)
    private Status status;
    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp createdAt;
    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private Timestamp updatedAt;

    @Column
    private Timestamp startAt;
    private Timestamp endAt;

    private String image;


}
