package com.namo.spring.application.external.api.group.converter;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.namo.spring.application.external.api.group.dto.GroupResponse;
import com.namo.spring.db.mysql.domains.group.domain.Moim;
import com.namo.spring.db.mysql.domains.group.domain.MoimAndUser;

public class GroupResponseConverter {
	private GroupResponseConverter() {
		throw new IllegalStateException("Util Class");
	}

	public static GroupResponse.GroupIdDto toGroupIdDto(Moim group) {
		return GroupResponse.GroupIdDto.builder()
			.groupId(group.getId())
			.build();
	}

	public static List<GroupResponse.GroupDto> toGroupDtos(List<MoimAndUser> groupAndUsers,
		List<MoimAndUser> curUserGroupsInUser) {
		Map<Moim, List<MoimAndUser>> groupMappingGroupAndUsers = groupAndUsers.stream()
			.collect(
				Collectors.groupingBy(
					MoimAndUser::getMoim
				)
			);
		return curUserGroupsInUser.stream()
			.map((groupAndUser) -> toGroupDto(groupAndUser, groupMappingGroupAndUsers.get(groupAndUser.getMoim())))
			.collect(Collectors.toList());
	}

	public static GroupResponse.GroupDto toGroupDto(MoimAndUser groupAndUser, List<MoimAndUser> groupAndUsers) {
		return GroupResponse.GroupDto.builder()
			.groupId(groupAndUser.getMoim().getId())
			.groupName(groupAndUser.getMoimCustomName())
			.groupImgUrl(groupAndUser.getMoim().getImgUrl())
			.groupCode(groupAndUser.getMoim().getCode())
			.groupUsers(toGroupUserDtos(groupAndUsers))
			.build();
	}

	private static List<GroupResponse.GroupUserDto> toGroupUserDtos(List<MoimAndUser> groupAndUsers) {
		return groupAndUsers.stream()
			.map(GroupResponseConverter::toGroupUserDto)
			.collect(Collectors.toList());
	}

	private static GroupResponse.GroupUserDto toGroupUserDto(MoimAndUser groupAndUser) {
		return GroupResponse.GroupUserDto
			.builder()
			.userId(groupAndUser.getUser().getId())
			.userName(groupAndUser.getUser().getName())
			.color(groupAndUser.getColor())
			.build();
	}

	public static GroupResponse.GroupParticipantDto toGroupParticipantDto(Moim group) {
		return GroupResponse.GroupParticipantDto.builder()
			.groupId(group.getId())
			.code(group.getCode())
			.build();
	}
}
