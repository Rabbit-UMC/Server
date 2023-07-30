package rabbit.umc.com.demo.article.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class ArticleListsRes {

    private Long categoryHostId;
    List<ArticleListDto> articleLists;


    public void setArticleLists (Long hostId, List<ArticleListDto> articleLists){
        this.categoryHostId = hostId;
        this.articleLists = articleLists;
    }
}
