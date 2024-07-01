package com.namo.spring.application.external.api.group.controller;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.namo.spring.application.external.api.group.dto.GroupDiaryRequest;
import com.namo.spring.application.external.api.group.dto.GroupDiaryResponse;
import com.namo.spring.application.external.api.group.dto.GroupScheduleRequest;
import com.namo.spring.application.external.api.group.facade.GroupDiaryFacade;
import com.namo.spring.application.external.global.annotation.swagger.ApiErrorCodes;
import com.namo.spring.application.external.global.utils.Converter;
import com.namo.spring.core.common.code.status.ErrorStatus;
import com.namo.spring.core.common.response.ResponseDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "8. Diary (모임)", description = "모임 기록 관련 API")
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/group/diaries")
public class GroupDiaryController {
	private final GroupDiaryFacade groupDiaryFacade;
	private final Converter converter;

	@Operation(summary = "모임 기록 생성", description = "모임 기록 생성 API")
	@PostMapping(value = "/{moimScheduleId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiErrorCodes(value = {
		ErrorStatus.EMPTY_ACCESS_KEY,
		ErrorStatus.EXPIRATION_ACCESS_TOKEN,
		ErrorStatus.EXPIRATION_REFRESH_TOKEN,
		ErrorStatus.INTERNET_SERVER_ERROR
	})
	public ResponseDto<Void> createGroupDiary(
		@Parameter(description = "모임 일정 ID") @PathVariable Long moimScheduleId,
		@Parameter(description = "모임 기록용 이미지") @RequestPart(required = false) List<MultipartFile> imgs,
		@Parameter(description = "모임 기록명") @RequestPart String name,
		@Parameter(description = "모임 회비") @RequestPart String money,
		@Parameter(description = "참여자", example = "멍청이, 똑똑이") @RequestPart String participants
	) {
		GroupDiaryRequest.LocationDto locationDto = new GroupDiaryRequest.LocationDto(name, money, participants);
		groupDiaryFacade.createGroupDiary(moimScheduleId, locationDto, imgs);
		return ResponseDto.onSuccess(null);
	}

	@Operation(summary = "모임 기록 장소 수정", description = "모임 기록 장소 수정 API")
	@PatchMapping(value = "/{activityId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiErrorCodes(value = {
		ErrorStatus.EMPTY_ACCESS_KEY,
		ErrorStatus.EXPIRATION_ACCESS_TOKEN,
		ErrorStatus.EXPIRATION_REFRESH_TOKEN,
		ErrorStatus.INTERNET_SERVER_ERROR
	})
	public ResponseDto<Object> updateGroupDiary(
		@Parameter(description = "수정하고자 하는 활동 ID") @PathVariable Long activityId,
		@Parameter(description = "모임 기록용 이미지") @RequestPart(required = false) List<MultipartFile> imgs,
		@Parameter(description = "모임 기록명") @RequestPart String name,
		@Parameter(description = "모임 회비") @RequestPart String money,
		@Parameter(description = "참여자", example = "멍청이, 똑똑이") @RequestPart String participants
	) {
		GroupDiaryRequest.LocationDto locationDto = new GroupDiaryRequest.LocationDto(name, money, participants);
		groupDiaryFacade.modifyGroupActivity(activityId, locationDto, imgs);
		return ResponseDto.onSuccess(null);
	}

	@Operation(summary = "모임 기록 조회", description = "모임 기록 조회 API")
	@GetMapping("/{moimScheduleId}")
	@ApiErrorCodes(value = {
		ErrorStatus.EMPTY_ACCESS_KEY,
		ErrorStatus.EXPIRATION_ACCESS_TOKEN,
		ErrorStatus.EXPIRATION_REFRESH_TOKEN,
		ErrorStatus.INTERNET_SERVER_ERROR
	})
	public ResponseDto<Object> getGroupDiary(
		@Parameter(description = "모임 기록 ID") @PathVariable("moimScheduleId") Long moimScheduleId
	) {
		GroupDiaryResponse.GroupDiaryDto groupDiaryDto = groupDiaryFacade.getGroupDiaryWithLocations(moimScheduleId);
		return ResponseDto.onSuccess(groupDiaryDto);
	}

	@Operation(summary = "월간 모임 기록 조회", description = "월간 모임 기록 조회 API")
	@GetMapping("/month/{month}")
	@ApiErrorCodes(value = {
		ErrorStatus.EMPTY_ACCESS_KEY,
		ErrorStatus.EXPIRATION_ACCESS_TOKEN,
		ErrorStatus.EXPIRATION_REFRESH_TOKEN,
		ErrorStatus.INTERNET_SERVER_ERROR
	})
	public ResponseDto<GroupDiaryResponse.SliceDiaryDto<GroupDiaryResponse.DiaryDto>> findMonthGroupDiary(
		@Parameter(description = "조회 일자", example = "{년},{월}") @PathVariable("month") String month,
		Pageable pageable,
		HttpServletRequest request
	) {
		List<LocalDateTime> localDateTimes = converter.convertLongToLocalDateTime(month);
		GroupDiaryResponse.SliceDiaryDto<GroupDiaryResponse.DiaryDto> diaryDto = groupDiaryFacade
			.getMonthMonthGroupDiary((Long)request.getAttribute("userId"), localDateTimes, pageable);
		return ResponseDto.onSuccess(diaryDto);
	}

	@Operation(summary = "모임 기록 상세 조회", description = "모임 기록 상세 조회 API")
	@GetMapping("/detail/{moimScheduleId}")
	@ApiErrorCodes(value = {
		ErrorStatus.EMPTY_ACCESS_KEY,
		ErrorStatus.EXPIRATION_ACCESS_TOKEN,
		ErrorStatus.EXPIRATION_REFRESH_TOKEN,
		ErrorStatus.INTERNET_SERVER_ERROR
	})
	public ResponseDto<GroupDiaryResponse.DiaryDto> getGroupDiaryDetail(
		@Parameter(description = "모임 일정 ID") @PathVariable Long moimScheduleId,
		HttpServletRequest request
	) {
		Long userId = (Long)request.getAttribute("userId");
		GroupDiaryResponse.DiaryDto diaryDto = groupDiaryFacade.getGroupDiaryDetail(moimScheduleId, userId);
		return ResponseDto.onSuccess(diaryDto);
	}

	@Operation(summary = "개인 페이지 모임 기록 삭제", description = "일정에 대한 모임 활동 기록 삭제 API")
	@DeleteMapping("/person/{scheduleId}")
	@ApiErrorCodes(value = {
		ErrorStatus.EMPTY_ACCESS_KEY,
		ErrorStatus.EXPIRATION_ACCESS_TOKEN,
		ErrorStatus.EXPIRATION_REFRESH_TOKEN,
		ErrorStatus.INTERNET_SERVER_ERROR
	})
	public ResponseDto<Object> removePersonGroupDiary(
		@Parameter(description = "일정 ID") @PathVariable Long scheduleId,
		HttpServletRequest request
	) {
		Long userId = (Long)request.getAttribute("userId");
		groupDiaryFacade.removePersonGroupDiary(scheduleId, userId);
		return ResponseDto.onSuccess(null);
	}

	@Operation(summary = "모임 기록 전체 삭제", description = "일정에 대한 모임 기록 전체 삭제 API")
	@DeleteMapping("/all/{moimScheduleId}")
	@ApiErrorCodes(value = {
		ErrorStatus.EMPTY_ACCESS_KEY,
		ErrorStatus.EXPIRATION_ACCESS_TOKEN,
		ErrorStatus.EXPIRATION_REFRESH_TOKEN,
		ErrorStatus.INTERNET_SERVER_ERROR
	})
	public ResponseDto<Object> removeGroupDiary(
		@Parameter(description = "모임 일정 ID") @PathVariable Long moimScheduleId
	) {
		groupDiaryFacade.removeGroupDiary(moimScheduleId);
		return ResponseDto.onSuccess(null);
	}

	@Operation(summary = "모임 활동 삭제", description = "모임 활동 삭제 API")
	@DeleteMapping("/{activityId}")
	@ApiErrorCodes(value = {
		ErrorStatus.EMPTY_ACCESS_KEY,
		ErrorStatus.EXPIRATION_ACCESS_TOKEN,
		ErrorStatus.EXPIRATION_REFRESH_TOKEN,
		ErrorStatus.INTERNET_SERVER_ERROR
	})
	public ResponseDto<Object> removeGroupActivity(
		@Parameter(description = "모임 활동 ID") @PathVariable Long activityId
	) {
		groupDiaryFacade.removeGroupActivity(activityId);
		return ResponseDto.onSuccess(null);
	}

	@Operation(summary = "모임 기록 텍스트 추가 (모임 기록 추가)", description = "모임 기록 추가 API")
	@PatchMapping("/text/{moimScheduleId}")
	@ApiErrorCodes(value = {
		ErrorStatus.EMPTY_ACCESS_KEY,
		ErrorStatus.EXPIRATION_ACCESS_TOKEN,
		ErrorStatus.EXPIRATION_REFRESH_TOKEN,
		ErrorStatus.INTERNET_SERVER_ERROR
	})
	public ResponseDto<Object> createGroupMemo(
		@Parameter(description = "모임 일정 ID") @PathVariable Long moimScheduleId,
		@RequestBody GroupScheduleRequest.PostGroupScheduleTextDto moimScheduleText,
		HttpServletRequest request
	) {
		groupDiaryFacade.createGroupMemo(moimScheduleId,
			(Long)request.getAttribute("userId"),
			moimScheduleText);
		return ResponseDto.onSuccess(null);
	}
}
