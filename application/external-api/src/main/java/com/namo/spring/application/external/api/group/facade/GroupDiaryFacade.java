package com.namo.spring.application.external.api.group.facade;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.namo.spring.application.external.api.group.converter.GroupActivityConverter;
import com.namo.spring.application.external.api.group.converter.GroupDiaryResponseConverter;
import com.namo.spring.application.external.api.group.converter.GroupMemoConverter;
import com.namo.spring.application.external.api.group.dto.GroupDiaryRequest;
import com.namo.spring.application.external.api.group.dto.GroupDiaryResponse;
import com.namo.spring.application.external.api.group.dto.GroupScheduleRequest;
import com.namo.spring.application.external.api.group.service.GroupActivityService;
import com.namo.spring.application.external.api.group.service.GroupMemoService;
import com.namo.spring.application.external.api.group.service.GroupScheduleAndUserService;
import com.namo.spring.application.external.api.group.service.GroupScheduleService;
import com.namo.spring.application.external.api.user.service.UserService;
import com.namo.spring.core.infra.common.aws.s3.FileUtils;
import com.namo.spring.core.infra.common.constant.FilePath;
import com.namo.spring.db.mysql.domains.group.domain.MoimMemo;
import com.namo.spring.db.mysql.domains.group.domain.MoimMemoLocation;
import com.namo.spring.db.mysql.domains.group.domain.MoimMemoLocationAndUser;
import com.namo.spring.db.mysql.domains.group.domain.MoimMemoLocationImg;
import com.namo.spring.db.mysql.domains.group.domain.MoimSchedule;
import com.namo.spring.db.mysql.domains.group.domain.MoimScheduleAndUser;
import com.namo.spring.db.mysql.domains.user.domain.User;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class GroupDiaryFacade {
	private final GroupScheduleService groupScheduleService;

	private final GroupScheduleAndUserService groupScheduleAndUserService;
	private final GroupMemoService groupMemoService;
	private final GroupActivityService groupActivityService;
	private final UserService userService;

	private final FileUtils fileUtils;

	@Transactional(readOnly = false)
	public void createGroupDiary(Long groupScheduleId, GroupDiaryRequest.LocationDto locationDto,
		List<MultipartFile> imgs) {
		MoimMemo groupMemo = getGroupMemo(groupScheduleId);
		MoimMemoLocation groupActivity = createGroupActivity(groupMemo, locationDto);

		createGroupActivityAndUsers(locationDto, groupActivity);
		createGroupActivityImgs(imgs, groupActivity);
	}

	private MoimMemo getGroupMemo(Long groupScheduleId) {
		MoimSchedule groupSchedule = groupScheduleService.getGroupSchedule(groupScheduleId);
		return groupMemoService.getGroupMemoOrNull(groupSchedule)
			.orElseGet(
				() -> groupMemoService.createGroupMemo(GroupMemoConverter.toGroupMemo(groupSchedule))
			);
	}

	private MoimMemoLocation createGroupActivity(MoimMemo groupMemo, GroupDiaryRequest.LocationDto locationDto) {
		MoimMemoLocation groupActivity = GroupActivityConverter.toGroupActivity(groupMemo, locationDto);
		return groupActivityService.createGroupActivity(groupActivity, groupMemo);
	}

	private void createGroupActivityAndUsers(GroupDiaryRequest.LocationDto locationDto,
		MoimMemoLocation groupActivity) {
		List<User> users = userService.getUsersInGroupSchedule(locationDto.getParticipants());
		List<MoimMemoLocationAndUser> groupActivityAndUsers = GroupActivityConverter
			.toGroupActivityAndUsers(groupActivity, users);
		groupActivityService.createGroupActivityAndUsers(groupActivityAndUsers);
	}

	/**
	 * TODO: 적절한 validation: 처리 필요
	 */
	private void createGroupActivityImgs(List<MultipartFile> imgs, MoimMemoLocation groupActivity) {
		if (imgs == null) {
			return;
		}
		/**
		 * imgs 에 대한 validation 처리 필요
		 * 값이 3개 이상일 경우 OVER_IMAGES_FAILURE 필요
		 */
		List<String> urls = fileUtils.uploadImages(imgs, FilePath.GROUP_ACTIVITY_IMG);
		for (String url : urls) {
			MoimMemoLocationImg groupActivityImg = GroupActivityConverter
				.toGroupActivityImg(groupActivity, url);
			groupActivityService.createGroupActivityImg(groupActivityImg);
		}
	}

	@Transactional(readOnly = false)
	public void modifyGroupActivity(Long activityId, GroupDiaryRequest.LocationDto locationDto,
		List<MultipartFile> imgs) {
		MoimMemoLocation groupActivity = groupActivityService.getGroupActivityWithImgs(activityId);
		groupActivity.update(locationDto.getName(), locationDto.getMoney());

		groupActivityService.removeGroupActivityAndUsers(groupActivity);
		createGroupActivityAndUsers(locationDto, groupActivity);

		removeGroupActivityImgs(groupActivity);
		createGroupActivityImgs(imgs, groupActivity);
	}

	private void removeGroupActivityImgs(MoimMemoLocation groupActivity) {
		List<String> urls = groupActivity.getMoimMemoLocationImgs()
			.stream()
			.map(MoimMemoLocationImg::getUrl)
			.toList();
		fileUtils.deleteImages(urls, FilePath.GROUP_ACTIVITY_IMG);
		groupActivityService.removeGroupActivityImgs(groupActivity);
	}

	@Transactional(readOnly = false)
	public void removeGroupActivity(Long activityId) {
		MoimMemoLocation groupActivity = groupActivityService.getGroupActivityWithImgs(activityId);

		groupActivityService.removeGroupActivityAndUsers(groupActivity);
		removeGroupActivityImgs(groupActivity);
		groupActivityService.removeGroupActivity(groupActivity);
	}

	@Transactional(readOnly = false)
	public GroupDiaryResponse.GroupDiaryDto getGroupDiaryWithLocations(Long groupScheduleId) {
		MoimSchedule groupSchedule = groupScheduleService.getGroupSchedule(groupScheduleId);
		MoimMemo groupMemo = groupMemoService.getGroupMemoWithUsers(groupSchedule);
		List<MoimMemoLocation> groupActivities = groupActivityService.getGroupActivities(groupSchedule);
		List<MoimMemoLocationAndUser> groupActivityAndUsers
			= groupActivityService.getGroupActivityAndUsers(groupActivities);
		return GroupDiaryResponseConverter.toGroupDiaryDto(groupMemo, groupActivities, groupActivityAndUsers);
	}

	@Transactional(readOnly = true)
	public GroupDiaryResponse.SliceDiaryDto<GroupDiaryResponse.DiaryDto> getMonthMonthGroupDiary(Long userId,
		List<LocalDateTime> dates, Pageable page) {
		User user = userService.getUser(userId);
		List<MoimScheduleAndUser> groupScheduleAndUsersForMonthGroupMemo
			= groupScheduleAndUserService.getGroupScheduleAndUsersForMonthGroupDiary(user, dates, page);
		return GroupDiaryResponseConverter.toSliceDiaryDto(groupScheduleAndUsersForMonthGroupMemo, page);
	}

	@Transactional(readOnly = false)
	public void createGroupMemo(Long groupScheduleId, Long userId,
		GroupScheduleRequest.PostGroupScheduleTextDto groupScheduleText) {
		MoimSchedule groupSchedule = groupScheduleService.getGroupSchedule(groupScheduleId);
		User user = userService.getUser(userId);
		MoimScheduleAndUser groupScheduleAndUser = groupScheduleAndUserService.getGroupScheduleAndUser(groupSchedule,
			user);
		groupScheduleAndUserService.modifyMemo(groupScheduleAndUser, groupScheduleText.getText());
	}

	@Transactional(readOnly = false)
	public void removeGroupDiary(Long groupScheduleId) {
		MoimMemo groupMemoWithLocations = groupMemoService.getGroupMemoWithLocations(groupScheduleId);
		for (MoimMemoLocation groupActivity : groupMemoWithLocations.getMoimMemoLocations()) {
			removeGroupActivity(groupActivity.getId());
		}
		groupMemoService.removeGroupMemo(groupMemoWithLocations);
	}

	@Transactional(readOnly = false)
	public void removePersonGroupDiary(Long scheduleId, Long userId) {
		MoimSchedule groupSchedule = groupScheduleService.getGroupSchedule(scheduleId);
		User user = userService.getUser(userId);
		MoimScheduleAndUser groupScheduleAndUser
			= groupScheduleAndUserService.getGroupScheduleAndUser(groupSchedule, user);
		groupScheduleAndUserService.removeGroupScheduleDiaryInPersonalSpace(groupScheduleAndUser);
	}

	@Transactional(readOnly = true)
	public GroupDiaryResponse.DiaryDto getGroupDiaryDetail(Long groupScheduleId, Long userId) {
		User user = userService.getUser(userId);
		MoimScheduleAndUser groupScheduleAndUser = groupScheduleAndUserService.getGroupScheduleAndUser(groupScheduleId,
			user);
		return GroupDiaryResponseConverter.toDiaryDto(groupScheduleAndUser);
	}
}
