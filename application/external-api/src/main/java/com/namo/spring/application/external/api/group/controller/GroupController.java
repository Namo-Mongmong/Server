package com.namo.spring.application.external.api.group.controller;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

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

import com.namo.spring.application.external.api.group.dto.GroupRequest;
import com.namo.spring.application.external.api.group.dto.GroupResponse;
import com.namo.spring.application.external.api.group.facade.GroupFacade;
import com.namo.spring.application.external.global.annotation.swagger.ApiErrorCodes;
import com.namo.spring.core.common.code.status.ErrorStatus;
import com.namo.spring.core.common.response.ResponseDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "6. Group", description = "그룹 관련 API")
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/groups")
public class GroupController {
	private final GroupFacade groupFacade;

	@Operation(summary = "그룹 생성", description = "그룹 생성 API")
	@PostMapping(value = "",
		consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
		produces = MediaType.APPLICATION_JSON_VALUE
	)
	@ApiErrorCodes(value = {
		ErrorStatus.EMPTY_ACCESS_KEY,
		ErrorStatus.EXPIRATION_ACCESS_TOKEN,
		ErrorStatus.EXPIRATION_REFRESH_TOKEN,
		ErrorStatus.INTERNET_SERVER_ERROR
	})
	public ResponseDto<GroupResponse.GroupIdDto> createGroup(
		@Parameter(description = "그룹 프로필 img") @RequestPart(required = false) MultipartFile img,
		@Parameter(description = "그룹명") @RequestPart(required = true) String groupName,
		HttpServletRequest request
	) {
		GroupResponse.GroupIdDto groupIdDto = groupFacade.createGroup(
			(Long)request.getAttribute("userId"),
			groupName,
			img
		);

		return ResponseDto.onSuccess(groupIdDto);
	}

	@Operation(summary = "그룹 조회", description = "유저가 참여중인 그룹 조회 API")
	@GetMapping("")
	@ApiErrorCodes(value = {
		ErrorStatus.EMPTY_ACCESS_KEY,
		ErrorStatus.EXPIRATION_ACCESS_TOKEN,
		ErrorStatus.EXPIRATION_REFRESH_TOKEN,
		ErrorStatus.INTERNET_SERVER_ERROR
	})
	public ResponseDto<List<GroupResponse.GroupDto>> findGroups(
		HttpServletRequest request
	) {
		List<GroupResponse.GroupDto> groups = groupFacade.getGroups((Long)request.getAttribute("userId"));
		return ResponseDto.onSuccess(groups);
	}

	@Operation(summary = "그룹 이름 수정", description = "그룹 이름 수정 API, 변경자의 입장에서만 수정되어 적용됨")
	@PatchMapping("/name")
	@ApiErrorCodes(value = {
		ErrorStatus.EMPTY_ACCESS_KEY,
		ErrorStatus.EXPIRATION_ACCESS_TOKEN,
		ErrorStatus.EXPIRATION_REFRESH_TOKEN,
		ErrorStatus.INTERNET_SERVER_ERROR
	})
	public ResponseDto<Long> modifyGroupName(
		@Valid @RequestBody GroupRequest.PatchGroupNameDto patchGroupNameDto,
		HttpServletRequest request
	) {
		Long groupId = groupFacade.modifyGroupName(patchGroupNameDto, (Long)request.getAttribute("userId"));
		return ResponseDto.onSuccess(groupId);
	}

	@Operation(summary = "그룹 참여", description = "그룹 참여 API")
	@PatchMapping("/participate/{code}")
	@ApiErrorCodes(value = {
		ErrorStatus.EMPTY_ACCESS_KEY,
		ErrorStatus.EXPIRATION_ACCESS_TOKEN,
		ErrorStatus.EXPIRATION_REFRESH_TOKEN,
		ErrorStatus.INTERNET_SERVER_ERROR
	})
	public ResponseDto<GroupResponse.GroupParticipantDto> createGroupAndUser(
		@Parameter(description = "그룹 참여용 코드") @PathVariable("code") String code,
		HttpServletRequest request
	) {
		GroupResponse.GroupParticipantDto groupParticipantDto = groupFacade.createGroupAndUser(
			(Long)request.getAttribute("userId"),
			code);
		return ResponseDto.onSuccess(groupParticipantDto);
	}

	@Operation(summary = "그룹 탈퇴", description = "그룹 탈퇴 API")
	@DeleteMapping("/withdraw/{groupId}")
	@ApiErrorCodes(value = {
		ErrorStatus.EMPTY_ACCESS_KEY,
		ErrorStatus.EXPIRATION_ACCESS_TOKEN,
		ErrorStatus.EXPIRATION_REFRESH_TOKEN,
		ErrorStatus.INTERNET_SERVER_ERROR
	})
	public ResponseDto<Void> removeGroupAndUser(
		@Parameter(description = "그룹 ID") @PathVariable("groupId") Long groupId,
		HttpServletRequest request
	) {
		groupFacade.removeGroupAndUser((Long)request.getAttribute("userId"), groupId);
		return ResponseDto.onSuccess(null);
	}
}
