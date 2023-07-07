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
@Getter@Setter
@Table(name = "mission")
public class Mission {
    @Id@GeneratedValue
    @Column(name = "mission")
    private Long id;

    private String title;
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "mission")
    private List<MissionUsers> missionUsersList;

    @Column(columnDefinition = "false")
    private Boolean isOpen;

    @Enumerated(EnumType.STRING)
    private Status status;
    @Column
    private Timestamp createdAt;
    private Timestamp updatedAt;

    @Column
    private Timestamp startAt;
    private Timestamp endAt;


}
