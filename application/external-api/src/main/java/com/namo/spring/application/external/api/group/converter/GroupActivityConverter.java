package com.namo.spring.application.external.api.group.converter;

import java.util.List;
import java.util.stream.Collectors;

import com.namo.spring.application.external.api.group.dto.GroupDiaryRequest;
import com.namo.spring.db.mysql.domains.group.domain.MoimMemo;
import com.namo.spring.db.mysql.domains.group.domain.MoimMemoLocation;
import com.namo.spring.db.mysql.domains.group.domain.MoimMemoLocationAndUser;
import com.namo.spring.db.mysql.domains.group.domain.MoimMemoLocationImg;
import com.namo.spring.db.mysql.domains.user.domain.User;

public class GroupActivityConverter {
	private GroupActivityConverter() {
		throw new IllegalArgumentException("Util Class");
	}

	public static MoimMemoLocation toGroupActivity(MoimMemo groupMemo, GroupDiaryRequest.LocationDto locationDto) {
		return MoimMemoLocation.builder()
			.moimMemo(groupMemo)
			.name(locationDto.getName())
			.totalAmount(locationDto.getMoney())
			.build();
	}

	public static List<MoimMemoLocationAndUser> toGroupActivityAndUsers(MoimMemoLocation groupActivity,
		List<User> users) {
		return users.stream()
			.map((user) -> toGroupActivityAndUser(groupActivity, user))
			.collect(Collectors.toList());
	}

	public static MoimMemoLocationAndUser toGroupActivityAndUser(MoimMemoLocation groupActivity,
		User user) {
		return MoimMemoLocationAndUser
			.builder()
			.moimMemoLocation(groupActivity)
			.user(user)
			.build();
	}

	public static MoimMemoLocationImg toGroupActivityImg(MoimMemoLocation groupActivity, String url) {
		return MoimMemoLocationImg
			.builder()
			.moimMemoLocation(groupActivity)
			.url(url)
			.build();
	}
}
