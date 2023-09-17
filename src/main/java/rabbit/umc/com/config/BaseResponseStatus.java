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
    SUCCESS(true, HttpStatus.OK.value(), "요청에 성공하였습니다."),


    /**
     * 400 : Request 오류, Response 오류
     */
    // Common
    REQUEST_ERROR(false, HttpStatus.BAD_REQUEST.value(), "입력값을 확인해주세요."),
    EMPTY_JWT(false, HttpStatus.UNAUTHORIZED.value(), "JWT TOKEN 값이 존재하지 않습니다."),
    INVALID_JWT(false, HttpStatus.UNAUTHORIZED.value(), "유효하지 않은 JWT입니다."),
    EXPIRED_JWT_ACCESS(false, HttpStatus.FORBIDDEN.value(), "ACCESS TOKEN의 유효 기간이 만료되었습니다."),
    UNEXPIRED_JWT_ACCESS(false, HttpStatus.UNAUTHORIZED.value(), "유효기간이 남아있는 ACCESS TOKEN입니다."),
    EMPTY_KAKAO_ACCESS(false, HttpStatus.UNAUTHORIZED.value(), "KAKAO ACCESS TOKEN 값이 존재하지 않습니다."),
    INVALID_JWT_REFRESH(false, HttpStatus.UNAUTHORIZED.value(), "REFRESH TOKEN이 유효하지 않습니다."),
    INVALID_USER_JWT(false,HttpStatus.UNAUTHORIZED.value(),"권한이 없는 유저의 접근입니다."),
    RESPONSE_ERROR(false, HttpStatus.NOT_FOUND.value(), "값을 불러오는데 실패하였습니다."),
    END_PAGE(false, HttpStatus.NOT_FOUND.value(), "마지막 페이지입니다."),

    // users
    USERS_EMPTY_USER_ID(false, HttpStatus.BAD_REQUEST.value(), "유저 아이디 값을 확인해주세요."),
    INVALID_USER_ID(false, HttpStatus.BAD_REQUEST.value(), "탈퇴한 유저입니다."),

    // [POST] /users
    POST_USERS_EMPTY_EMAIL(false, HttpStatus.BAD_REQUEST.value(), "이메일을 입력해주세요."),
    POST_USERS_INVALID_EMAIL(false, HttpStatus.BAD_REQUEST.value(), "이메일 형식을 확인해주세요."),
    POST_USERS_EXISTS_EMAIL(false,HttpStatus.BAD_REQUEST.value(),"중복된 이메일입니다."),
    POST_USERS_EXISTS_NICKNAME(false,HttpStatus.CONFLICT.value(),"중복된 닉네임입니다."),
    FAILED_TO_LOGIN(false,HttpStatus.NOT_FOUND.value(),"없는 아이디거나 비밀번호가 틀렸습니다."),
    FAILED_TO_AUTHENTICATION(false, HttpStatus.BAD_REQUEST.value(),"올바른 인증이 아닙니다."),

    // article
    FAILED_TO_REPORT(false,HttpStatus.BAD_REQUEST.value(),"이미 신고한 게시물입니다."),
    FAILED_TO_LIKE(false,HttpStatus.BAD_REQUEST.value(),"이미 좋아요한 게시물입니다."),
    FAILED_TO_UNLIKE(false,HttpStatus.BAD_REQUEST.value(),"좋아요 하지 않은 게시물입니다."),
    FAILED_TO_LIKE_MISSION(false,HttpStatus.BAD_REQUEST.value(),"이미 좋아요한 사진입니다."),
    FAILED_TO_UNLIKE_MISSION(false,HttpStatus.BAD_REQUEST.value(),"좋아요하지 않은 사진입니다."),
    DONT_EXIST_ARTICLE(false,HttpStatus.NOT_FOUND.value(),"존재하지 않는 게시물입니다."),
    DONT_EXIST_COMMENT(false,HttpStatus.NOT_FOUND.value(),"존재하지 않는 글/댓글 입니다."),

    FAILED_TO_LOCK(false,HttpStatus.BAD_REQUEST.value(),"이미 잠긴 댓글 입니다."),

    FAILED_TO_SCHEDULE(false,HttpStatus.NOT_FOUND.value(),"존재하지 않는 일정입니다."),

    FAILED_TO_MISSION(false,HttpStatus.NOT_FOUND.value(),"존재하지 않는 미션입니다."),

    FAILED_DELETE_MISSION(false,HttpStatus.NOT_FOUND.value(),"미션 삭제에 실패했습니다(존재하지 않는 미션을 삭제하는 경우)."),
    FAILED_TO_POST_SCHEDULE_DATE(false,HttpStatus.BAD_REQUEST.value(), "종료 시간은 시작 시간보다 커야 합니다."),


    // mission

    DONT_EXIST_MISSION(false,HttpStatus.NOT_FOUND.value(),"존재하지 않는 미션입니다."),
    DONT_EXIST_MISSION_PROOF(false,HttpStatus.NOT_FOUND.value(),"존재하지 않는 미션 사진입니다."),
    FAILED_TO_UPLOAD(false,HttpStatus.BAD_REQUEST.value(),"이미 오늘 인증을 완료했습니다."),
    FAILED_TO_TOGETHER_MISSION(false,HttpStatus.BAD_REQUEST.value(), "이미 같이하고 있는 미션입니다."),
    EXIST_MISSION_TITLE(false,HttpStatus.NOT_FOUND.value(),"이미 존재하는 미션명입니다."),
    FAILED_TO_POST_SCHEDULE(false,HttpStatus.BAD_REQUEST.value(), "해당 미션에 대한 일정이 같은 날짜에 있습니다."),
    NOT_DONE_MISSION(false,3000, "아직끝나지 않은 미션입니다."),


    /**
     * 50 : Database, Server 오류
     */
    DATABASE_ERROR(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "데이터베이스 연결에 실패하였습니다."),
    SERVER_ERROR(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "서버와의 연결에 실패하였습니다."),

    //[PATCH] /users/{userIdx}
    MODIFY_FAIL_USERNAME(false,HttpStatus.INTERNAL_SERVER_ERROR.value(),"유저네임 수정 실패"),

    PASSWORD_ENCRYPTION_ERROR(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "비밀번호 암호화에 실패하였습니다."),
    PASSWORD_DECRYPTION_ERROR(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "비밀번호 복호화에 실패하였습니다.");





    private final boolean isSuccess;
    private final int code;
    private final String message;

    private BaseResponseStatus(boolean isSuccess, int code, String message) {
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
    }
}