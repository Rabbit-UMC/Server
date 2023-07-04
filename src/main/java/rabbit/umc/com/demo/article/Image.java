package rabbit.umc.com.demo.article;

import lombok.Getter;
import lombok.Setter;
import rabbit.umc.com.demo.Status;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Getter@Setter
@Table(name = "image")
public class Image {
    @Id@GeneratedValue
    @Column(name = "image_id")
    private Long id;

    private String filePath;

    @ManyToOne(fetch = FetchType.LAZY)
    private Article article;

    @Enumerated(EnumType.STRING)
    private Status status;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
