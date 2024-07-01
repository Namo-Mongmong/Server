package com.namo.spring.application.external.api.group.controller;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.namo.spring.application.external.api.group.dto.GroupScheduleRequest;
import com.namo.spring.application.external.api.group.dto.GroupScheduleResponse;
import com.namo.spring.application.external.api.group.facade.GroupScheduleFacade;
import com.namo.spring.application.external.global.annotation.swagger.ApiErrorCodes;
import com.namo.spring.application.external.global.utils.Converter;
import com.namo.spring.core.common.code.status.ErrorStatus;
import com.namo.spring.core.common.response.ResponseDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "7. Schedule (모임)", description = "모임 일정 관련 API")
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/group/schedules")
public class GroupScheduleController {
	private final GroupScheduleFacade groupScheduleFacade;
	private final Converter converter;

	@Operation(summary = "모임 일정 생성", description = "모임 일정 생성 API")
	@PostMapping("")
	@ApiErrorCodes(value = {
		ErrorStatus.EMPTY_ACCESS_KEY,
		ErrorStatus.EXPIRATION_ACCESS_TOKEN,
		ErrorStatus.EXPIRATION_REFRESH_TOKEN,
		ErrorStatus.INTERNET_SERVER_ERROR
	})
	public ResponseDto<Long> createGroupSchedule(
		@Valid @RequestBody GroupScheduleRequest.PostGroupScheduleDto scheduleReq
	) {
		Long scheduleId = groupScheduleFacade.createSchedule(scheduleReq);
		return ResponseDto.onSuccess(scheduleId);
	}

	@Operation(summary = "모임 일정 수정", description = "모임 일정 수정 API")
	@PatchMapping("")
	@ApiErrorCodes(value = {
		ErrorStatus.EMPTY_ACCESS_KEY,
		ErrorStatus.EXPIRATION_ACCESS_TOKEN,
		ErrorStatus.EXPIRATION_REFRESH_TOKEN,
		ErrorStatus.INTERNET_SERVER_ERROR
	})
	public ResponseDto<Long> modifyGroupSchedule(
		@Valid @RequestBody GroupScheduleRequest.PatchGroupScheduleDto scheduleReq
	) {
		groupScheduleFacade.modifyGroupSchedule(scheduleReq);
		return ResponseDto.onSuccess(null);
	}

	@Operation(summary = "모임 일정 카테고리 수정", description = "모임 일정 카테고리 수정 API")
	@PatchMapping("/category")
	@ApiErrorCodes(value = {
		ErrorStatus.EMPTY_ACCESS_KEY,
		ErrorStatus.EXPIRATION_ACCESS_TOKEN,
		ErrorStatus.EXPIRATION_REFRESH_TOKEN,
		ErrorStatus.INTERNET_SERVER_ERROR
	})
	public ResponseDto<Long> modifyGroupScheduleCategory(
		@Valid @RequestBody GroupScheduleRequest.PatchGroupScheduleCategoryDto scheduleReq,
		HttpServletRequest request
	) {
		groupScheduleFacade.modifyGroupScheduleCategory(scheduleReq, (Long)request.getAttribute("userId"));
		return ResponseDto.onSuccess(null);
	}

	@Operation(summary = "모임 일정 삭제", description = "모임 일정 삭제 API")
	@DeleteMapping("/{moimScheduleId}")
	@ApiErrorCodes(value = {
		ErrorStatus.EMPTY_ACCESS_KEY,
		ErrorStatus.EXPIRATION_ACCESS_TOKEN,
		ErrorStatus.EXPIRATION_REFRESH_TOKEN,
		ErrorStatus.INTERNET_SERVER_ERROR
	})
	public ResponseDto<Long> removeGroupSchedule(
		@Parameter(description = "모임 일정 ID") @PathVariable Long groupScheduleId,
		HttpServletRequest request
	) {
		groupScheduleFacade.removeGroupSchedule(groupScheduleId, (Long)request.getAttribute("userId"));
		return ResponseDto.onSuccess(null);
	}

