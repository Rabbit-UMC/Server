package rabbit.umc.com.demo.article.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class ArticleListsRes {

    private String categoryImage;
    private Long mainMissionId;
    private Long categoryHostId;
    List<ArticleListDto> articleLists;


    public void setArticleLists (String categoryImage, Long mainMissionId, Long hostId, List<ArticleListDto> articleLists){
        this.categoryImage = categoryImage;
        this.mainMissionId = mainMissionId;
        this.categoryHostId = hostId;
        this.articleLists = articleLists;
    }
}
