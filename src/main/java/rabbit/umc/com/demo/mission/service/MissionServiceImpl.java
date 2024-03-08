package rabbit.umc.com.demo.mission.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rabbit.umc.com.config.apiPayload.BaseException;
import rabbit.umc.com.demo.community.category.CategoryRepository;
import rabbit.umc.com.demo.community.domain.Category;
import rabbit.umc.com.demo.mission.Mission;
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
import rabbit.umc.com.demo.user.repository.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static rabbit.umc.com.config.apiPayload.BaseResponseStatus.*;
import static rabbit.umc.com.demo.base.Status.ACTIVE;

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

    private static final int PAGING_SIZE = 20;

    @Override
    public List<MissionHomeRes> getMissionHome(int page) throws BaseException {
        LocalDateTime now =  LocalDateTime.now();
        PageRequest pageRequest = PageRequest.of(page,PAGING_SIZE,Sort.by("startAt"));
        List<Mission> missionList = missionRepository.findAllByStatusAndStartAtAfterAndIsOpenOrderByStartAt(ACTIVE,now,0,pageRequest);

        if (missionList.isEmpty()) {
            throw new BaseException(END_PAGE);
        }

        List<MissionHomeRes> resultList = missionList.stream()
                .map(MissionHomeRes::toMissionHomeRes)
                .collect(Collectors.toList());

        return resultList;
    }

    /**
     * 미션 카테고리별 확인
     */
    @Override
    public List<MissionHomeRes> getMissionByMissionCategoryId(Long categoryId, int page) throws BaseException {
        LocalDateTime now =  LocalDateTime.now();
        PageRequest pageRequest = PageRequest.of(page,PAGING_SIZE,Sort.by("startAt"));
        List<Mission> missionList;

        if(categoryId == 0){
            missionList = missionRepository.findAllByStatusAndStartAtAfterAndIsOpenOrderByStartAt(ACTIVE,now,0,pageRequest);
        }else{
            missionList = missionRepository.getMissionByMissionCategoryIdOrderByStartAt(ACTIVE,now,0,categoryId,pageRequest);
        }

        if(missionList.isEmpty()){
                throw new BaseException(END_PAGE);
        }else{
            List<MissionHomeRes> resultList = missionList.stream()
                    .map(MissionHomeRes::toMissionHomeRes)
                    .collect(Collectors.toList());
            return resultList;
        }
    }

    /**
     * 도전중인 미션리스트
     * @param userId
     * @return
     */
    @Override
    public List<GetMyMissionRes> getMyMissions(long userId) {

        LocalDateTime currentDateTime = LocalDateTime.now();
        LocalDateTime oneDayLaterDateTime = currentDateTime.minusDays(1);

        List<Mission> missionList = missionUserRepository.getMissionUsersByUserIdAndMissionEndAtIsAfterAndMissionStatusAndMissionIsOpen(userId,oneDayLaterDateTime,ACTIVE,0)
                .stream()
                .map(MissionUsers::getMission)
                .collect(Collectors.toList());

        return missionList.stream()
                .map(GetMyMissionRes::toMyMissions)
                .collect(Collectors.toList());
    }

    @Override
    public GetMissionDetailDto getMyMissionDetail(long userId, long missionId) throws BaseException {
        MissionUsers missionUsers = missionUserRepository.getMissionUsersByMissionIdAndUserId(missionId, userId);
        if (missionUsers == null || missionUsers.getMission() == null) {
            // 내 미션이 없는 경우
            throw new BaseException(NOT_TOGETER_MISSION);
        }
        Mission mission = missionRepository.getMissionById(missionUsers.getMission().getId());
        if (mission == null) {
            // 미션을 찾을 수 없는 경우
            throw new BaseException(FAILED_TO_MISSION);
        }
        return GetMissionDetailDto.toGetMissionDetaliDto(mission,true);
    }

    @Override
    public List<GetMyMissionSchedule> getMyMissionSchedules(long userId, long missionId) throws BaseException {
        MissionUsers findedMissionUser = missionUserRepository.getMissionUsersByMissionIdAndUserId(missionId, userId);
        if (findedMissionUser == null) {
            throw new BaseException(FAILED_TO_MISSION);
        }

        List<Schedule> schedules = missionScheduleRepository.findMissionSchedulesByMissionId(missionId).stream()
                .filter(ms -> ms.getSchedule().getUser().getId() == userId)
                .map(MissionSchedule::getSchedule)
                .sorted(Comparator.comparing(Schedule::getEndAt))
                .collect(Collectors.toList());

        List<GetMyMissionSchedule> resultList = schedules.stream()
                .map(GetMyMissionSchedule::toGetMyMissionSchedule)
                .collect(Collectors.toList());

        return resultList;
    }

    @Override
    @Transactional
    public void deleteMyMission(List<Long> missionIds, long userId) throws BaseException {
        List<MissionUsers> missionUsers = missionUserRepository.getMissionUsersByMissionIdAndUserId(missionIds,userId);
        List<MissionSchedule> missionScheduleList = missionScheduleRepository.findMissionSchedulesByMissionIds(missionIds);
        // 일정, 미션일정 테이블 값 제거
        missionScheduleList.forEach(ms -> {
            ms.deleteMissionSchedule(ms.getMission(),ms.getSchedule());
        });

        if(missionUsers.size() == 0 || missionUsers.size() != missionIds.size())
            throw new BaseException(FAILED_TO_MISSION);

        // 미션 유저 테이블 값 제거
        missionUsers.forEach(id -> missionUserRepository.delete(id));
    }

    @Override
    @Transactional
    public void deleteMyMissoinAndSchedules(String missionIdString, List<Long> scheduleIds, long userId) throws BaseException {
        if(missionIdString.equals("")){
            throw new BaseException(REQUEST_ERROR);
        }
        Long missionId = null;

        if(!missionIdString.equals("null")){
            missionId = Long.valueOf(missionIdString);
        }

        // 미션,일정에 대한 값이 둘 다 없을 때
        if(missionId == null & scheduleIds.isEmpty()){
            throw new BaseException(FAILED_TO_DELETE_MISSION_SCHEDULE);
        }

        if(missionId != null){
            // 미션 아이디 값은 있지만 해당 미션이 없을 때
            if (missionRepository.getMissionById(missionId) == null)
                throw new BaseException(FAILED_TO_MISSION);

            if(!scheduleIds.isEmpty()){
                // 미션 있고 일정 아이디 중에서 없는 일정이 있다면 해당 일정 없다고 예외처리
                for (Long s : scheduleIds) {
                    Schedule findSchedule = scheduleRepository.getScheduleByIdAndUserId(s, userId);
                    if (findSchedule == null) {
                        throw new BaseException(FAILED_TO_SCHEDULE);
                    }
                }
                // 미션,일정 둘 다 있을 때 미션-일정, 미션 삭제
                missionUserRepository.deleteByMissionIdAndUserId(missionId,userId); // 미션 유저 삭제
                missionScheduleRepository.deleteByMissionIdAndScheduleIds(missionId,scheduleIds);
//                missionRepository.deleteById(missionId);
            }else{
                // 미션만 있고 일정 아이디들은 주어지지 않았을 때
                missionUserRepository.deleteByMissionIdAndUserId(missionId,userId); // 미션 유저 삭제
                List<MissionSchedule> missionSchedulesByMissionId = missionScheduleRepository.getMissionScheduleByMissionId(missionId);
                missionSchedulesByMissionId.forEach(ms -> ms.deleteMissionSchedule(ms.getMission(),ms.getSchedule()));
//                missionRepository.deleteById(missionId);
            }
        }else{
            // 미션 아이디 값은 없고 일정 아이디들만 있을 때 해당 일정이 없으면 예외 처리
            for (Long s : scheduleIds) {
                Schedule findSchedule = scheduleRepository.getScheduleByIdAndUserId(s, userId);
                if (findSchedule == null) {
                    throw new BaseException(FAILED_TO_SCHEDULE);
                }
            }
            // 해당 일정들이 있다면 미션-일정, 일정 삭제
            if(!scheduleIds.isEmpty())
                missionScheduleRepository.deleteByScheduleIds(scheduleIds);
                scheduleRepository.deleteByScheduleIds(scheduleIds);
        }
    }

    @Override
    @Transactional
    public void reportMission(long missionId,long userId) throws BaseException {
        Mission mission = missionRepository.getMissionById(missionId);
        
        if(mission == null){
            throw  new BaseException(FAILED_TO_MISSION);
        }

        User user = userRepository.getReferenceById(userId);
        Report existingReport = reportRepository.findReportByUserIdAndMissionId(userId, missionId);
        if(existingReport != null){
            throw new BaseException(FAILED_TO_REPORT);
        }


        if(mission != null){
            Report report = Report.builder()
                    .user(user)
                    .mission(mission)
                    .build();
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

        if (mission == null) {
            throw new BaseException(DONT_EXIST_MISSION);
        }

        // 미션 날짜에 맞는 일정들인지 체크
        if (missionUserRepository.getMissionUsersByMissionIdAndUserId(missionId, userId) != null) {
            throw new BaseException(FAILED_TO_TOGETHER_MISSION);
        }

        MissionUsers missionUsers = new MissionUsers(user,mission, ACTIVE);
        missionUserRepository.save(missionUsers);
    }

    @Override
    public GetMissionDetailDto getMissionDetail(Long missionId, Long userId) throws BaseException {

        Mission mission = missionRepository.getMissionById(missionId);
        MissionUsers missionUsers = missionUserRepository.getMissionUsersByMissionIdAndUserId(missionId,userId);
        boolean isAlreadyIn = false;
        if(missionUsers != null)
            isAlreadyIn = true;

        if (mission == null) {
            // 미션을 찾을 수 없는 경우
            throw new BaseException(FAILED_TO_MISSION);
        }

        return GetMissionDetailDto.toGetMissionDetaliDto(mission,isAlreadyIn);
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
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneDayLaterDateTime = now.minusDays(1);
        CheckMissionSuccessOrFail successMissionIds = getMissionIdsByStatus(userId, oneDayLaterDateTime, true);

        List<Mission> successMissions = missionRepository.getMissionsByIdIsIn(successMissionIds.getIds());
        List<MissionHomeRes> resultList = successMissions.stream()
                .map(MissionHomeRes::toMissionHomeRes)
                .collect(Collectors.toList());


        return MissionHistoryRes.toSuccessMissionHistoryRes(successMissionIds.getTotalCnt(), resultList);
    }

    /**
     * 도전 실패한 미션리스트
     * @param userId
     * @return
     */
    @Override
    public MissionHistoryRes getFailureMissions(Long userId) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneDayLaterDateTime = now.minusDays(1);
        CheckMissionSuccessOrFail failureMissionIds = getMissionIdsByStatus(userId, oneDayLaterDateTime, false);

        List<Mission> failureMissions = missionRepository.getMissionsByIdIsIn(failureMissionIds.getIds());
        List<MissionHomeRes> resultList = failureMissions.stream()
                .map(MissionHomeRes::toMissionHomeRes)
                .collect(Collectors.toList());

        return MissionHistoryRes.toFailMissionHistoryRes(failureMissionIds.getTotalCnt(), resultList);
    }

    /**
     * 미션 성공,실패 판단하는 메소드
     */
    private CheckMissionSuccessOrFail getMissionIdsByStatus(Long userId, LocalDateTime now, boolean isSuccess) {
        int totalCnt = 0;
        List<MissionUsers> missionUsersList = missionUserRepository.getMissionUsersByUserIdAndMissionEndAtIsBefore(userId, now);
        List<Long> missionIds = new ArrayList<>();

        for (MissionUsers missionUser : missionUsersList) {
            List<MissionSchedule> missionSchedules = missionScheduleRepository.getMissionScheduleByMissionId(missionUser.getMission().getId());
            Mission mission = missionRepository.getMissionByIdAndEndAtIsBeforeOrderByEndAt(missionUser.getMission().getId(), now);

            if (mission != null) {
                LocalDate targetDate = mission.getEndAt().toLocalDate();
                LocalDate currentDate = mission.getStartAt().toLocalDate();
                int targetCnt = (int) ChronoUnit.DAYS.between(currentDate, targetDate);
                totalCnt++;
                List<LocalDate> dateList = getDateBetweenTwoDates(mission.getStartAt(), mission.getEndAt());

                int successCnt = 0;
                for (MissionSchedule missionSchedule : missionSchedules) {
                    Schedule schedule = scheduleRepository.findScheduleById(missionSchedule.getSchedule().getId());
                    LocalDate when = LocalDate.from(schedule.getEndAt());

                    if (schedule.getUser().getId() == userId && dateList.contains(when)) {
                        successCnt++;
                    }
                }

                if (isSuccess && successCnt == targetCnt + 1) {
                    missionIds.add(missionUser.getMission().getId());
                } else if (!isSuccess && successCnt < targetCnt + 1) {
                    missionIds.add(missionUser.getMission().getId());
                }
            }
        }

        return new CheckMissionSuccessOrFail(missionIds,totalCnt);
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

        Mission findMission = missionRepository.getMissionByTitle(postMissionReq.getTitle());

        if(findMission != null){
            throw new BaseException(EXIST_MISSION_TITLE);
        }

        User user = userRepository.getReferenceById(userId);
        Category category = categoryRepository.getReferenceById(postMissionReq.getCategoryId());
        Mission mission = new Mission();
        mission.toMission(postMissionReq,category);
        missionRepository.save(mission);

        MissionUsers missionUsers = new MissionUsers(user,mission, ACTIVE);
        missionUserRepository.save(missionUsers);
    }
}
