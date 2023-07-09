package rabbit.umc.com.demo.mission.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rabbit.umc.com.config.BaseResponse;
import rabbit.umc.com.demo.mission.Mission;
import rabbit.umc.com.demo.mission.dto.MissionHomeRes;
import rabbit.umc.com.demo.mission.service.MissionService;

import java.util.List;

@RestController
@RequestMapping("/app/mission")
@RequiredArgsConstructor
public class MissionController {

    private final MissionService missionService;

    /**
     * 미션 홈
     */
    @GetMapping()
    public BaseResponse<List<MissionHomeRes>> getHome(){

        List<MissionHomeRes> resultList = missionService.getMissionHome();


        return new BaseResponse<>(resultList);
    }

    /**
     * 미션 카테고리 별로 확인
     */
    @GetMapping("/{categoryId}")
    public BaseResponse<List<MissionHomeRes>> getHomeByCategoryId(@PathVariable(name = "categoryId") Long categoryId){

        List<MissionHomeRes> resultList = missionService.getMissionByCategoryId(categoryId);

        return new BaseResponse<>(resultList);
    }
}
