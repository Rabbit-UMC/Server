package rabbit.umc.com.config;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 에러 코드 관리
 */
@Getter
public enum BaseResponseStatus {
    /**
     * 200 : 요청 성공
     */
    SUCCESS(true, "COMMON200", "요청에 성공하였습니다."),

    /**
     * 400 : Request 오류, Response 오류
     */
    // Common
    REQUEST_ERROR(false, "COMMON401", "입력값을 확인해주세요."),
    INVALID_USER_JWT(false, "COMMON402","권한이 없는 유저의 접근입니다."),
    RESPONSE_ERROR(false, "COMMON404", "값을 불러오는데 실패하였습니다."),
    END_PAGE(false, "COMMON405", "마지막 페이지입니다."),

    //JWT
    EMPTY_JWT(false, "JWT4001", "JWT TOKEN 값이 존재하지 않습니다."),
    INVALID_JWT(false, "JWT4002", "유효하지 않은 JWT입니다."),
    FORBIDDEN(false, "JWT4003", "금지된 접근입니다."),

    // users
    USERS_EMPTY_USER_ID(false, "USER4001", "유저 아이디 값을 확인해주세요."),
    INVALID_USER_ID(false, "USER4002", "탈퇴한 유저입니다."),
    EXPIRED_JWT_ACCESS(false, "USER4003", "ACCESS TOKEN의 유효 기간이 만료되었습니다."),
    POST_USERS_EMPTY_EMAIL(false, "USER4004", "이메일을 입력해주세요."),
    POST_USERS_INVALID_EMAIL(false, "USER4005", "이메일 형식을 확인해주세요."),
    POST_USERS_EXISTS_EMAIL(false,"USER4006","중복된 이메일입니다."),
    POST_USERS_EXISTS_NICKNAME(false,"USER4007","중복된 닉네임입니다."),
    FAILED_TO_LOGIN(false,"USER4008","없는 아이디거나 비밀번호가 틀렸습니다."),
    FAILED_TO_AUTHENTICATION(false, "USER4009","올바른 인증이 아닙니다."),

    // Token
    UNEXPIRED_JWT_ACCESS(false, "TOKEN4001", "유효기간이 남아있는 ACCESS TOKEN입니다."),
    EMPTY_KAKAO_ACCESS(false, "TOKEN4002", "KAKAO ACCESS TOKEN 값이 존재하지 않습니다."),
    INVALID_JWT_REFRESH(false, "TOKEN4003", "REFRESH TOKEN이 유효하지 않습니다."),

    // article
    FAILED_TO_REPORT(false, "ARTICLE4001","이미 신고한 게시물입니다."),
    FAILED_TO_LIKE(false,"ARTICLE4002","이미 좋아요한 게시물입니다."),
    FAILED_TO_UNLIKE(false, "ARTICLE4003","좋아요 하지 않은 게시물입니다."),
    DONT_EXIST_ARTICLE(false,"ARTICLE4006","존재하지 않는 게시물입니다."),

    //comment
    FAILED_TO_LOCK(false, "COMMENT4001","이미 잠긴 댓글 입니다."),
    DONT_EXIST_COMMENT(false, "COMMENT4002","존재하지 않는 글/댓글 입니다."),

    //schedule
    FAILED_TO_SCHEDULE(false,"SCHEDULE4001","존재하지 않는 일정입니다."),
    FAILED_TO_POST_SCHEDULE_DATE(false,"SCHEDULE4002", "종료 시간은 시작 시간보다 커야 합니다."),
    EMPTY_SCHEDULE(true,"SCHEDULE4003", "해당 날짜에 일정이 없습니다."),

    //mission
    DONT_EXIST_MISSION(false,"MISSION4001","존재하지 않는 메인 미션입니다."),
    DONT_EXIST_MISSION_PROOF(false,"MISSION4002","존재하지 않는 메인 미션 사진입니다."),
    FAILED_TO_UPLOAD(false,"MISSION4003","이미 오늘 인증을 완료했습니다."),
    FAILED_TO_TOGETHER_MISSION(false,"MISSION4004", "이미 같이하고 있는 미션입니다."),
    EXIST_MISSION_TITLE(false,"MISSION4005","이미 존재하는 미션명입니다."),
    FAILED_TO_POST_SCHEDULE(false,"MISSION4006", "해당 미션에 대한 일정이 같은 날짜에 있습니다."),
    NOT_DONE_MISSION(false,"MISSION4007", "아직끝나지 않은 미션입니다."),
    FAILED_TO_LIKE_MISSION(false,"MISSION4008","이미 좋아요한 사진입니다."),
    FAILED_TO_UNLIKE_MISSION(false, "MISSION4009","좋아요하지 않은 사진입니다."),
    FAILED_TO_MISSION(false,"MISSION4010", "존재하지 않는 미션입니다."),
    FAILED_DELETE_MISSION(false,"MISSION4011","미션 삭제에 실패했습니다(존재하지 않는 미션을 삭제하는 경우)."),
    FAILED_CREATE_MISSION(false,"MISSION4012","미션 생성에 실패했습니다."),
    FAILED_TO_MISSION_DATE(false,"MISSION4013","미션 종료일은 미션 시작일보다 커야 합니다."),

    DONT_EXIST_CATEGORY(false, "CATEGORY4001", "존재하지 않는 카테고리 입니다."),

    //Database, Server 오류
    DATABASE_ERROR(false, "COMMON500", "데이터베이스 연결에 실패하였습니다."),
    SERVER_ERROR(false, "COMMON500", "서버와의 연결에 실패하였습니다.");


    private final boolean isSuccess;
    private final String code;
    private final String message;

    private BaseResponseStatus(boolean isSuccess, String code, String message) {
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
    }
}