package rabbit.umc.com.demo.mission.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rabbit.umc.com.config.BaseException;
import rabbit.umc.com.config.BaseResponseStatus;
import rabbit.umc.com.demo.Status;
import rabbit.umc.com.demo.article.CategoryRepository;
import rabbit.umc.com.demo.article.domain.Category;
import rabbit.umc.com.demo.mission.Mission;
import rabbit.umc.com.demo.mission.MissionUserSuccess;
import rabbit.umc.com.demo.mission.MissionUsers;
import rabbit.umc.com.demo.mission.dto.*;
import rabbit.umc.com.demo.mission.repository.MissionRepository;
import rabbit.umc.com.demo.mission.repository.MissionUserSuccessRepository;
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
import java.util.stream.IntStream;

import static rabbit.umc.com.config.BaseResponseStatus.*;
import static rabbit.umc.com.demo.Status.ACTIVE;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MissionServiceImpl implements MissionService{

    private final MissionRepository missionRepository;
    private final UserRepository userRepository;
    private final MissionUsersRepository missionUserRepository;
    private final MissionScheduleRepository missionScheduleRepository;
    private final ScheduleRepository scheduleRepository;
    private final ReportRepository reportRepository;
    private final CategoryRepository categoryRepository;
    private final MissionUserSuccessRepository missionUserSuccessRepository;



    @Override
    public List<MissionHomeRes> getMissionHome() {
        LocalDateTime now =  LocalDateTime.now();
        List<Mission> missionList = missionRepository.getMissions(now,0, ACTIVE);



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
    public void deleteMyMissoin(List<Long> missionIds, long userId) throws BaseException {
        List<MissionUsers> missionUsers = missionUserRepository.getMissionUsersByMissionIdAndUserId(missionIds,userId);
        if(missionUsers.size() == 0 || missionUsers.size() != missionIds.size())
            throw new BaseException(FAILED_TO_MISSION);
        missionUsers.forEach(id -> missionUserRepository.delete(id));
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

        if (user == null) {
            throw new BaseException(USERS_EMPTY_USER_ID);
        }

        Mission mission = missionRepository.getMissionById(missionId);
        System.out.println("mission == null = " + mission == null);

        if (mission == null) {
            throw new BaseException(DONT_EXIST_MISSION);
        }

        if (missionUserRepository.getMissionUsersByMissionIdAndUserId(missionId, userId) != null) {
            throw new BaseException(FAILED_TO_TOGETHER_MISSION);
        }


        MissionUsers missionUsers = new MissionUsers();
        missionUsers.setUser(user);
        missionUsers.setMission(mission);
        missionUserRepository.save(missionUsers);
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

    public static List<LocalDate> getDateBetweenTwoDates(LocalDateTime startAt, LocalDateTime endAt) {
        LocalDate startDate = startAt.toLocalDate();
        LocalDate endDate = endAt.toLocalDate();

        int numOfDaysBetween = (int) ChronoUnit.DAYS.between(startDate,endDate);

        return IntStream.iterate(0, i -> i <= numOfDaysBetween, i -> i + 1)
                .mapToObj(i -> startDate.plusDays(i))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public MissionHistoryRes getSuccessMissions(Long userId) {
        Status status = ACTIVE;
        LocalDateTime now =  LocalDateTime.now();
        int totalCnt = 0;

        // 내가 참가한 미션들 가져오기
        List<MissionUsers> missionUsersList = missionUserRepository.getMissionUsersByUserId(userId);
        User user = userRepository.getReferenceById(userId);

        // 미션 인덱스들 담기 위한 리스트
        List<Long> ids = new ArrayList<>();

        List<MissionSchedule> missionSchedules;


        for (MissionUsers missionUser : missionUsersList) {
            int successCnt = 0;
            missionSchedules = missionScheduleRepository.getMissionScheduleByMissionId(missionUser.getMission().getId());

            // 1명의 종료된 미션
            Mission mission = missionRepository.getMissionByIdAndEndAtIsBeforeOrderByEndAt(missionUser.getMission().getId(), now);


            if(mission != null){
                LocalDate targetDate = mission.getEndAt().toLocalDate();
                LocalDate currentDate = mission.getStartAt().toLocalDate();
                int targetCnt; // 현재 날짜와 대상 날짜 사이의 일 수 계산
                targetCnt = (int) ChronoUnit.DAYS.between(currentDate,targetDate);
                totalCnt++;

                // 미션 시작부터 종료 날짜까지의 날짜들
                List<LocalDate> dateList = getDateBetweenTwoDates(mission.getStartAt(),mission.getEndAt());


                for(int i=0; i<missionSchedules.size(); i++){
                    Schedule schedule = scheduleRepository.findScheduleById(missionSchedules.get(i).getSchedule().getId());
                    String whenStr = schedule.getEndAt().toString().substring(0,10);
                    LocalDate when = LocalDate.parse(whenStr);

                    if(schedule.getUser().getId() == userId){
                        if(dateList.contains(when)){
                            successCnt++;
                        }
                    }
                }

                System.out.println("successCnt = " + successCnt);
                System.out.println("targetCnt = " + targetCnt +1);

                if(successCnt == targetCnt+1){
                    ids.add(missionUser.getMission().getId());

                    MissionUserSuccess findMissionUserSuccess = missionUserSuccessRepository.getMissionUserSuccessByMissionIdAndUserId(mission.getId(),userId);

                    if( findMissionUserSuccess == null){
                        MissionUserSuccess missionUserSuccess = new MissionUserSuccess();
                        missionUserSuccess.setUser(user);
                        missionUserSuccess.setMission(mission);
                        missionUserSuccessRepository.save(missionUserSuccess);
                    }

                }
            }
        }

        List<Mission> missionList = missionRepository.getMissionsByIdIsIn(ids);
        List<MissionHomeRes> resultList = missionList.stream()
                .map(MissionHomeRes::toMissionHomeRes)
                .collect(Collectors.toList());

        MissionHistoryRes result = MissionHistoryRes.toMissionHistoryRes(totalCnt,resultList);

        totalCnt = 0;

        return result;
    }



    /**
     * 도전 실패한 미션리스트
     * @param userId
     * @return
     */
    @Override
    public MissionHistoryRes getFailureMissions(Long userId) {
        int targetCnt = 0;
        int totalCnt = 0;
        Status status = ACTIVE;

        List<MissionUsers> missionUsersList = missionUserRepository.getMissionUsersByUserId(userId);

        List<Long> ids = new ArrayList<>();




        List<MissionSchedule> missionSchedules;
        LocalDateTime now =  LocalDateTime.now();


        for (MissionUsers missionUser : missionUsersList) {
            int successCnt = 0;
            missionSchedules = missionScheduleRepository.getMissionScheduleByMissionId(missionUser.getMission().getId());


            Mission mission = missionRepository.getMissionByIdAndEndAtIsBefore(missionUser.getMission().getId(), now);

            if(mission != null){
                mission.setMissionUserSuccessList(missionUserSuccessRepository.getMissionUserSuccessByMissionId(mission.getId()));

                LocalDate targetDate = mission.getEndAt().toLocalDate();
                LocalDate currentDate = mission.getStartAt().toLocalDate();
                targetCnt = (int) ChronoUnit.DAYS.between(currentDate,targetDate); // 현재 날짜와 대상 날짜 사이의 일 수 계산
                totalCnt++;


                List<LocalDate> dateList = getDateBetweenTwoDates(mission.getStartAt(),mission.getEndAt());

                for(MissionSchedule ms : missionSchedules){
                    Schedule schedule = scheduleRepository.findScheduleById(ms.getSchedule().getId());
                    String whenStr = schedule.getEndAt().toString().substring(0,10);
                    LocalDate when = LocalDate.parse(whenStr);

                    if(dateList.contains(when)){
                        successCnt++;
                    }
                }

                if(successCnt < targetCnt+1){
                    ids.add(missionUser.getMission().getId());
                }
            }
        }

        List<Mission> missionList = missionRepository.getMissionsByIdIsIn(ids);
        List<MissionHomeRes> resultList = missionList.stream()
                .map(MissionHomeRes::toMissionHomeRes)
                .collect(Collectors.toList());


        MissionHistoryRes result = MissionHistoryRes.toMissionHistoryRes(totalCnt,resultList);

        totalCnt = 0;

        return result;
    }

    /**
     * 미션 등록시 미션 카테고리명 리스트 보기
     * @return
     */
    @Override
    public List<MissionCategoryRes> getMissionCategory() {

        List<Category> missionCategoryList = categoryRepository.findAll();

        List<MissionCategoryRes> resultList = missionCategoryList.stream()
                .map(MissionCategoryRes::toMissionCategoryRes)
                .collect(Collectors.toList());

        return resultList;
    }

    @Override
    @Transactional
    public void postMission(PostMissionReq postMissionReq, Long userId) throws BaseException {
        MissionUsers missionUsers = new MissionUsers();

        Mission findMission = missionRepository.getMissionByTitle(postMissionReq.getTitle());

        if(findMission != null){
            throw new BaseException(EXIST_MISSION_TITLE);
        }



        User user = userRepository.getReferenceById(userId);
        Category category = categoryRepository.getReferenceById(postMissionReq.getCategoryId());
        Mission mission = new Mission();
        mission.setMission(postMissionReq,category);
        missionRepository.save(mission);

        missionUsers.setMission(mission);
        missionUsers.setUser(user);
        missionUserRepository.save(missionUsers);
    }


}
