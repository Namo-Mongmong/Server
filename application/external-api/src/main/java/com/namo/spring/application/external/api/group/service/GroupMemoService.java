package com.namo.spring.application.external.api.group.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.namo.spring.core.common.code.status.ErrorStatus;
import com.namo.spring.core.common.exception.GroupException;
import com.namo.spring.db.mysql.domains.group.domain.MoimMemo;
import com.namo.spring.db.mysql.domains.group.domain.MoimSchedule;
import com.namo.spring.db.mysql.domains.group.repository.diary.MoimMemoRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class GroupMemoService {
	private final MoimMemoRepository moimMemoRepository;

	public Optional<MoimMemo> getGroupMemoOrNull(MoimSchedule groupSchedule) {
		return moimMemoRepository.findMoimMemoByMoimSchedule(groupSchedule);
	}

	public MoimMemo getGroupMemo(MoimSchedule groupSchedule) {
		return moimMemoRepository.findMoimMemoByMoimSchedule(groupSchedule)
			.orElseThrow(() -> new GroupException(ErrorStatus.NOT_FOUND_GROUP_FAILURE));
	}

	public MoimMemo getGroupMemoWithUsers(MoimSchedule groupSchedule) {
		return moimMemoRepository.findMoimMemoAndUsersByMoimSchedule(groupSchedule);
	}

	public void removeGroupMemo(MoimMemo groupMemo) {
		moimMemoRepository.delete(groupMemo);
	}

	public MoimMemo createGroupMemo(MoimMemo groupMemo) {
		return moimMemoRepository.save(groupMemo);
	}

	public MoimMemo getGroupMemoWithLocations(Long groupScheduleId) {
		return moimMemoRepository.findMoimMemoAndLocationsByMoimSchedule(groupScheduleId)
			.orElseThrow(() -> new GroupException(ErrorStatus.NOT_FOUND_GROUP_MEMO_FAILURE));
	}
}
