package com.namo.spring.application.external.api.group.facade;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.namo.spring.application.external.api.group.converter.GroupAndUserConverter;
import com.namo.spring.application.external.api.group.converter.GroupScheduleConverter;
import com.namo.spring.application.external.api.group.converter.GroupScheduleResponseConverter;
import com.namo.spring.application.external.api.group.dto.GroupScheduleRequest;
import com.namo.spring.application.external.api.group.dto.GroupScheduleResponse;
import com.namo.spring.application.external.api.group.service.GroupActivityService;
import com.namo.spring.application.external.api.group.service.GroupAndUserService;
import com.namo.spring.application.external.api.group.service.GroupMemoService;
import com.namo.spring.application.external.api.group.service.GroupScheduleAndUserService;
import com.namo.spring.application.external.api.group.service.GroupScheduleService;
import com.namo.spring.application.external.api.group.service.GroupService;
import com.namo.spring.application.external.api.individual.service.CategoryService;
import com.namo.spring.application.external.api.individual.service.ScheduleService;
import com.namo.spring.application.external.api.user.service.UserService;
import com.namo.spring.core.infra.common.aws.s3.FileUtils;
import com.namo.spring.core.infra.common.constant.FilePath;
import com.namo.spring.db.mysql.domains.group.domain.Moim;
import com.namo.spring.db.mysql.domains.group.domain.MoimAndUser;
import com.namo.spring.db.mysql.domains.group.domain.MoimMemo;
import com.namo.spring.db.mysql.domains.group.domain.MoimMemoLocation;
import com.namo.spring.db.mysql.domains.group.domain.MoimMemoLocationImg;
import com.namo.spring.db.mysql.domains.group.domain.MoimSchedule;
import com.namo.spring.db.mysql.domains.group.domain.MoimScheduleAlarm;
import com.namo.spring.db.mysql.domains.group.domain.MoimScheduleAndUser;
import com.namo.spring.db.mysql.domains.individual.domain.Category;
import com.namo.spring.db.mysql.domains.individual.domain.Schedule;
import com.namo.spring.db.mysql.domains.individual.type.Location;
import com.namo.spring.db.mysql.domains.individual.type.Period;
import com.namo.spring.db.mysql.domains.user.domain.User;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class GroupScheduleFacade {
	private final UserService userService;
	private final GroupService groupService;
	private final GroupAndUserService groupAndUserService;
	private final GroupScheduleService groupScheduleService;
	private final GroupScheduleAndUserService groupScheduleAndUserService;
	private final GroupMemoService groupMemoService;
	private final GroupActivityService groupActivityService;
	private final ScheduleService scheduleService;
	private final CategoryService categoryService;

	private final FileUtils fileUtils;

	/**
	 * 버그 발생 우려;
	 * categories 수정시 모임과 기본 카테고리에 대해서는 수정이 불가능하게 해야함
	 */
	@Transactional(readOnly = false)
	public Long createSchedule(GroupScheduleRequest.PostGroupScheduleDto groupScheduleDto) {
		Moim group = groupService.getGroupWithGroupAndUsersByGroupId(groupScheduleDto.getGroupId());
		Period period = GroupScheduleConverter.toPeriod(groupScheduleDto);
		Location location = GroupScheduleConverter.toLocation(groupScheduleDto);
		MoimSchedule groupSchedule = GroupScheduleConverter
			.toGroupSchedule(group, period, location, groupScheduleDto);
		MoimSchedule savedGroupSchedule = groupScheduleService.createGroupSchedule(groupSchedule);

		createGroupScheduleAndUsers(groupScheduleDto.getUsers(), savedGroupSchedule, group);

		return savedGroupSchedule.getId();
	}

	private void createGroupScheduleAndUsers(List<Long> usersId, MoimSchedule savedGroupSchedule, Moim group) {
		List<User> users = userService.getUsersInGroupSchedule(usersId);
		List<Category> categories = categoryService
			.getGroupUsersCategories(users);
		List<MoimScheduleAndUser> groupScheduleAndUsers = GroupScheduleConverter
			.toGroupScheduleAndUsers(categories, savedGroupSchedule, users);
		groupScheduleAndUserService.createAll(groupScheduleAndUsers, group);
	}

	@Transactional(readOnly = false)
	public void modifyGroupSchedule(GroupScheduleRequest.PatchGroupScheduleDto groupScheduleDto) {
		MoimSchedule groupSchedule = groupScheduleService.getGroupSchedule(groupScheduleDto.getMoimScheduleId());
		Period period = GroupScheduleConverter.toPeriod(groupScheduleDto);
		Location location = GroupScheduleConverter.toLocation(groupScheduleDto);
		groupSchedule.update(groupScheduleDto.getName(), period, location);
		groupScheduleAndUserService.removeGroupScheduleAndUser(groupSchedule);
		createGroupScheduleAndUsers(groupScheduleDto.getUsers(), groupSchedule, groupSchedule.getMoim());
	}

	@Transactional(readOnly = false)
	public void modifyGroupScheduleCategory(GroupScheduleRequest.PatchGroupScheduleCategoryDto scheduleCategoryDto,
		Long userId) {
		MoimSchedule groupSchedule = groupScheduleService.getGroupSchedule(scheduleCategoryDto.getMoimScheduleId());
		User user = userService.getUser(userId);
		Category category = categoryService.getCategory(scheduleCategoryDto.getCategoryId());
		MoimScheduleAndUser groupScheduleAndUser = groupScheduleAndUserService.getGroupScheduleAndUser(groupSchedule,
			user);
		groupScheduleAndUser.updateCategory(category);
	}

	@Transactional(readOnly = false)
	public void removeGroupSchedule(Long groupScheduleId, Long userId) {
		MoimSchedule groupSchedule = groupScheduleService.getGroupScheduleWithGroupDiary(groupScheduleId);
		List<MoimScheduleAndUser> groupScheduleAndUsers = groupScheduleService.getGroupScheduleAndUsers(groupSchedule);

		existGroupAndUser(userId, groupSchedule.getMoim());

		removeGroupScheduleMemo(groupSchedule.getMoimMemo());

		groupScheduleAndUserService.removeGroupScheduleAlarm(groupScheduleAndUsers);
		groupScheduleAndUserService.removeGroupScheduleAndUser(groupSchedule);
		groupScheduleService.removeGroupSchedule(groupSchedule);
	}

	private void existGroupAndUser(Long userId, Moim group) {
		User user = userService.getUser(userId);
		groupAndUserService.getGroupAndUser(group, user);
	}

	private void removeGroupScheduleMemo(MoimMemo groupMemo) {
		if (groupMemo == null) {
			return;
		}
		List<MoimMemoLocation> groupMemoLocations = groupActivityService.getGroupActivityWithImgs(groupMemo);
		groupActivityService.removeGroupActivityAndUsers(groupMemoLocations);
		removeGroupActivityImgs(groupMemoLocations);
		groupMemoService.removeGroupMemo(groupMemo);
	}

	private void removeGroupActivityImgs(List<MoimMemoLocation> groupMemoLocations) {
		List<MoimMemoLocationImg> groupMemoLocationImgs
			= groupActivityService.getGroupActivityImgs(groupMemoLocations);
		List<String> urls = groupMemoLocationImgs.stream()
			.map(MoimMemoLocationImg::getUrl)
			.toList();
		fileUtils.deleteImages(urls, FilePath.GROUP_ACTIVITY_IMG);
		groupActivityService.removeGroupActivityImgs(groupMemoLocations);
	}

	@Transactional(readOnly = false)
	public void createGroupScheduleAlarm(GroupScheduleRequest.PostGroupScheduleAlarmDto groupScheduleAlarmDto,
		Long userId) {
		MoimSchedule groupSchedule = groupScheduleService.getGroupSchedule(groupScheduleAlarmDto.getMoimScheduleId());
		User user = userService.getUser(userId);
		MoimScheduleAndUser groupScheduleAndUser = groupScheduleAndUserService.getGroupScheduleAndUser(groupSchedule,
			user);

		for (Integer alarmDate : groupScheduleAlarmDto.getAlarmDates()) {
			MoimScheduleAlarm groupScheduleAlarm = GroupScheduleConverter.toGroupScheduleAlarm(groupScheduleAndUser,
				alarmDate);
			groupScheduleAndUserService.createGroupScheduleAlarm(groupScheduleAlarm);
		}
	}

	@Transactional(readOnly = false)
	public void modifyGroupScheduleAlarm(GroupScheduleRequest.PostGroupScheduleAlarmDto groupScheduleAlarmDto,
		Long userId) {
		MoimSchedule groupSchedule = groupScheduleService.getGroupSchedule(groupScheduleAlarmDto.getMoimScheduleId());
		User user = userService.getUser(userId);
		MoimScheduleAndUser groupScheduleAndUser = groupScheduleAndUserService.getGroupScheduleAndUser(groupSchedule,
			user);
		groupScheduleAndUserService.removeGroupScheduleAlarm(groupScheduleAndUser);

		for (Integer alarmDate : groupScheduleAlarmDto.getAlarmDates()) {
			MoimScheduleAlarm groupScheduleAlarm = GroupScheduleConverter.toGroupScheduleAlarm(groupScheduleAndUser,
				alarmDate);
			groupScheduleAndUserService.createGroupScheduleAlarm(groupScheduleAlarm);
		}
	}

	@Transactional(readOnly = true)
	public List<GroupScheduleResponse.GroupScheduleDto> getMonthGroupSchedules(Long groupId,
		List<LocalDateTime> localDateTimes, Long userId) {
		Moim group = groupService.getGroupWithGroupAndUsersByGroupId(groupId);
		existGroupAndUser(userId, group);
		List<MoimAndUser> groupAndUsersInGroup = groupAndUserService.getGroupAndUsers(group);
		List<User> users = GroupAndUserConverter.toUsers(groupAndUsersInGroup);

		List<Schedule> individualsSchedules = scheduleService.getSchedules(users);
		List<MoimScheduleAndUser> groupScheduleAndUsers = groupScheduleService
			.getMonthGroupSchedules(localDateTimes, users);
		return GroupScheduleResponseConverter
			.toGroupScheduleDtos(individualsSchedules, groupScheduleAndUsers, groupAndUsersInGroup);
	}

	@Transactional(readOnly = true)
	public List<GroupScheduleResponse.GroupScheduleDto> getAllGroupSchedules(Long groupId, Long userId) {
		Moim group = groupService.getGroupWithGroupAndUsersByGroupId(groupId);
		existGroupAndUser(userId, group);
		List<MoimAndUser> groupAndUsersInGroup = groupAndUserService.getGroupAndUsers(group);
		List<User> users = GroupAndUserConverter.toUsers(groupAndUsersInGroup);

		List<Schedule> individualsSchedules = scheduleService.getSchedules(users);
		List<MoimScheduleAndUser> groupScheduleAndUsers = groupScheduleService
			.getAllGroupSchedules(users);
		return GroupScheduleResponseConverter
			.toGroupScheduleDtos(individualsSchedules, groupScheduleAndUsers, groupAndUsersInGroup);
	}
}
