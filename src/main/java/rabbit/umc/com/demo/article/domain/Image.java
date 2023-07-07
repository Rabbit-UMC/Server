package rabbit.umc.com.demo.article.domain;

import lombok.Getter;
import lombok.Setter;
import rabbit.umc.com.demo.Status;
//<<<<<<< HEAD
//import rabbit.umc.com.demo.article.domain.Article;
//=======
//import rabbit.umc.com.demo.article.dto.PostArticleReq;
//>>>>>>> 7fcf3bb43902111068ac93fc8de16eaacd49b844

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Getter@Setter
@Table(name = "image")
public class Image {
    @Id@GeneratedValue
    @Column(name = "image_id")
    private Long id;

    @Column(nullable = false)
    private String filePath;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id",nullable = false)
    private Article article;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;
    @Column(nullable = false)
    private Timestamp createdAt;
    @Column(nullable = false)
    private Timestamp updatedAt;

    public void setImage(String filePath) {
        this.filePath = filePath;

    }
}
