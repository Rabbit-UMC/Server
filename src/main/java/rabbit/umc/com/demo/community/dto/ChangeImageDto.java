package rabbit.umc.com.demo.community.dto;

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
