package rabbit.umc.com.demo.mission.service;

import rabbit.umc.com.demo.mission.Mission;
import rabbit.umc.com.demo.mission.dto.MissionHomeRes;

import java.util.List;

public interface MissionService {
    List<MissionHomeRes> getMissionHome();

    List<MissionHomeRes> getMissionByCategoryId(Long categoryId);
}
