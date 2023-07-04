package rabbit.umc.com.demo.mission;

import lombok.Getter;
import lombok.Setter;
import rabbit.umc.com.demo.Status;
import rabbit.umc.com.demo.article.Category;

import javax.persistence.*;
import java.sql.Timestamp;

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

    private Boolean isOpen;

    @Enumerated(EnumType.STRING)
    private Status status;
    private Timestamp createdAt;
    private Timestamp updatedAt;


}
