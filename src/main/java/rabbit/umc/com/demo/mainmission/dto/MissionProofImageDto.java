package rabbit.umc.com.demo.mainmission.dto;

import lombok.*;
import rabbit.umc.com.demo.mainmission.domain.mapping.MainMissionProof;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MissionProofImageDto {
    private Long imageId;
    private Long userId;
    private String filePath;
    private Boolean isLike;

    public static MissionProofImageDto toMissionProofImageDto(MainMissionProof mainMissionProof){
        return new MissionProofImageDto(
                mainMissionProof.getId(),
                mainMissionProof.getUser().getId(),
                mainMissionProof.getProofImage(),
                false

        );
    }
}