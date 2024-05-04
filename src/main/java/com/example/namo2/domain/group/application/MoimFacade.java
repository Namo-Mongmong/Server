package com.example.namo2.domain.group.application;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.namo2.domain.group.application.converter.MoimAndUserConverter;
import com.example.namo2.domain.group.application.converter.MoimConverter;
import com.example.namo2.domain.group.application.converter.GroupResponseConverter;
import com.example.namo2.domain.group.application.impl.MoimAndUserService;
import com.example.namo2.domain.group.application.impl.MoimService;
import com.example.namo2.domain.group.domain.Moim;
import com.example.namo2.domain.group.domain.MoimAndUser;
import com.example.namo2.domain.group.ui.dto.GroupRequest;
import com.example.namo2.domain.group.ui.dto.GroupResponse;

import com.example.namo2.domain.user.application.impl.UserService;
import com.example.namo2.domain.user.domain.User;

import com.example.namo2.global.common.exception.BaseException;
import com.example.namo2.global.common.response.BaseResponseStatus;
import com.example.namo2.global.utils.FileUtils;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MoimFacade {
	/**
	 * TODO
	 * ISSUE 설명: Moim_USER_COLOR 에 대한 부여를 현재 총 모임원의 index를 통해서 부여함
	 * 이 경우 모임원이 탈퇴하고 다시금 들어올 경우 동일한 color를 부여받는 모임원이 생김
	 * <p>
	 * BaseURL을 직접 넣어주세요.
	 */
	private static final int[] MOIM_USERS_COLOR = new int[] {5, 6, 7, 8, 9, 10, 11, 12, 13, 14};
	private final MoimService moimService;
	private final UserService userService;
	private final MoimAndUserService moimAndUserService;
	private final FileUtils fileUtils;

	@Value("${moim.base-url-image}")
	private String BASE_URL;

	@Transactional(readOnly = false)
	public GroupResponse.GroupIdDto createMoim(Long userId, String groupName, MultipartFile img) {
		User user = userService.getUser(userId);
		String url = BASE_URL;
		if (img != null && !img.isEmpty()) {
			url = fileUtils.uploadImage(img);
		}
		Moim moim = MoimConverter.toMoim(groupName, url);
		moimService.create(moim);

		MoimAndUser moimAndUser = MoimAndUserConverter
			.toMoimAndUser(groupName, MOIM_USERS_COLOR[0], user, moim);
		moimAndUserService.create(moimAndUser, moim);
		return GroupResponseConverter.toMoimIdDto(moim);
	}

	@Transactional(readOnly = true)
	public List<GroupResponse.GroupDto> getMoims(Long userId) {
		User user = userService.getUser(userId);
		List<MoimAndUser> curUsersMoimAndUsers = moimAndUserService.getMoimAndUsers(user);
		List<Moim> moimsInUser = curUsersMoimAndUsers
			.stream().map(MoimAndUser::getMoim)
			.collect(Collectors.toList());

		List<MoimAndUser> moimAndUsersInMoims = moimAndUserService
			.getMoimAndUsers(moimsInUser);
		return GroupResponseConverter.toMoimDtos(moimAndUsersInMoims, curUsersMoimAndUsers);
	}

	@Transactional(readOnly = false)
	public Long modifyMoimName(GroupRequest.PatchGroupNameDto patchGroupNameDto, Long userId) {
		User user = userService.getUser(userId);
		Moim moim = moimService.getMoimWithMoimAndUsersByMoimId(patchGroupNameDto.getGroupId());
		MoimAndUser moimAndUser = moimAndUserService.getMoimAndUser(moim, user);
		moimAndUser.updateCustomName(patchGroupNameDto.getGroupName());
		return moim.getId();
	}

	@Transactional(readOnly = false)
	public GroupResponse.GroupParticipantDto createMoimAndUser(Long userId, String code) {
		User user = userService.getUser(userId);
		Moim moim = moimService.getMoimWithMoimAndUsersByCode(code);

		MoimAndUser moimAndUser = MoimAndUserConverter
			.toMoimAndUser(moim.getName(), selectColor(moim), user, moim);
		moimAndUserService.create(moimAndUser, moim);
		return GroupResponseConverter.toMoimParticipantDto(moim);
	}

	private int selectColor(Moim moim) {
		Set<Integer> colors = moim.getMoimAndUsers()
			.stream()
			.map(MoimAndUser::getColor)
			.collect(Collectors.toSet());
		return Arrays.stream(MOIM_USERS_COLOR)
			.filter((color) -> !colors.contains(color))
			.findFirst()
			.orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_COLOR));
	}

	@Transactional(readOnly = false)
	public void removeMoimAndUser(Long userId, Long moimId) {
		User user = userService.getUser(userId);
		Moim moim = moimService.getMoimHavingLockById(moimId);
		MoimAndUser moimAndUser = moimAndUserService.getMoimAndUser(moim, user);
		moimAndUserService.removeMoimAndUser(moimAndUser, moim);
	}
}
