package com.example.namo2.domain.memo.ui;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.example.namo2.domain.memo.application.MoimMemoFacade;
import com.example.namo2.domain.memo.ui.dto.MoimMemoRequest;
import com.example.namo2.domain.memo.ui.dto.MoimMemoResponse;

import com.example.namo2.domain.moim.ui.dto.MoimScheduleRequest;

import com.example.namo2.global.common.response.BaseResponse;
import com.example.namo2.global.utils.Converter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "Moim Memo", description = "모임 메모 관련 API")
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/moims")
public class MoimMemoController {
	private final MoimMemoFacade moimMemoFacade;
	private final Converter converter;

	@Operation(summary = "모임 메모 생성", description = "모임 모임 매모 생성 API")
	@PostMapping("/schedule/memo/{moimScheduleId}")
	public BaseResponse<Void> createMoimMemo(@RequestPart(required = false) List<MultipartFile> imgs,
		@PathVariable Long moimScheduleId,
		@RequestPart(required = true) String name,
		@RequestParam(defaultValue = "0") String money,
		@RequestPart(required = true) String participants) {
		MoimMemoRequest.LocationDto locationDto = new MoimMemoRequest.LocationDto(name, money, participants);
		moimMemoFacade.create(moimScheduleId, locationDto, imgs);
		return BaseResponse.ok();
	}

	@Operation(summary = "모임 메모 장소 수정", description = "모임 메모 장소 수정 API")
	@PatchMapping("/schedule/memo/{memoLocationId}")
	public BaseResponse<Object> updateMoimMemo(@RequestPart(required = false) List<MultipartFile> imgs,
		@PathVariable Long memoLocationId,
		@RequestPart(required = true) String name,
		@RequestPart(required = true) String money,
		@RequestPart(required = true) String participants) {
		MoimMemoRequest.LocationDto locationDto = new MoimMemoRequest.LocationDto(name, money, participants);
		moimMemoFacade.modifyMoimMemoLocation(memoLocationId, locationDto, imgs);
		return BaseResponse.ok();
	}

	@Operation(summary = "모임 메모 조회", description = "모임 메모 조회 API")
	@GetMapping("/schedule/memo/{moimScheduleId}")
	public BaseResponse<Object> getMoimMemo(@PathVariable("moimScheduleId") Long moimScheduleId) {
		MoimMemoResponse.MoimMemoDto moimMemoDto = moimMemoFacade.getMoimMemoWithLocations(moimScheduleId);
		return new BaseResponse(moimMemoDto);
	}

	@Operation(summary = "월간 모임 메모 조회", description = "월간 모임 메모 조회 API")
	@GetMapping("/schedule/memo/month/{month}")
	public BaseResponse<MoimMemoResponse.SliceDiaryDto<MoimMemoResponse.DiaryDto>> findMonthMoimMemo(
		@PathVariable("month") String month, Pageable pageable, HttpServletRequest request) {
		List<LocalDateTime> localDateTimes = converter.convertLongToLocalDateTime(month);
		MoimMemoResponse.SliceDiaryDto<MoimMemoResponse.DiaryDto> diaryDto = moimMemoFacade
			.getMonthMonthMoimMemo((Long)request.getAttribute("userId"), localDateTimes, pageable);
		return new BaseResponse(diaryDto);
	}

	@Operation(summary = "모임 메모 장소 삭제", description = "모임 메모 장소 삭제 API")
	@DeleteMapping("/schedule/memo/{memoLocationId}")
	public BaseResponse<Object> removeMoimMemoLocation(@PathVariable Long memoLocationId) {
		moimMemoFacade.removeMoimMemoLocation(memoLocationId);
		return BaseResponse.ok();
	}

	@Operation(summary = "모임 메모 텍스트 추가", description = "모임 메모 텍스트 추가 API")
	@PatchMapping("/schedule/memo/text/{moimScheduleId}")
	public BaseResponse<Object> createMoimScheduleText(@PathVariable Long moimScheduleId,
		HttpServletRequest request,
		@RequestBody MoimScheduleRequest.PostMoimScheduleTextDto moimScheduleText) {
		moimMemoFacade.createMoimScheduleText(moimScheduleId,
			(Long)request.getAttribute("userId"),
			moimScheduleText);
		return BaseResponse.ok();
	}
}
