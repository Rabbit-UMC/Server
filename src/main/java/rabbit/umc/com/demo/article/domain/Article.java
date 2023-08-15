package rabbit.umc.com.demo.article.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import rabbit.umc.com.config.BaseTimeEntity;
import rabbit.umc.com.demo.Status;


import rabbit.umc.com.demo.article.dto.PostArticleReq;



import rabbit.umc.com.demo.user.Domain.User;


import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

import static javax.persistence.CascadeType.*;
import static javax.persistence.GenerationType.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "article")
public class Article extends BaseTimeEntity {
    @Id@GeneratedValue(strategy = IDENTITY)
    @Column(name = "article_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ApiModelProperty(value="게시물 작성자", example = "idx", required = true)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ApiModelProperty(value="게시물 제목", example = "안녕하세요~", required = true)
    @Column(nullable = false)
    private String title;

    @ApiModelProperty(value="게시물 내용", example = "안녕하세요 잘부탁드립니다!", required = true)
    @Column(nullable = false)
    private String content;

    @OneToMany(mappedBy = "article", cascade = ALL, orphanRemoval = true)
    private List<Comment> comments;

    @OneToMany(mappedBy = "article", cascade = ALL, orphanRemoval = true)
    private List<LikeArticle> likeArticles;

    @OneToMany(mappedBy = "article", cascade = ALL, orphanRemoval = true)
    private List<Image> images;

    @Enumerated(EnumType.STRING)
    private Status status = Status.ACTIVE;



    public void setArticle(PostArticleReq postArticleReq) {
        title = postArticleReq.getArticleTitle();
        content = postArticleReq.getArticleContent();
    }


}
