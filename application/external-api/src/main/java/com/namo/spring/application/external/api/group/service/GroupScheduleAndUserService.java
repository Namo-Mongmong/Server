package com.namo.spring.application.external.api.group.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.namo.spring.core.common.code.status.ErrorStatus;
import com.namo.spring.core.common.exception.GroupException;
import com.namo.spring.db.mysql.domains.group.domain.Moim;
import com.namo.spring.db.mysql.domains.group.domain.MoimAndUser;
import com.namo.spring.db.mysql.domains.group.domain.MoimSchedule;
import com.namo.spring.db.mysql.domains.group.domain.MoimScheduleAlarm;
import com.namo.spring.db.mysql.domains.group.domain.MoimScheduleAndUser;
import com.namo.spring.db.mysql.domains.group.repository.schedule.MoimScheduleAlarmRepository;
import com.namo.spring.db.mysql.domains.group.repository.schedule.MoimScheduleAndUserRepository;
import com.namo.spring.db.mysql.domains.user.domain.User;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class GroupScheduleAndUserService {
	private final MoimScheduleAndUserRepository moimScheduleAndUserRepository;
	private final MoimScheduleAlarmRepository moimScheduleAlarmRepository;

	public void createAll(List<MoimScheduleAndUser> groupScheduleAndUsers, Moim group) {
		validateGroupScheduleAndUserSize(groupScheduleAndUsers);
		validateGroupScheduleAndUsersInGroupAndUsers(groupScheduleAndUsers, group);
		moimScheduleAndUserRepository.saveAll(groupScheduleAndUsers);
	}

	private void validateGroupScheduleAndUserSize(List<MoimScheduleAndUser> groupScheduleAndUsers) {
		if (groupScheduleAndUsers.size() == 0) {
			throw new GroupException(ErrorStatus.EMPTY_USERS_FAILURE);
		}
	}

	private void validateGroupScheduleAndUsersInGroupAndUsers(List<MoimScheduleAndUser> groupScheduleAndUsers,
		Moim group) {
		Set<User> groupUsers = group.getMoimAndUsers()
			.stream()
			.map(MoimAndUser::getUser)
			.collect(Collectors.toSet());
		for (MoimScheduleAndUser groupScheduleAndUser : groupScheduleAndUsers) {
			if (!groupUsers.contains(groupScheduleAndUser.getUser())) {
				throw new GroupException(ErrorStatus.NOT_USERS_IN_GROUP);
			}

		}
	}

	public void removeGroupScheduleAndUser(MoimSchedule groupSchedule) {
		moimScheduleAndUserRepository.deleteMoimScheduleAndUserByMoimSchedule(groupSchedule);
	}

	public void removeGroupScheduleAndUserInPersonalSpace(MoimScheduleAndUser groupScheduleAndUser) {
		groupScheduleAndUser.handleDeletedPersonalSchedule();
	}

	public void removeGroupScheduleAndUsers(List<MoimScheduleAndUser> groupScheduleAndUsers) {
		moimScheduleAndUserRepository.deleteAll(groupScheduleAndUsers);
	}

	public MoimScheduleAndUser getGroupScheduleAndUser(MoimSchedule groupSchedule, User user) {
		return moimScheduleAndUserRepository.findMoimScheduleAndUserByMoimScheduleAndUser(groupSchedule, user)
			.orElseThrow(() -> new GroupException(ErrorStatus.NOT_FOUND_GROUP_SCHEDULE_AND_USER_FAILURE));
	}

	public List<MoimScheduleAndUser> getAllByUser(User user) {
		return moimScheduleAndUserRepository.findAllByUser(user);
	}

	public void removeGroupScheduleAlarm(MoimScheduleAndUser groupScheduleAndUser) {
		moimScheduleAlarmRepository.deleteMoimScheduleAlarmByMoimScheduleAndUser(groupScheduleAndUser);
	}

	public void removeGroupScheduleAlarm(List<MoimScheduleAndUser> groupScheduleAndUser) {
		moimScheduleAlarmRepository.deleteMoimScheduleAlarmByMoimScheduleAndUser(groupScheduleAndUser);
	}

	public void removeGroupScheduleAlarms(List<MoimScheduleAndUser> groupScheduleAndUsers) {
		groupScheduleAndUsers.forEach(groupScheduleAndUser ->
			moimScheduleAlarmRepository.deleteAll(groupScheduleAndUser.getMoimScheduleAlarms()));
	}

	public void createGroupScheduleAlarm(MoimScheduleAlarm groupScheduleAlarm) {
		moimScheduleAlarmRepository.save(groupScheduleAlarm);
	}

	/**
	 * 메서드 네이밍에 더 분명한 의미를 담기 위해서 규칙을 좀 더
	 * 자세히 세워보는 것도 괜찮을 것 같아요
	 */
	public List<MoimScheduleAndUser> getGroupScheduleAndUsersForMonthGroupDiary(User user, List<LocalDateTime> dates,
		Pageable page) {
		return moimScheduleAndUserRepository.findMoimScheduleMemoByMonthPaging(user, dates, page);
	}

	public void modifyMemo(MoimScheduleAndUser groupScheduleAndUser, String groupScheduleMemo) {
		groupScheduleAndUser.updateText(groupScheduleMemo);
	}

	public void removeGroupScheduleDiaryInPersonalSpace(MoimScheduleAndUser groupScheduleAndUser) {
		groupScheduleAndUser.handleDeletedPersonalMoimMemo();
	}

	public MoimScheduleAndUser getGroupScheduleAndUser(Long groupScheduleId, User user) {
		MoimSchedule groupSchedule = MoimSchedule.builder().id(groupScheduleId).build();
		return getGroupScheduleAndUser(groupSchedule, user);
	}
}
