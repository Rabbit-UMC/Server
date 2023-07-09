package rabbit.umc.com.demo.article.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import rabbit.umc.com.config.BaseTimeEntity;
import rabbit.umc.com.demo.Status;



import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Getter@Setter
@Table(name = "image")
public class Image extends BaseTimeEntity {
    @Id@GeneratedValue
    @Column(name = "image_id")
    private Long id;

    @Column(nullable = false)
    private String filePath;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id",nullable = false)
    private Article article;

    @Enumerated(EnumType.STRING)
    private Status status = Status.ACTIVE ;


    public void setImage(String filePath) {
        this.filePath = filePath;

    }
}
