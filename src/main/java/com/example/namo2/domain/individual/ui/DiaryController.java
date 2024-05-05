package com.example.namo2.domain.individual.ui;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.example.namo2.domain.individual.application.DiaryFacade;
import com.example.namo2.domain.individual.ui.dto.DiaryResponse;

import com.example.namo2.global.annotation.swagger.ApiErrorCodes;
import com.example.namo2.global.common.response.BaseResponse;
import com.example.namo2.global.common.response.BaseResponseStatus;
import com.example.namo2.global.utils.Converter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "4. Diary (개인)", description = "개인 기록 관련 API")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/diaries")
public class DiaryController {
	private final DiaryFacade diaryFacade;
	private final Converter converter;

	@Operation(summary = "기록 생성", description = "기록 생성 API")
	@PostMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiErrorCodes({
			BaseResponseStatus.EMPTY_ACCESS_KEY,
			BaseResponseStatus.EXPIRATION_ACCESS_TOKEN,
			BaseResponseStatus.EXPIRATION_REFRESH_TOKEN,
			BaseResponseStatus.INTERNET_SERVER_ERROR
	})
	public BaseResponse<DiaryResponse.ScheduleIdDto> createDiary(
			@RequestPart(required = false) List<MultipartFile> imgs,
			@RequestPart String scheduleId,
			@RequestPart(required = false) String content
	) {
		DiaryResponse.ScheduleIdDto dto = diaryFacade.createDiary(Long.valueOf(scheduleId), content, imgs);
		return new BaseResponse<>(dto);
	}

	@Operation(summary = "일정 기록 월간 조회", description = "일정 기록 월간 조회 API")
	@GetMapping("/{month}")
	@ApiErrorCodes({
			BaseResponseStatus.EMPTY_ACCESS_KEY,
			BaseResponseStatus.EXPIRATION_ACCESS_TOKEN,
			BaseResponseStatus.EXPIRATION_REFRESH_TOKEN,
			BaseResponseStatus.INTERNET_SERVER_ERROR
	})
	public BaseResponse<DiaryResponse.SliceDiaryDto> findDiaryByMonth(
			@PathVariable("month") String month,
			Pageable pageable,
			HttpServletRequest request
	) {
		Long userId = (Long)request.getAttribute("userId");
		List<LocalDateTime> localDateTimes = converter.convertLongToLocalDateTime(month);
		DiaryResponse.SliceDiaryDto diaries = diaryFacade.getMonthDiary(userId, localDateTimes, pageable);
		return new BaseResponse<>(diaries);
	}

	@Operation(summary = "개인 일정 기록 전체 조회", description = "개인 일정 기록 전체 조회 API")
	@GetMapping("/all")
	@ApiErrorCodes({
			BaseResponseStatus.EMPTY_ACCESS_KEY,
			BaseResponseStatus.EXPIRATION_ACCESS_TOKEN,
			BaseResponseStatus.EXPIRATION_REFRESH_TOKEN,
			BaseResponseStatus.INTERNET_SERVER_ERROR
	})
	public BaseResponse<List<DiaryResponse.GetDiaryByUserDto>> findAllDiary(
			HttpServletRequest request
	) {
		Long userId = (Long)request.getAttribute("userId");
		List<DiaryResponse.GetDiaryByUserDto> diaries = diaryFacade.getAllDiariesByUser(userId);
		return new BaseResponse<>(diaries);
	}

	//일정 별 기록 조회 == 1개 조회
	@Operation(summary = "일정 기록 개별 조회", description = "일정 기록 개별 조회 API")
	@GetMapping("/{scheduleId}")
	@ApiErrorCodes({
			BaseResponseStatus.EMPTY_ACCESS_KEY,
			BaseResponseStatus.EXPIRATION_ACCESS_TOKEN,
			BaseResponseStatus.EXPIRATION_REFRESH_TOKEN,
			BaseResponseStatus.INTERNET_SERVER_ERROR
	})
	public BaseResponse<DiaryResponse.GetDiaryByScheduleDto> findDiaryById(
			@PathVariable("scheduleId") Long scheduleId
	) {
		DiaryResponse.GetDiaryByScheduleDto diary = diaryFacade.getDiaryBySchedule(scheduleId);
		return new BaseResponse<>(diary);
	}

	@Operation(summary = "일정 기록 수정", description = "일정 기록 수정 API")
	@PatchMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiErrorCodes({
			BaseResponseStatus.EMPTY_ACCESS_KEY,
			BaseResponseStatus.EXPIRATION_ACCESS_TOKEN,
			BaseResponseStatus.EXPIRATION_REFRESH_TOKEN,
			BaseResponseStatus.INTERNET_SERVER_ERROR
	})
	public BaseResponse<String> updateDiary(
			@RequestPart(required = false) List<MultipartFile> imgs,
			@RequestPart String scheduleId,
			@RequestPart(required = false) String content
	) {
		diaryFacade.removeDiary(Long.valueOf(scheduleId));
		diaryFacade.createDiary(Long.valueOf(scheduleId), content, imgs);
		return new BaseResponse<>("수정에 성공하셨습니다.");
	}

	@Operation(summary = "일정 기록 삭제", description = "일정 기록 삭제 API")
	@DeleteMapping("/{scheduleId}")
	@ApiErrorCodes({
			BaseResponseStatus.EMPTY_ACCESS_KEY,
			BaseResponseStatus.EXPIRATION_ACCESS_TOKEN,
			BaseResponseStatus.EXPIRATION_REFRESH_TOKEN,
			BaseResponseStatus.INTERNET_SERVER_ERROR
	})
	public BaseResponse<String> deleteDiary(
			@PathVariable("scheduleId") Long scheduleId
	) {
		diaryFacade.removeDiary(scheduleId);
		return new BaseResponse<>("삭제에 성공하셨습니다.");
	}
}
