package rabbit.umc.com.demo.community.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PatchArticleReq {
    private String articleTitle;
    private String articleContent;
    private List<ChangeImageDto> imageList;

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ChangeImageDto {
        private String filePath;
    }
}
