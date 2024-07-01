package com.namo.spring.application.external.api.group.converter;

import java.util.List;
import java.util.stream.Collectors;

import com.namo.spring.db.mysql.domains.group.domain.Moim;
import com.namo.spring.db.mysql.domains.group.domain.MoimAndUser;
import com.namo.spring.db.mysql.domains.user.domain.User;

public class GroupAndUserConverter {
	private GroupAndUserConverter() {
		throw new IllegalArgumentException("Util Class");
	}

	public static MoimAndUser toGroupAndUser(String groupCustomName, Integer color, User user, Moim group) {
		return MoimAndUser.builder()
			.moimCustomName(groupCustomName)
			.color(color)
			.user(user)
			.moim(group)
			.build();
	}

	public static List<User> toUsers(List<MoimAndUser> groupAndUsers) {
		return groupAndUsers.stream()
			.map(MoimAndUser::getUser)
			.collect(Collectors.toList());
	}
}
