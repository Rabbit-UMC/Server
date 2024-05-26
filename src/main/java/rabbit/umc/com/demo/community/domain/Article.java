package rabbit.umc.com.demo.community.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import rabbit.umc.com.demo.base.BaseTimeEntity;
import rabbit.umc.com.demo.base.Status;


import rabbit.umc.com.demo.community.domain.mapping.LikeArticle;


import rabbit.umc.com.demo.image.domain.Image;
import rabbit.umc.com.demo.user.Domain.User;


import javax.persistence.*;
import java.util.List;

import static javax.persistence.CascadeType.*;
import static javax.persistence.GenerationType.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
    @Builder.Default
    private Status status = Status.ACTIVE;

    @ColumnDefault("0")
    private int likeCount;

    @ColumnDefault("0")
    private int commentCount;

    public void setInactive(){
        this.status = Status.INACTIVE;
    }

    public void updateArticle(String title, String content){
        this.title = title;
        this.content = content;
    }

    public void updateLikeCountUp(){
        this.likeCount++;
    }

    public void updateLikeCountDown(){
        this.likeCount--;
    }

    public void updateCommentCountUp(){
        this.commentCount++;
    }

    public void updateCommentCountDown(){
        this.commentCount--;
    }

}
