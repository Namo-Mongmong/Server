package com.example.namo2.global.common.response;

import lombok.Getter;

@Getter
public enum BaseResponseStatus {

	/**
	 * 200 : 요청 성공
	 */
	SUCCESS(200, "요청 성공"),

	/**
	 * 400 : Bad Request
	 */
	MAKE_PUBLIC_KEY_FAILURE(400, "애플 퍼블릭 키를 생성하는데 실패하였습니다"),
	//애플 identityToken 오류
	APPLE_REQUEST_ERROR(400, "애플 identityToken이 잘못되었습니다."),

	/**
	 * 401 : 소셜 로그인 오류
	 */
	SOCIAL_LOGIN_FAILURE(401, "소셜 로그인에 실패하였습니다."),
	KAKAO_UNAUTHORIZED(401, "카카오 accessToken이 잘못되었습니다"),

	/**
	 * 403 : local Access Token 오류
	 */
	EMPTY_ACCESS_KEY(403, "AccessToken 이 비어있습니다."),
	LOGOUT_ERROR(403, "로그 아웃된 사용자입니다."),
	EXPIRATION_ACCESS_TOKEN(403, "Access token 이 만료되었습니다."),
	EXPIRATION_REFRESH_TOKEN(403, "RefreshToken 이 만료되었습니다."),
	KAKAO_FORBIDDEN(403, "카카오 권한 오류"),

	/**
	 * NOT FOUND 오류
	 * 404 번오류
	 */
	NOT_FOUND_USER_FAILURE(404, "유저를 찾을 수 없습니다."),
	NOT_FOUND_SCHEDULE_FAILURE(404, "스케줄을 찾을 수 없습니다."),
	NOT_FOUND_CATEGORY_FAILURE(404, "카테고리를 찾을 수 없습니다."),
	NOT_FOUND_PALETTE_FAILURE(404, "팔레트를 찾을 수 없습니다."),
	NOT_FOUND_DIARY_FAILURE(404, "다이어리를 찾을 수 없습니다."),
	NOT_FOUND_MOIM_DIARY_FAILURE(404, "모임 메모 장소를 찾을 수 없습니다."),
	NOT_FOUND_MOIM_FAILURE(404, "모임을 찾을 수 없습니다."),
	NOT_FOUND_MOIM_AND_USER_FAILURE(404, "그룹 구성원이 아닙니다."),
	NOT_FOUND_MOIM_SCHEDULE_AND_USER_FAILURE(404, "그룹 스케줄 구성원이 아닙니다."),
	NOT_FOUND_MOIM_MEMO_LOCATION_FAILURE(404, "모임 장소를 찾을 수 없습니다."),

	/**
	 * 404 : 예외 상황 에러
	 */
	NOT_DELETE_BASE_CATEGORY_FAILURE(404, "일정 및 모임 카테고리는 삭제 될 수 없습니다."),
	NOT_CHANGE_SPECIFIED_NAME_FAILURE(404, "일정 및 모임은 기본 카테고리로 지정된 이름입니다."),
	NOT_CHECK_TERM_ERROR(404, "약관에 무조건 동의 해야합니다."),

	/**
	 * 404: 중복 에러
	 */
	DIARY_EXISTS_FAILURE(404, "이미 존재하는 다이어리 입니다."),
	DUPLICATE_PARTICIPATE_FAILURE(404, "이미 가입한 모임입니다."),
	DUPLICATE_MOIM_MEMO_FAILURE(404, "이미 모임 메모가 생성되어 있습니다."),

	/**
	 * 404: 인프라 에러
	 */
	FILE_NAME_EXCEPTION(404, "파일 확장자가 잘못되었습니다."),
	S3_FAILURE(404, "파일 업로드 과정에서 오류가 발생하였습니다."),

	/**
	 * 404: IllegalArgumentException
	 */
	NOT_NULL_FAILURE(404, "널 혹은 비어 있는 값을 카테고리 값으로 넣지 말아주세요,"),

	/**
	 * 500 : 서버 에러
	 */
	INTERNET_SERVER_ERROR(500, "서버 오류"),


	JPA_FAILURE(500, "jpa, sql 상에서 오류가 발생했습니다."),


	KAKAO_INTERNAL_SERVER_ERROR(500, "카카오 서버 오류"),
	KAKAO_BAD_GATEWAY(500, "카카오 시스템 오류"),
	KAKAO_SERVICE_UNAVAILABLE(500, "카카오 서비스 점검 중"),
	FEIGN_SERVER_ERROR(500, "feign 서버 에러")
	;


	private final int code;
	private final String message;

	private BaseResponseStatus(int code, String message) {
		this.code = code;
		this.message = message;
	}
}
