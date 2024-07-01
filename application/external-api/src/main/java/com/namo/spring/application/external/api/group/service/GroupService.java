package com.namo.spring.application.external.api.group.service;

import org.springframework.stereotype.Service;

import com.namo.spring.core.common.code.status.ErrorStatus;
import com.namo.spring.core.common.exception.GroupException;
import com.namo.spring.db.mysql.domains.group.domain.Moim;
import com.namo.spring.db.mysql.domains.group.repository.group.MoimRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class GroupService {
	private final MoimRepository groupRepository;

	public Moim createGroup(Moim group) {
		return groupRepository.save(group);
	}

	public Moim getGroupWithGroupAndUsersByGroupId(Long groupId) {
		return groupRepository.findMoimWithMoimAndUsersByMoimId(groupId)
			.orElseThrow(() -> new GroupException(ErrorStatus.NOT_FOUND_GROUP_FAILURE));
	}

	public Moim getGroupHavingLockById(Long groupId) {
		Moim group = groupRepository.findHavingLockById(groupId);
		if (group == null) {
			throw new GroupException(ErrorStatus.NOT_FOUND_GROUP_FAILURE);
		}
		return group;
	}

	public Moim getGroupWithGroupAndUsersByCode(String code) {
		Moim group = groupRepository.findMoimHavingLockWithMoimAndUsersByCode(code);
		if (group == null) {
			throw new GroupException(ErrorStatus.NOT_FOUND_GROUP_FAILURE);
		}
		return group;
	}
}
