package rabbit.umc.com.demo.converter;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import rabbit.umc.com.demo.Status;
import rabbit.umc.com.demo.community.domain.Category;
import rabbit.umc.com.demo.community.dto.CommunityHomeRes.MainMissionDto;
import rabbit.umc.com.demo.community.dto.CommunityHomeResV2.MainMissionDtoV2;
import rabbit.umc.com.demo.mainmission.domain.MainMission;
import rabbit.umc.com.demo.mainmission.domain.mapping.LikeMissionProof;
import rabbit.umc.com.demo.mainmission.domain.mapping.MainMissionProof;
import rabbit.umc.com.demo.mainmission.domain.mapping.MainMissionUsers;
import rabbit.umc.com.demo.mainmission.dto.GetMainMissionRes;
import rabbit.umc.com.demo.mainmission.dto.GetMainMissionRes.MissionProofImageDto;
import rabbit.umc.com.demo.mainmission.dto.GetMainMissionRes.RankDto;
import rabbit.umc.com.demo.mainmission.dto.MainMissionViewRes;
import rabbit.umc.com.demo.mainmission.dto.PostMainMissionReq;
import rabbit.umc.com.demo.user.Domain.User;
import rabbit.umc.com.utils.DateUtil;


@RequiredArgsConstructor
public class MainMissionConverter {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

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

    public static GetMainMissionRes toGetMainMissionRes(MainMission mainMission, List<MissionProofImageDto> missionProofImages, List<RankDto> rank){
        return GetMainMissionRes.builder()
                .mainMissionId(mainMission.getId())
                .mainMissionName(mainMission.getTitle())
                .startDay(mainMission.getStartAt().format(DATE_TIME_FORMATTER))
                .dDay(DateUtil.getMissionDday(mainMission.getEndAt()))
                .mainMissionContent(mainMission.getContent())
                .rank(rank)
                .missionProofImages(missionProofImages)
                .build();
    }

    public static MainMission toMainMission(PostMainMissionReq postMainMissionReq, Category category){
        return MainMission.builder()
                .category(category)
                .startAt(postMainMissionReq.getMissionStartTime())
                .endAt(postMainMissionReq.getMissionEndTime())
                .title(postMainMissionReq.getMainMissionTitle())
                .content(postMainMissionReq.getMainMissionContent())
                .lastMission(postMainMissionReq.getLastMission())
                .status(Status.ACTIVE)
                .build();
    }

    public static MainMissionUsers toMainMissionUsers(User user, MainMission mainMission){
        return MainMissionUsers.builder()
                .user(user)
                .mainMission(mainMission)
                .build();
    }

    public static MainMissionProof toMainMissionProof(String filePath, User user, MainMission mainMission){
        return MainMissionProof.builder()
                .proofImage(filePath)
                .user(user)
                .mainMission(mainMission)
                .build();
    }

    public static MainMissionViewRes toMainMissionViewRes(User user, MainMission mainMission){
        return MainMissionViewRes.builder()
                .userName(user.getUserName())
                .missionImageUrl(mainMission.getCategory().getImage())
                .missionTitle(mainMission.getTitle())
                .missionStartDay(DateUtil.getMonthDay(mainMission.getStartAt()))
                .missionEndDay(DateUtil.getMonthDay(mainMission.getEndAt()))
                .memo(mainMission.getContent())
                .build();
    }

    public static MainMissionDtoV2 toMainMissionDtoV2(MainMission mainMission, String hostUserName){
        return MainMissionDtoV2.builder()
                .mainMissionId(mainMission.getId())
                .mainMissionTitle(mainMission.getTitle())
                .dDay(DateUtil.calculateDDay(mainMission.getEndAt()))
                .hostUserName(hostUserName)
                .missionCategoryId(mainMission.getCategory().getId())
                .build();
    }

}
