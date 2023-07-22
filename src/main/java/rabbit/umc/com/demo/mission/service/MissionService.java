package rabbit.umc.com.demo.mission.service;

import rabbit.umc.com.config.BaseException;
import rabbit.umc.com.demo.mission.dto.*;

import java.util.List;

public interface MissionService {
    List<MissionHomeRes> getMissionHome();

    void postMission(PostMissionReq postMissionReq, Long userId);

    List<MissionHomeRes> getMissionByMissionCategoryId(Long categoryId);

    List<MissionHomeRes> getSuccessMissions(Long userId);

    List<GetMyMissionRes> getMyMissions(long userId);

    GetMissionDetailDto getMyMissionDetail(long userId, long missionId) throws BaseException;

    List<GetMyMissionSchedule> getMyMissionSchedules(long userId, long missionId);

    void deleteMyMissoin(long missionId, long userId);

    void reportMission(long missionId,long userId) throws Exception;

    void togetherMission(long missionId, long userId) throws BaseException;

    GetMissionDetailDto getMissionDetail(Long missionId) throws BaseException;

    List<MissionHomeRes> getFailureMissions(Long userId);

    List<MissionCategoryRes> getMissionCategory();
}
