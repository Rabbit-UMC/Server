package rabbit.umc.com.demo.image;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class GenerateImageResDto {

    private Long imageId;
    private String imageUrl;

}
