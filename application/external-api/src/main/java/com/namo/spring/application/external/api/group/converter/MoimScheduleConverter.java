package com.namo.spring.application.external.api.group.converter;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.namo.spring.application.external.api.group.dto.GroupScheduleRequest;
import com.namo.spring.db.mysql.domains.group.domain.Moim;
import com.namo.spring.db.mysql.domains.group.domain.MoimSchedule;
import com.namo.spring.db.mysql.domains.group.domain.MoimScheduleAlarm;
import com.namo.spring.db.mysql.domains.group.domain.MoimScheduleAndUser;
import com.namo.spring.db.mysql.domains.individual.domain.Category;
import com.namo.spring.db.mysql.domains.individual.type.Location;
import com.namo.spring.db.mysql.domains.individual.type.Period;
import com.namo.spring.db.mysql.domains.user.domain.User;

public class MoimScheduleConverter {
	private MoimScheduleConverter() {
		throw new IllegalStateException("Util Classes");
	}

	public static Period toPeriod(GroupScheduleRequest.PostGroupScheduleDto moimScheduleDto) {
		return Period.builder()
			.startDate(moimScheduleDto.getStartDate())
			.endDate(moimScheduleDto.getEndDate())
			.dayInterval(moimScheduleDto.getInterval())
			.build();
	}

	public static Period toPeriod(GroupScheduleRequest.PatchGroupScheduleDto moimScheduleDto) {
		return Period.builder()
			.startDate(moimScheduleDto.getStartDate())
			.endDate(moimScheduleDto.getEndDate())
			.dayInterval(moimScheduleDto.getInterval())
			.build();
	}

	public static Location toLocation(GroupScheduleRequest.PostGroupScheduleDto moimScheduleDto) {
		return Location.builder()
			.x(moimScheduleDto.getX())
			.y(moimScheduleDto.getY())
			.locationName(moimScheduleDto.getLocationName())
			.kakaoLocationId(moimScheduleDto.getKakaoLocationId())
			.build();
	}

	public static Location toLocation(GroupScheduleRequest.PatchGroupScheduleDto moimScheduleDto) {
		return Location.builder()
			.x(moimScheduleDto.getX())
			.y(moimScheduleDto.getY())
			.locationName(moimScheduleDto.getLocationName())
			.build();
	}

	public static MoimSchedule toMoimSchedule(Moim moim, Period period, Location location,
		GroupScheduleRequest.PostGroupScheduleDto moimScheduleDto) {
		return MoimSchedule.builder()
			.name(moimScheduleDto.getName())
			.period(period)
			.location(location)
			.moim(moim)
			.build();
	}

	public static List<MoimScheduleAndUser> toMoimScheduleAndUsers(
		List<Category> categories,
		MoimSchedule moimSchedule,
		List<User> users
	) {
		Map<User, Category> categoryMap = categories
			.stream().collect(Collectors.toMap(Category::getUser, category -> category));

		return users.stream()
			.map((user) -> toMoimScheduleAndUser(user, moimSchedule, categoryMap.get(user)))
			.collect(Collectors.toList());
	}

	public static MoimScheduleAndUser toMoimScheduleAndUser(User user, MoimSchedule moimSchedule, Category category) {
		return MoimScheduleAndUser.builder()
			.user(user)
			.moimSchedule(moimSchedule)
			.category(category)
			.build();
	}

	public static MoimScheduleAlarm toMoimScheduleAlarm(MoimScheduleAndUser moimScheduleAndUser, Integer alarmDate) {
		return MoimScheduleAlarm.builder()
			.alarmDate(alarmDate)
			.moimScheduleAndUser(moimScheduleAndUser)
			.build();
	}
}
