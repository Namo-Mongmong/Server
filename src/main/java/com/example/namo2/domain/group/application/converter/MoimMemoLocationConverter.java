package com.example.namo2.domain.group.application.converter;

import java.util.List;
import java.util.stream.Collectors;

import com.example.namo2.domain.group.domain.MoimMemo;
import com.example.namo2.domain.group.domain.MoimMemoLocation;
import com.example.namo2.domain.group.domain.MoimMemoLocationAndUser;
import com.example.namo2.domain.group.domain.MoimMemoLocationImg;
import com.example.namo2.domain.group.ui.dto.MoimMemoRequest;

import com.example.namo2.domain.user.domain.User;

public class MoimMemoLocationConverter {
	private MoimMemoLocationConverter() {
		throw new IllegalArgumentException("Util Class");
	}

	public static MoimMemoLocation toMoimMemoLocation(MoimMemo moimMemo, MoimMemoRequest.LocationDto locationDto) {
		return MoimMemoLocation.builder()
			.moimMemo(moimMemo)
			.name(locationDto.getName())
			.totalAmount(locationDto.getMoney())
			.build();
	}

	public static List<MoimMemoLocationAndUser> toMoimMemoLocationLocationAndUsers(MoimMemoLocation moimMemoLocation,
		List<User> users) {
		return users.stream()
			.map((user) -> toMoimMemoLocationLocationAndUser(moimMemoLocation, user))
			.collect(Collectors.toList());
	}

	public static MoimMemoLocationAndUser toMoimMemoLocationLocationAndUser(MoimMemoLocation moimMemoLocation,
		User user) {
		return MoimMemoLocationAndUser
			.builder()
			.moimMemoLocation(moimMemoLocation)
			.user(user)
			.build();
	}

	public static MoimMemoLocationImg toMoimMemoLocationLocationImg(MoimMemoLocation moimMemoLocation, String url) {
		return MoimMemoLocationImg
			.builder()
			.moimMemoLocation(moimMemoLocation)
			.url(url)
			.build();
	}
}
