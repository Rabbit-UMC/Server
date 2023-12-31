package rabbit.umc.com.demo.converter;

import rabbit.umc.com.demo.mainmission.domain.mapping.MainMissionUsers;
import rabbit.umc.com.demo.mainmission.dto.GetMainMissionRes.RankDto;

public class RankConverter {

    public static RankDto toRankDto(MainMissionUsers missionUsers){
        return RankDto.builder()
                .userId(missionUsers.getId())
                .userName(missionUsers.getUser().getUserName())
                .build();
    }
}
