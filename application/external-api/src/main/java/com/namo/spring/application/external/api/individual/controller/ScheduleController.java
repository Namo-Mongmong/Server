package com.namo.spring.application.external.api.individual.controller;

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

import com.namo.spring.application.external.api.individual.dto.ScheduleRequest;
import com.namo.spring.application.external.api.individual.dto.ScheduleResponse;
import com.namo.spring.application.external.api.individual.facade.ScheduleFacade;
import com.namo.spring.application.external.global.annotation.swagger.ApiErrorCodes;
import com.namo.spring.application.external.global.utils.Converter;
import com.namo.spring.core.common.code.status.ErrorStatus;
import com.namo.spring.core.common.response.ResponseDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "3. Schedule (개인)", description = "개인 일정 관련 API")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/schedules")
public class ScheduleController {
	private final ScheduleFacade scheduleFacade;
	private final Converter converter;

	@Operation(summary = "일정 생성", description = "일정 생성 API")
	@PostMapping("")
	@ApiErrorCodes({
		ErrorStatus.EMPTY_ACCESS_KEY,
		ErrorStatus.EXPIRATION_ACCESS_TOKEN,
		ErrorStatus.EXPIRATION_REFRESH_TOKEN,
		ErrorStatus.INTERNET_SERVER_ERROR
	})
	public ResponseDto<ScheduleResponse.ScheduleIdDto> createSchedule(
		@Valid @RequestBody ScheduleRequest.PostScheduleDto postScheduleDto,
		HttpServletRequest request
	) {
		ScheduleResponse.ScheduleIdDto scheduleIddto = scheduleFacade.createSchedule(
			postScheduleDto,
			(Long)request.getAttribute("userId")
		);
		return ResponseDto.onSuccess(scheduleIddto);
	}

	@Operation(summary = "일정 월별 조회", description = "개인 일정 & 모임 일정 월별 조회 API")
	@GetMapping("/{month}")
	@ApiErrorCodes({
		ErrorStatus.EMPTY_ACCESS_KEY,
		ErrorStatus.EXPIRATION_ACCESS_TOKEN,
		ErrorStatus.EXPIRATION_REFRESH_TOKEN,
		ErrorStatus.INTERNET_SERVER_ERROR
	})
	public ResponseDto<List<ScheduleResponse.GetScheduleDto>> getSchedulesByUser(
		@Parameter(description = "조회 일자", example = "{년},{월}") @PathVariable("month") String month,
		HttpServletRequest request
	) {
		List<LocalDateTime> localDateTimes = converter.convertLongToLocalDateTime(month);
		List<ScheduleResponse.GetScheduleDto> userSchedule = scheduleFacade.getSchedulesByUser(
			(Long)request.getAttribute("userId"), localDateTimes);
		return ResponseDto.onSuccess(userSchedule);
	}

	@Operation(summary = "모임 일정 월별 조회", description = "모임 일정 월별 조회 API")
	@GetMapping("/group/{month}")
	@ApiErrorCodes({
		ErrorStatus.EMPTY_ACCESS_KEY,
		ErrorStatus.EXPIRATION_ACCESS_TOKEN,
		ErrorStatus.EXPIRATION_REFRESH_TOKEN,
		ErrorStatus.INTERNET_SERVER_ERROR
	})
	public ResponseDto<List<ScheduleResponse.GetScheduleDto>> getGroupSchedulesByUser(
		@Parameter(description = "조회 일자", example = "{년},{월}") @PathVariable("month") String month,
		HttpServletRequest request
	) {
		List<LocalDateTime> localDateTimes = converter.convertLongToLocalDateTime(month);
		List<ScheduleResponse.GetScheduleDto> userSchedule = scheduleFacade.getGroupSchedulesByUser(
			(Long)request.getAttribute("userId"),
			localDateTimes
		);
		return ResponseDto.onSuccess(userSchedule);
	}

	@Operation(summary = "모든 일정 조회", description = "유저의 모든 개인 일정과 모임 일정 조회 API")
	@GetMapping("/all")
	@ApiErrorCodes({
		ErrorStatus.EMPTY_ACCESS_KEY,
		ErrorStatus.EXPIRATION_ACCESS_TOKEN,
		ErrorStatus.EXPIRATION_REFRESH_TOKEN,
		ErrorStatus.INTERNET_SERVER_ERROR
	})
	public ResponseDto<List<ScheduleResponse.GetScheduleDto>> getAllSchedulesByUser(
		HttpServletRequest request
	) {
		List<ScheduleResponse.GetScheduleDto> userSchedule = scheduleFacade.getAllSchedulesByUser(
			(Long)request.getAttribute("userId")
		);
		return ResponseDto.onSuccess(userSchedule);
	}

	@Operation(summary = "모든 모임 일정 조회", description = "모든 모임 일정 조회 API")
	@GetMapping("/group/all")
	@ApiErrorCodes({
		ErrorStatus.EMPTY_ACCESS_KEY,
		ErrorStatus.EXPIRATION_ACCESS_TOKEN,
		ErrorStatus.EXPIRATION_REFRESH_TOKEN,
		ErrorStatus.INTERNET_SERVER_ERROR
	})
	public ResponseDto<List<ScheduleResponse.GetScheduleDto>> getAllGroupSchedulesByUser(
		HttpServletRequest request
	) {
		List<ScheduleResponse.GetScheduleDto> groupSchedule = scheduleFacade.getAllGroupSchedulesByUser(
			(Long)request.getAttribute("userId")
		);
		return ResponseDto.onSuccess(groupSchedule);
	}

	@Operation(summary = "일정 수정", description = "일정 수정 API")
	@PatchMapping("/{scheduleId}")
	@ApiErrorCodes({
		ErrorStatus.EMPTY_ACCESS_KEY,
		ErrorStatus.EXPIRATION_ACCESS_TOKEN,
		ErrorStatus.EXPIRATION_REFRESH_TOKEN,
		ErrorStatus.INTERNET_SERVER_ERROR
	})
	public ResponseDto<ScheduleResponse.ScheduleIdDto> modifyUserSchedule(
		HttpServletRequest request,
		@Parameter(description = "일정 ID") @PathVariable("scheduleId") Long scheduleId,
		@RequestBody ScheduleRequest.PostScheduleDto req
	) {
		ScheduleResponse.ScheduleIdDto dto = scheduleFacade.modifySchedule(
			scheduleId,
			req,
			(Long)request.getAttribute("userId")
		);
		return ResponseDto.onSuccess(dto);
	}

	/**
	 * kind 0 은 개인 일정
	 * kind 1 은 모임 일정
	 */
	@Operation(summary = "일정 삭제", description = "개인 캘린더에서 개인 혹은 모임 일정 삭제 API")
	@DeleteMapping("/{scheduleId}/{kind}")
	@ApiErrorCodes({
		ErrorStatus.EMPTY_ACCESS_KEY,
		ErrorStatus.EXPIRATION_ACCESS_TOKEN,
		ErrorStatus.EXPIRATION_REFRESH_TOKEN,
		ErrorStatus.INTERNET_SERVER_ERROR
	})
	public ResponseDto<String> deleteUserSchedule(
		@Parameter(description = "일정 ID") @PathVariable("scheduleId") Long scheduleId,
		@Parameter(description = "일정 타입", example = "0(개인 일정), 1(모임 일정)") @PathVariable("kind") Integer kind,
		HttpServletRequest request
	) {
		scheduleFacade.removeSchedule(scheduleId, kind, (Long)request.getAttribute("userId"));
		return ResponseDto.onSuccess("삭제에 성공하였습니다.");
	}

}
