package rabbit.umc.com.demo.mission.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rabbit.umc.com.config.BaseException;
import rabbit.umc.com.demo.Status;
import rabbit.umc.com.demo.mission.Mission;
import rabbit.umc.com.demo.mission.MissionCategory;
import rabbit.umc.com.demo.mission.MissionUsers;
import rabbit.umc.com.demo.mission.dto.*;
import rabbit.umc.com.demo.mission.repository.MissionCategoryRepository;
import rabbit.umc.com.demo.mission.repository.MissionRepository;
import rabbit.umc.com.demo.mission.repository.MissionUsersRepository;
import rabbit.umc.com.demo.report.Report;
import rabbit.umc.com.demo.report.ReportRepository;
import rabbit.umc.com.demo.schedule.domain.MissionSchedule;
import rabbit.umc.com.demo.schedule.domain.Schedule;
import rabbit.umc.com.demo.schedule.repository.MissionScheduleRepository;
import rabbit.umc.com.demo.schedule.repository.ScheduleRepository;
import rabbit.umc.com.demo.user.Domain.User;
import rabbit.umc.com.demo.user.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static rabbit.umc.com.config.BaseResponseStatus.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MissionServiceImpl implements MissionService{

    private final MissionRepository missionRepository;
    private final UserRepository userRepository;
    private final MissionCategoryRepository missionCategoryRepository;
    private final MissionUsersRepository missionUserRepository;
    private final MissionScheduleRepository missionScheduleRepository;
    private final ScheduleRepository scheduleRepository;
    private final ReportRepository reportRepository;



    @Override
    public List<MissionHomeRes> getMissionHome() {
        LocalDateTime now =  LocalDateTime.now();
        List<Mission> missionList = missionRepository.getMissionsByEndAtAfterAndIsOpenOrderByEndAt(now,0);

        List<MissionHomeRes> resultList = missionList.stream()
                .map(MissionHomeRes::toMissionHomeRes)
                .collect(Collectors.toList());

        return resultList;
    }

    /**
     * 미션 카테고리별 확인
     */
    @Override
    public List<MissionHomeRes> getMissionByMissionCategoryId(Long categoryId) {

        List<Mission> missionList = missionRepository.getMissionByMissionCategoryIdOrderByEndAt(categoryId);
        List<MissionHomeRes> resultList = missionList.stream()
                .map(MissionHomeRes::toMissionHomeRes)
                .collect(Collectors.toList());

        return resultList;
    }

    @Override
    public List<MissionHomeRes> getSuccessMissions(Long userId) {
        // 5일차면 5일동안 일정에 있으면 성공!
        // 내가 참여중인 미션 리스트 가져오기
        List<MissionUsers> missionUsersList = missionUserRepository.getMissionUsersByUserId(userId);

        List<Long> ids = new ArrayList<>();



        Status status = Status.valueOf("ACTIVE");
        List<MissionSchedule> missionSchedules;
        LocalDateTime now =  LocalDateTime.now();
        for (MissionUsers missionUser : missionUsersList) {
            int successCnt = 0;
            missionSchedules = missionScheduleRepository.getMissionScheduleByMissionId(missionUser.getMission().getId());
            for(MissionSchedule ms : missionSchedules){
                Schedule schedule = scheduleRepository.findScheduleById(ms.getSchedule().getId());
                if(schedule.getUser().getId() == userId)
                    successCnt++;
            }

            Mission mission = missionRepository.getMissionByIdAndEndAtIsBeforeOrderByEndAt(missionUser.getMission().getId(), now);
            if(mission != null){
                LocalDate targetDate = mission.getEndAt().toLocalDate();
                LocalDate currentDate = mission.getStartAt().toLocalDate();
                int targetCnt; // 현재 날짜와 대상 날짜 사이의 일 수 계산
                targetCnt = (int) ChronoUnit.DAYS.between(currentDate,targetDate);
                System.out.println("missionUser.getMission().getId() = " + missionUser.getMission().getId());
                System.out.println("successCnt = " + successCnt);
                System.out.println("targetCnt = " + targetCnt);
                if(successCnt >= targetCnt+1){
                    ids.add(missionUser.getMission().getId());
                }
            }
        }

        List<Mission> missionList = missionRepository.getMissionsByIdIsIn(ids);
        List<MissionHomeRes> resultList = missionList.stream()
                .map(MissionHomeRes::toMissionHomeRes)
                .collect(Collectors.toList());

        return resultList;
    }

    /**
     *
     * @param userId
     * @return
     */
    @Override
    public List<GetMyMissionRes> getMyMissions(long userId) {
        List<MissionUsers> missionUsersList = missionUserRepository.getMissionUsersByUserId(userId);

        List<Mission> missionList = new ArrayList<>();
        LocalDateTime currentDateTime = LocalDateTime.now();



        for (MissionUsers mu :missionUsersList) {
            Mission mission = missionRepository.getMissionByIdAndEndAtIsAfterOrderByEndAt(mu.getMission().getId(), currentDateTime);
            if(mission != null)
                missionList.add(mission);
        }

        if(missionList.isEmpty()){
            return null;
        }else{
            List<GetMyMissionRes> resultList = missionList.stream()
                    .map(GetMyMissionRes::toMyMissions)
                    .collect(Collectors.toList());

            return resultList;
        }

    }

    @Override
    public GetMissionDetailDto getMyMissionDetail(long userId, long missionId) throws BaseException {
        MissionUsers missionUsers = missionUserRepository.getMissionUsersByMissionIdAndUserId(missionId, userId);
        if (missionUsers == null || missionUsers.getMission() == null) {
            // 내 미션이 없는 경우
            throw new BaseException(FAILED_TO_MISSION);
        }
        Mission mission = missionRepository.getMissionById(missionUsers.getMission().getId());
        if (mission == null) {
            // 미션을 찾을 수 없는 경우
            throw new BaseException(FAILED_TO_MISSION);
        }
        return GetMissionDetailDto.toGetMissionDetaliDto(mission);
    }

    @Override
    public List<GetMyMissionSchedule> getMyMissionSchedules(long userId, long missionId) {
        List<MissionSchedule> missionSchedules = missionScheduleRepository.findMissionSchedulesByMissionId(missionId);

        List<Schedule> schedules = new ArrayList<>();
        Schedule schedule;
        for (MissionSchedule ms: missionSchedules) {
            schedule = scheduleRepository.findScheduleById(ms.getSchedule().getId());
            if(schedule.getUser().getId() == userId)
                schedules.add(schedule);
        }

        List<GetMyMissionSchedule> resultList = schedules.stream()
                .map(GetMyMissionSchedule::toGetMyMissionSchedule)
                .collect(Collectors.toList());

        return resultList;
    }

    @Override
    @Transactional
    public void deleteMyMissoin(long missionId, long userId) {
        MissionUsers missionUsers = missionUserRepository.getMissionUsersByMissionIdAndUserId(missionId,userId);
        missionUserRepository.delete(missionUsers);
    }

    @Override
    @Transactional
    public void reportMission(long missionId,long userId) throws Exception {
        Mission mission = missionRepository.getMissionById(missionId);
        
        if(mission == null){
            throw  new Exception(String.valueOf(FAILED_TO_MISSION));
        }

        User user = userRepository.getReferenceById(userId);
        Report existingReport = reportRepository.findReportByUserIdAndMissionId(userId, missionId);
        if(existingReport != null){
            throw new Exception(String.valueOf(FAILED_TO_REPORT));
        }


        if(mission != null){
            Report report = new Report();
            report.setUser(user);
            report.setMission(mission);
            reportRepository.save(report);
        }
    }

    @Override
    @Transactional
    public void togetherMission(long missionId, long userId) throws BaseException {
        User user = userRepository.getReferenceById(userId);

        Mission mission = missionRepository.getReferenceById(missionId);
        MissionUsers missionUsers = new MissionUsers();

        if(missionUserRepository.getMissionUsersByMissionIdAndUserId(missionId,userId) == null){
            missionUsers.setUser(user);
            missionUsers.setMission(mission);
            missionUserRepository.save(missionUsers);
        }else{
            throw new BaseException(FAILED_TO_TOGETHER_MISSION);
        }
    }

    @Override
    public GetMissionDetailDto getMissionDetail(Long missionId) throws BaseException {

        Mission mission = missionRepository.getMissionById(missionId);
        if (mission == null) {
            // 미션을 찾을 수 없는 경우
            throw new BaseException(FAILED_TO_MISSION);
        }

        return GetMissionDetailDto.toGetMissionDetaliDto(mission);
    }

    /**
     * 도전 실패한 미션리스트
     * @param userId
     * @return
     */
    @Override
    public List<MissionHomeRes> getFailureMissions(Long userId) {
        // 5일차면 5일동안 일정에 있으면 성공!
        // 내가 참여중인 미션 리스트 가져오기
        List<MissionUsers> missionUsersList = missionUserRepository.getMissionUsersByUserId(userId);

        List<Long> ids = new ArrayList<>();


        int targetCnt = 0;
        Status status = Status.valueOf("ACTIVE");
        Mission mission = new Mission();
        List<MissionSchedule> missionSchedules;
        LocalDateTime now =  LocalDateTime.now();
        for (MissionUsers missionUser : missionUsersList) {
            int successCnt = 0;
            missionSchedules = missionScheduleRepository.getMissionScheduleByMissionId(missionUser.getMission().getId());
            for(MissionSchedule ms : missionSchedules){
                Schedule schedule = scheduleRepository.findScheduleById(ms.getSchedule().getId());
                if(schedule.getUser().getId() == userId)
                    successCnt++;
            }

            mission = missionRepository.getMissionByIdAndEndAtIsBefore(missionUser.getMission().getId(), now);
            if(mission != null){
                LocalDate targetDate = mission.getEndAt().toLocalDate();
                LocalDate currentDate = mission.getStartAt().toLocalDate();
                targetCnt = (int) ChronoUnit.DAYS.between(currentDate,targetDate); // 현재 날짜와 대상 날짜 사이의 일 수 계산

                if(successCnt < targetCnt+1){
                    ids.add(missionUser.getMission().getId());
                }
            }
        }

        List<Mission> missionList = missionRepository.getMissionsByIdIsIn(ids);
        List<MissionHomeRes> resultList = missionList.stream()
                .map(MissionHomeRes::toMissionHomeRes)
                .collect(Collectors.toList());

        return resultList;
    }

    /**
     * 미션 등록시 미션 카테고리명 리스트 보기
     * @return
     */
    @Override
    public List<MissionCategoryRes> getMissionCategory() {
        Status status = Status.valueOf("ACTIVE");
        List<MissionCategory> missionCategoryList = missionCategoryRepository.getAllByStatusIs(status);

        List<MissionCategoryRes> resultList = missionCategoryList.stream()
                .map(MissionCategoryRes::toMissionCategoryRes)
                .collect(Collectors.toList());

        return resultList;
    }

    @Override
    @Transactional
    public void postMission(PostMissionReq postMissionReq, Long userId) {
        MissionUsers missionUsers = new MissionUsers();

        Mission mission = new Mission();
        mission.setMission(postMissionReq);

        User user = userRepository.getReferenceById(userId);
        MissionCategory missionCategory = missionCategoryRepository.getReferenceById(postMissionReq.getCategoryId());
        mission.setMissionCategory(missionCategory);
        missionRepository.save(mission);

        missionUsers.setMission(mission);
        missionUsers.setUser(user);
        missionUserRepository.save(missionUsers);
    }


}
