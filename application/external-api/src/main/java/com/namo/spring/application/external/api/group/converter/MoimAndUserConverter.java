package com.namo.spring.application.external.api.group.converter;

import java.util.List;
import java.util.stream.Collectors;

import com.namo.spring.db.mysql.domains.group.domain.Moim;
import com.namo.spring.db.mysql.domains.group.domain.MoimAndUser;
import com.namo.spring.db.mysql.domains.user.domain.User;

public class MoimAndUserConverter {
	private MoimAndUserConverter() {
		throw new IllegalArgumentException("Util Class");
	}

	public static MoimAndUser toMoimAndUser(String moimCustomName, Integer color, User user, Moim moim) {
		return MoimAndUser.builder()
			.moimCustomName(moimCustomName)
			.color(color)
			.user(user)
			.moim(moim)
			.build();
	}

	public static List<User> toUsers(List<MoimAndUser> moimAndUsers) {
		return moimAndUsers.stream()
			.map(MoimAndUser::getUser)
			.collect(Collectors.toList());
	}
}
