package rabbit.umc.com.demo.converter;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import rabbit.umc.com.demo.community.dto.CommunityHomeRes.MainMissionDto;
import rabbit.umc.com.demo.community.dto.CommunityHomeResV2.MainMissionDtoV2;
import rabbit.umc.com.demo.mainmission.MainMissionService;
import rabbit.umc.com.demo.mainmission.domain.MainMission;
import rabbit.umc.com.demo.mainmission.domain.mapping.LikeMissionProof;
import rabbit.umc.com.demo.mainmission.domain.mapping.MainMissionProof;
import rabbit.umc.com.demo.mainmission.domain.mapping.MainMissionUsers;
import rabbit.umc.com.demo.mainmission.dto.GetMainMissionRes.MissionProofImageDto;
import rabbit.umc.com.demo.mainmission.dto.GetMainMissionRes.RankDto;
import rabbit.umc.com.demo.user.Domain.User;
import rabbit.umc.com.utils.DateUtil;


@RequiredArgsConstructor
public class MainMissionConverter {

    public static List<MainMissionDto> toMainMissionDtoList(List<MainMission> missionList){
        return missionList
                .stream()
                .map(mainMission -> MainMissionDto.builder()
                        .mainMissionId(mainMission.getId())
                        .mainMissionTitle(mainMission.getTitle())
                        .categoryImage(mainMission.getCategory().getImage())
                        .categoryName(mainMission.getCategory().getName())
                        .dDay(DateUtil.calculateDDay(mainMission.getEndAt()))
                        .build())
                .collect(Collectors.toList());
    }

    public static List<MissionProofImageDto> toMissionProofImageDto(List<MainMissionProof> mainMissionProofs){
        return mainMissionProofs
                .stream()
                .map(mainMissionProof -> MissionProofImageDto.builder()
                        .imageId(mainMissionProof.getId())
                        .userId(mainMissionProof.getUser().getId())
                        .filePath(mainMissionProof.getProofImage())
                        .isLike(false)
                        .build())
                .collect(Collectors.toList());
    }

    public static LikeMissionProof toLikeMissionProof(User user, MainMissionProof mainMissionProof){
        return LikeMissionProof.builder()
                .user(user)
                .mainMissionProof(mainMissionProof)
                .build();
    }

    public static RankDto toRankDto(MainMissionUsers missionUsers){
        return RankDto.builder()
                .userId(missionUsers.getId())
                .userName(missionUsers.getUser().getUserName())
                .build();
    }

}
