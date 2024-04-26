package com.example.namo2.domain.schedule.ui;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
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

import com.example.namo2.domain.schedule.application.ScheduleFacade;
import com.example.namo2.domain.schedule.ui.dto.ScheduleRequest;
import com.example.namo2.domain.schedule.ui.dto.ScheduleResponse;
import com.example.namo2.global.annotation.swagger.ApiErrorCodes;
import com.example.namo2.global.common.response.BaseResponse;
import com.example.namo2.global.common.response.BaseResponseStatus;
import com.example.namo2.global.utils.Converter;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "3. Schedule (개인)", description = "개인 일정 관련 API")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/schedules")
public class ScheduleController {
	private final ScheduleFacade scheduleFacade;
	private final Converter converter;

	@Operation(summary = "일정 생성", description = "일정 생성 API")
	@PostMapping("")
	@ApiErrorCodes({
			BaseResponseStatus.EMPTY_ACCESS_KEY,
			BaseResponseStatus.EXPIRATION_ACCESS_TOKEN,
			BaseResponseStatus.EXPIRATION_REFRESH_TOKEN,
			BaseResponseStatus.INTERNET_SERVER_ERROR
	})
	public BaseResponse<ScheduleResponse.ScheduleIdDto> createSchedule(
			@Valid @RequestBody ScheduleRequest.PostScheduleDto postScheduleDto,
			HttpServletRequest request
	) {
		ScheduleResponse.ScheduleIdDto scheduleIddto = scheduleFacade.createSchedule(
				postScheduleDto,
				(Long)request.getAttribute("userId")
		);
		return new BaseResponse<>(scheduleIddto);
	}

	@Operation(summary = "일정 월별 조회", description = "개인 일정 & 모임 일정 월별 조회 API")
	@GetMapping("/{month}")
	@ApiErrorCodes({
			BaseResponseStatus.EMPTY_ACCESS_KEY,
			BaseResponseStatus.EXPIRATION_ACCESS_TOKEN,
			BaseResponseStatus.EXPIRATION_REFRESH_TOKEN,
			BaseResponseStatus.INTERNET_SERVER_ERROR
	})
	public BaseResponse<List<ScheduleResponse.GetScheduleDto>> getSchedulesByUser(
			@PathVariable("month") String month,
			HttpServletRequest request
	) {
		List<LocalDateTime> localDateTimes = converter.convertLongToLocalDateTime(month);
		List<ScheduleResponse.GetScheduleDto> userSchedule = scheduleFacade.getSchedulesByUser(
				(Long)request.getAttribute("userId"), localDateTimes);
		return new BaseResponse<>(userSchedule);
	}

	// TODO: 2024-04-26 "모임" 일정인데 이동해야할지 고민해보기
	@Operation(summary = "모임 일정 월별 조회", description = "모임 일정 월별 조회 API")
	@GetMapping("/moim/{month}")
	@ApiErrorCodes({
			BaseResponseStatus.EMPTY_ACCESS_KEY,
			BaseResponseStatus.EXPIRATION_ACCESS_TOKEN,
			BaseResponseStatus.EXPIRATION_REFRESH_TOKEN,
			BaseResponseStatus.INTERNET_SERVER_ERROR
	})
	public BaseResponse<List<ScheduleResponse.GetScheduleDto>> getMoimSchedulesByUser(
			@PathVariable("month") String month,
			HttpServletRequest request
	) {
		List<LocalDateTime> localDateTimes = converter.convertLongToLocalDateTime(month);
		List<ScheduleResponse.GetScheduleDto> userSchedule = scheduleFacade.getMoimSchedulesByUser(
				(Long)request.getAttribute("userId"),
				localDateTimes
		);
		return new BaseResponse<>(userSchedule);
	}

	// TODO: 2024-04-26 "모임" 일정인데 이동해야할지 고민해보기
	@Operation(summary = "모든 일정 조회", description = "유저의 모든 개인 일정과 모임 일정 조회 API")
	@GetMapping("/all")
	@ApiErrorCodes({
			BaseResponseStatus.EMPTY_ACCESS_KEY,
			BaseResponseStatus.EXPIRATION_ACCESS_TOKEN,
			BaseResponseStatus.EXPIRATION_REFRESH_TOKEN,
			BaseResponseStatus.INTERNET_SERVER_ERROR
	})
	public BaseResponse<List<ScheduleResponse.GetScheduleDto>> getAllSchedulesByUser(
			HttpServletRequest request
	) {
		List<ScheduleResponse.GetScheduleDto> userSchedule = scheduleFacade.getAllSchedulesByUser(
				(Long)request.getAttribute("userId")
		);
		return new BaseResponse<>(userSchedule);
	}

	// TODO: 2024-04-26 "모임" 일정인데 이동해야할지 고민해보기
	@Operation(summary = "모든 모임 일정 조회", description = "모든 모임 일정 조회 API")
	@GetMapping("/moim/all")
	@ApiErrorCodes({
			BaseResponseStatus.EMPTY_ACCESS_KEY,
			BaseResponseStatus.EXPIRATION_ACCESS_TOKEN,
			BaseResponseStatus.EXPIRATION_REFRESH_TOKEN,
			BaseResponseStatus.INTERNET_SERVER_ERROR
	})
	public BaseResponse<List<ScheduleResponse.GetScheduleDto>> getAllMoimSchedulesByUser(
			HttpServletRequest request
	) {
		List<ScheduleResponse.GetScheduleDto> moimSchedule = scheduleFacade.getAllMoimSchedulesByUser(
				(Long)request.getAttribute("userId")
		);
		return new BaseResponse<>(moimSchedule);
	}

	@Operation(summary = "일정 수정", description = "일정 수정 API")
	@PatchMapping("/{scheduleId}")
	@ApiErrorCodes({
			BaseResponseStatus.EMPTY_ACCESS_KEY,
			BaseResponseStatus.EXPIRATION_ACCESS_TOKEN,
			BaseResponseStatus.EXPIRATION_REFRESH_TOKEN,
			BaseResponseStatus.INTERNET_SERVER_ERROR
	})
	public BaseResponse<ScheduleResponse.ScheduleIdDto> modifyUserSchedule(
			HttpServletRequest request,
			@PathVariable("scheduleId") Long scheduleId,
			@RequestBody ScheduleRequest.PostScheduleDto req
	) {
		ScheduleResponse.ScheduleIdDto dto = scheduleFacade.modifySchedule(
				scheduleId,
				req,
				(Long)request.getAttribute("userId")
		);
		return new BaseResponse<>(dto);
	}

	/**
	 * kind 0 은 개인 일정
	 * kind 1 은 모임 일정
	 */
	@Operation(summary = "일정 삭제", description = "개인 캘린더에서 개인 혹은 모임 일정 삭제 API")
	@DeleteMapping("/{scheduleId}/{kind}")
	@ApiErrorCodes({
			BaseResponseStatus.EMPTY_ACCESS_KEY,
			BaseResponseStatus.EXPIRATION_ACCESS_TOKEN,
			BaseResponseStatus.EXPIRATION_REFRESH_TOKEN,
			BaseResponseStatus.INTERNET_SERVER_ERROR
	})
	public BaseResponse<String> deleteUserSchedule(
			@PathVariable("scheduleId") Long scheduleId,
			@PathVariable("kind") Integer kind,
			HttpServletRequest request
	) {
		scheduleFacade.removeSchedule(scheduleId, kind, (Long)request.getAttribute("userId"));
		return new BaseResponse<>("삭제에 성공하였습니다.");
	}

}