	@Operation(summary = "월간 모임 일정 조회", description = "월간 모임 일정 조회 API")
	@GetMapping("/{groupId}/{month}")
	@ApiErrorCodes(value = {
		ErrorStatus.EMPTY_ACCESS_KEY,
		ErrorStatus.EXPIRATION_ACCESS_TOKEN,
		ErrorStatus.EXPIRATION_REFRESH_TOKEN,
		ErrorStatus.INTERNET_SERVER_ERROR
	})
	public ResponseDto<GroupScheduleResponse.GroupScheduleDto> getMonthGroupSchedules(
		@Parameter(description = "그룹 ID") @PathVariable("groupId") Long groupId,
		@Parameter(description = "조회 일자", example = "{년},{월}") @PathVariable("month") String month,
		HttpServletRequest request
	) {
		List<LocalDateTime> localDateTimes = converter.convertLongToLocalDateTime(month);
		List<GroupScheduleResponse.GroupScheduleDto> schedules = groupScheduleFacade.getMonthGroupSchedules(groupId,
			localDateTimes, (Long)request.getAttribute("userId"));
		return ResponseDto.onSuccess(schedules.get(0));
	}

	@Operation(summary = "모든 모임 일정 조회", description = "모든 모임 일정 조회 API")
	@GetMapping("/{groupId}/all")
	@ApiErrorCodes(value = {
		ErrorStatus.EMPTY_ACCESS_KEY,
		ErrorStatus.EXPIRATION_ACCESS_TOKEN,
		ErrorStatus.EXPIRATION_REFRESH_TOKEN,
		ErrorStatus.INTERNET_SERVER_ERROR
	})
	public ResponseDto<GroupScheduleResponse.GroupScheduleDto> getAllGroupSchedules(
		@Parameter(description = "그룹 ID") @PathVariable("groupId") Long groupId,
		HttpServletRequest request
	) {
		List<GroupScheduleResponse.GroupScheduleDto> schedules
			= groupScheduleFacade.getAllGroupSchedules(groupId, (Long)request.getAttribute("userId"));
		return ResponseDto.onSuccess(schedules.get(0));
	}

	@Operation(summary = "모임 일정 생성 알람", description = "모임 일정 생성 알람 API")
	@PostMapping("/alarm")
	@ApiErrorCodes(value = {
		ErrorStatus.EMPTY_ACCESS_KEY,
		ErrorStatus.EXPIRATION_ACCESS_TOKEN,
		ErrorStatus.EXPIRATION_REFRESH_TOKEN,
		ErrorStatus.INTERNET_SERVER_ERROR
	})
	public ResponseDto<Void> createGroupScheduleAlarm(
		@Valid @RequestBody GroupScheduleRequest.PostGroupScheduleAlarmDto postGroupScheduleAlarmDto,
		HttpServletRequest request
	) {
		groupScheduleFacade.createGroupScheduleAlarm(postGroupScheduleAlarmDto, (Long)request.getAttribute("userId"));
		return ResponseDto.onSuccess(null);
	}

	@Operation(summary = "모임 일정 변경 알람", description = "모임 일정 변경 알람 API")
	@PatchMapping("/alarm")
	@ApiErrorCodes(value = {
		ErrorStatus.EMPTY_ACCESS_KEY,
		ErrorStatus.EXPIRATION_ACCESS_TOKEN,
		ErrorStatus.EXPIRATION_REFRESH_TOKEN,
		ErrorStatus.INTERNET_SERVER_ERROR
	})
	public ResponseDto<Void> modifyGroupScheduleAlarm(
		@Valid @RequestBody GroupScheduleRequest.PostGroupScheduleAlarmDto postGroupScheduleAlarmDto,
		HttpServletRequest request
	) {
		groupScheduleFacade.modifyGroupScheduleAlarm(postGroupScheduleAlarmDto, (Long)request.getAttribute("userId"));
		return ResponseDto.onSuccess(null);
	}
}
