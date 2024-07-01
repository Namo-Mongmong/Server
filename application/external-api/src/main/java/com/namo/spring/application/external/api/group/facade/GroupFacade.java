package com.namo.spring.application.external.api.group.facade;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.namo.spring.application.external.api.group.converter.GroupAndUserConverter;
import com.namo.spring.application.external.api.group.converter.GroupConverter;
import com.namo.spring.application.external.api.group.converter.GroupResponseConverter;
import com.namo.spring.application.external.api.group.dto.GroupRequest;
import com.namo.spring.application.external.api.group.dto.GroupResponse;
import com.namo.spring.application.external.api.group.service.GroupAndUserService;
import com.namo.spring.application.external.api.group.service.GroupService;
import com.namo.spring.application.external.api.user.service.UserService;
import com.namo.spring.core.common.code.status.ErrorStatus;
import com.namo.spring.core.common.exception.GroupException;
import com.namo.spring.core.infra.common.aws.s3.FileUtils;
import com.namo.spring.core.infra.common.constant.FilePath;
import com.namo.spring.db.mysql.domains.group.domain.Moim;
import com.namo.spring.db.mysql.domains.group.domain.MoimAndUser;
import com.namo.spring.db.mysql.domains.user.domain.User;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class GroupFacade {
	/**
	 * TODO
	 * ISSUE 설명: Group_USER_COLOR 에 대한 부여를 현재 총 모임원의 index를 통해서 부여함
	 * 이 경우 모임원이 탈퇴하고 다시금 들어올 경우 동일한 color를 부여받는 모임원이 생김
	 * <p>
	 * BaseURL을 직접 넣어주세요.
	 */
	private static final int[] GROUP_USERS_COLOR = new int[] {5, 6, 7, 8, 9, 10, 11, 12, 13, 14};
	private final GroupService groupService;
	private final UserService userService;
	private final GroupAndUserService groupAndUserService;
	private final FileUtils fileUtils;

	@Value("${moim.base-url-image}")
	private String BASE_URL;

	@Transactional(readOnly = false)
	public GroupResponse.GroupIdDto createGroup(Long userId, String groupName, MultipartFile img) {
		User user = userService.getUser(userId);
		String url = BASE_URL;

		if (img != null && !img.isEmpty()) {
			url = fileUtils.uploadImage(img, FilePath.GROUP_PROFILE_IMG);
		}

		Moim group = GroupConverter.toGroup(groupName, url);
		groupService.createGroup(group);

		MoimAndUser groupAndUser = GroupAndUserConverter
			.toGroupAndUser(groupName, GROUP_USERS_COLOR[0], user, group);
		groupAndUserService.createGroupAndUser(groupAndUser, group);
		return GroupResponseConverter.toGroupIdDto(group);
	}

	@Transactional(readOnly = true)
	public List<GroupResponse.GroupDto> getGroups(Long userId) {
		User user = userService.getUser(userId);
		List<MoimAndUser> curUsersGroupAndUsers = groupAndUserService.getGroupAndUsers(user);
		List<Moim> groupsInUser = curUsersGroupAndUsers
			.stream().map(MoimAndUser::getMoim)
			.collect(Collectors.toList());

		List<MoimAndUser> groupAndUsersInGroups = groupAndUserService
			.getGroupAndUsers(groupsInUser);
		return GroupResponseConverter.toGroupDtos(groupAndUsersInGroups, curUsersGroupAndUsers);
	}

	@Transactional(readOnly = false)
	public Long modifyGroupName(GroupRequest.PatchGroupNameDto patchGroupNameDto, Long userId) {
		User user = userService.getUser(userId);
		Moim group = groupService.getGroupWithGroupAndUsersByGroupId(patchGroupNameDto.getGroupId());
		MoimAndUser groupAndUser = groupAndUserService.getGroupAndUser(group, user);
		groupAndUser.updateCustomName(patchGroupNameDto.getGroupName());
		return group.getId();
	}

	@Transactional(readOnly = false)
	public GroupResponse.GroupParticipantDto createGroupAndUser(Long userId, String code) {
		User user = userService.getUser(userId);
		Moim group = groupService.getGroupWithGroupAndUsersByCode(code);

		MoimAndUser groupAndUser = GroupAndUserConverter
			.toGroupAndUser(group.getName(), selectColor(group), user, group);
		groupAndUserService.createGroupAndUser(groupAndUser, group);
		return GroupResponseConverter.toGroupParticipantDto(group);
	}

	// TODO: error throw 위치 변경 필요
	private int selectColor(Moim group) {
		Set<Integer> colors = group.getMoimAndUsers()
			.stream()
			.map(MoimAndUser::getColor)
			.collect(Collectors.toSet());
		return Arrays.stream(GROUP_USERS_COLOR)
			.filter((color) -> !colors.contains(color))
			.findFirst()
			.orElseThrow(() -> new GroupException(ErrorStatus.NOT_FOUND_COLOR));
	}

	@Transactional(readOnly = false)
	public void removeGroupAndUser(Long userId, Long groupId) {
		User user = userService.getUser(userId);
		Moim group = groupService.getGroupHavingLockById(groupId);
		MoimAndUser groupAndUser = groupAndUserService.getGroupAndUser(group, user);
		groupAndUserService.removeGroupAndUser(groupAndUser, group);
	}
}
