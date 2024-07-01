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

public class GroupScheduleConverter {
	private GroupScheduleConverter() {
		throw new IllegalStateException("Util Classes");
	}

	public static Period toPeriod(GroupScheduleRequest.PostGroupScheduleDto groupScheduleDto) {
		return Period.builder()
			.startDate(groupScheduleDto.getStartDate())
			.endDate(groupScheduleDto.getEndDate())
			.dayInterval(groupScheduleDto.getInterval())
			.build();
	}

	public static Period toPeriod(GroupScheduleRequest.PatchGroupScheduleDto groupScheduleDto) {
		return Period.builder()
			.startDate(groupScheduleDto.getStartDate())
			.endDate(groupScheduleDto.getEndDate())
			.dayInterval(groupScheduleDto.getInterval())
			.build();
	}

	public static Location toLocation(GroupScheduleRequest.PostGroupScheduleDto groupScheduleDto) {
		return Location.builder()
			.x(groupScheduleDto.getX())
			.y(groupScheduleDto.getY())
			.locationName(groupScheduleDto.getLocationName())
			.kakaoLocationId(groupScheduleDto.getKakaoLocationId())
			.build();
	}

	public static Location toLocation(GroupScheduleRequest.PatchGroupScheduleDto groupScheduleDto) {
		return Location.builder()
			.x(groupScheduleDto.getX())
			.y(groupScheduleDto.getY())
			.locationName(groupScheduleDto.getLocationName())
			.build();
	}

	public static MoimSchedule toGroupSchedule(Moim group, Period period, Location location,
		GroupScheduleRequest.PostGroupScheduleDto groupScheduleDto) {
		return MoimSchedule.builder()
			.name(groupScheduleDto.getName())
			.period(period)
			.location(location)
			.moim(group)
			.build();
	}

	public static List<MoimScheduleAndUser> toGroupScheduleAndUsers(
		List<Category> categories,
		MoimSchedule groupSchedule,
		List<User> users
	) {
		Map<User, Category> categoryMap = categories
			.stream().collect(Collectors.toMap(Category::getUser, category -> category));

		return users.stream()
			.map((user) -> toGroupScheduleAndUser(user, groupSchedule, categoryMap.get(user)))
			.collect(Collectors.toList());
	}

	public static MoimScheduleAndUser toGroupScheduleAndUser(User user, MoimSchedule groupSchedule, Category category) {
		return MoimScheduleAndUser.builder()
			.user(user)
			.moimSchedule(groupSchedule)
			.category(category)
			.build();
	}

	public static MoimScheduleAlarm toGroupScheduleAlarm(MoimScheduleAndUser groupScheduleAndUser, Integer alarmDate) {
		return MoimScheduleAlarm.builder()
			.alarmDate(alarmDate)
			.moimScheduleAndUser(groupScheduleAndUser)
			.build();
	}
}
