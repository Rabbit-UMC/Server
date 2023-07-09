package rabbit.umc.com.demo.article.dto;

import lombok.*;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangeImageDto {
    private Long imageId;
    private String filePath;
}
