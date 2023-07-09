package rabbit.umc.com.demo.mission.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import rabbit.umc.com.demo.mission.Mission;
import rabbit.umc.com.demo.mission.dto.MissionHomeRes;
import rabbit.umc.com.demo.mission.repository.MissionRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MissionServiceImpl implements MissionService{

    private final MissionRepository missionRepository;

    @Override
    public List<MissionHomeRes> getMissionHome() {

        List<Mission> missionList = missionRepository.getHome();
        List<MissionHomeRes> resultList = missionList.stream()
                .map(MissionHomeRes::toMissionHomeRes)
                .collect(Collectors.toList());

        return resultList;
    }

    @Override
    public List<MissionHomeRes> getMissionByCategoryId(Long categoryId) {
        List<Mission> missionList = missionRepository.getMissionByCategoryId(categoryId);
        List<MissionHomeRes> resultList = missionList.stream()
                .map(MissionHomeRes::toMissionHomeRes)
                .collect(Collectors.toList());

        return resultList;
    }


}
