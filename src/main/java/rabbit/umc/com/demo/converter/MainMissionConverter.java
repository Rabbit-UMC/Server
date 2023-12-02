package rabbit.umc.com.demo.converter;

import java.util.List;
import java.util.stream.Collectors;
import rabbit.umc.com.demo.community.article.ArticleService;
import rabbit.umc.com.demo.community.dto.CommunityHomeRes.MainMissionDto;
import rabbit.umc.com.demo.mainmission.domain.MainMission;

public class MainMissionConverter {

    public static List<MainMissionDto> toMainMissionDtoList(List<MainMission> missionList){
        return missionList
                .stream()
                .map(mainMission -> MainMissionDto.builder()
                        .mainMissionId(mainMission.getId())
                        .mainMissionTitle(mainMission.getTitle())
                        .categoryImage(mainMission.getCategory().getImage())
                        .categoryName(mainMission.getCategory().getName())
                        .dDay(ArticleService.calculateDDay(mainMission.getEndAt()))
                        .build())
                .collect(Collectors.toList());
    }
}
