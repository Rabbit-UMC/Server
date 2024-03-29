package rabbit.umc.com.demo.mission.service;

import rabbit.umc.com.config.apiPayload.BaseException;
import rabbit.umc.com.demo.mission.dto.*;

import java.util.List;

public interface MissionService {
    List<MissionHomeRes> getMissionHome(int page) throws BaseException;

    void postMission(PostMissionReq postMissionReq, Long userId) throws BaseException;

    List<MissionHomeRes> getMissionByMissionCategoryId(Long categoryId, int page) throws BaseException;

    MissionHistoryRes getSuccessMissions(Long userId);

    List<GetMyMissionRes> getMyMissions(long userId);

    GetMissionDetailDto getMyMissionDetail(long userId, long missionId) throws BaseException;

    List<GetMyMissionSchedule> getMyMissionSchedules(long userId, long missionId) throws BaseException;

    void deleteMyMission(List<Long> missionIds, long userId) throws BaseException;

    void reportMission(long missionId,long userId) throws BaseException;

    void togetherMission(long missionId, long userId) throws BaseException;

    GetMissionDetailDto getMissionDetail(Long missionId, Long userId) throws BaseException;

    MissionHistoryRes getFailureMissions(Long userId);

    List<MissionCategoryRes> getMissionCategory();

    void deleteMyMissoinAndSchedules(String missionId, List<Long> scheduleIds, long userId) throws BaseException;
}
