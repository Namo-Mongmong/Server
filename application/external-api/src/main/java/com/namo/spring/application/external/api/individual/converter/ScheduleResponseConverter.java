package com.namo.spring.application.external.api.individual.converter;

import java.util.List;
import java.util.stream.Collectors;

import com.namo.spring.application.external.api.individual.dto.ScheduleResponse;
import com.namo.spring.core.common.utils.DateUtil;
import com.namo.spring.db.mysql.domains.group.domain.MoimMemo;
import com.namo.spring.db.mysql.domains.group.domain.MoimMemoLocationImg;
import com.namo.spring.db.mysql.domains.group.domain.MoimScheduleAlarm;
import com.namo.spring.db.mysql.domains.group.domain.MoimScheduleAndUser;
import com.namo.spring.db.mysql.domains.individual.domain.Alarm;
import com.namo.spring.db.mysql.domains.individual.domain.Image;
import com.namo.spring.db.mysql.domains.individual.domain.Schedule;
import com.namo.spring.db.mysql.domains.individual.dto.MoimScheduleProjection;
import com.namo.spring.db.mysql.domains.individual.dto.ScheduleProjection;

public class ScheduleResponseConverter {
	public static ScheduleResponse.ScheduleIdDto toScheduleIdRes(Schedule schedule) {
		return ScheduleResponse.ScheduleIdDto.builder()
			.scheduleId(schedule.getId())
			.build();
	}

	public static ScheduleResponse.GetScheduleDto toGetScheduleRes(ScheduleProjection.ScheduleDto scheduleDto) {
		List<Integer> alarmDates = scheduleDto.getAlarms().stream().map(Alarm::getAlarmDate).toList();

		return ScheduleResponse.GetScheduleDto.builder()
			.scheduleId(scheduleDto.getScheduleId())
			.name(scheduleDto.getName())
			.startDate(DateUtil.toSeconds(scheduleDto.getStartDate()))
			.endDate(DateUtil.toSeconds(scheduleDto.getEndDate()))
			.alarmDate(alarmDates)
			.interval(scheduleDto.getInterval())
			.x(scheduleDto.getX())
			.y(scheduleDto.getY())
			.locationName(scheduleDto.getLocationName())
			.kakaoLocationId(scheduleDto.getKakaoLocationId())
			.categoryId(scheduleDto.getCategoryId())
			.hasDiary(scheduleDto.getHasDiary())
			.isMoimSchedule(false)
			.build();
	}

	public static ScheduleResponse.GetScheduleDto toGetScheduleRes(MoimScheduleProjection.ScheduleDto scheduleDto) {
		List<Integer> alarmDates = scheduleDto.getAlarms().stream()
			.map(MoimScheduleAlarm::getAlarmDate).toList();

		return ScheduleResponse.GetScheduleDto.builder()
			.scheduleId(scheduleDto.getScheduleId())
			.name(scheduleDto.getName())
			.startDate(DateUtil.toSeconds(scheduleDto.getStartDate()))
			.endDate(DateUtil.toSeconds(scheduleDto.getEndDate()))
			.alarmDate(alarmDates)
			.interval(scheduleDto.getInterval())
			.x(scheduleDto.getX())
			.y(scheduleDto.getY())
			.locationName(scheduleDto.getLocationName())
			.kakaoLocationId(scheduleDto.getKakaoLocationId())
			.categoryId(scheduleDto.getCategoryId())
			.hasDiary(decideHasDiary(scheduleDto.getMoimMemo(), scheduleDto.getUserMemo()))
			.isMoimSchedule(true)
			.build();
	}

	private static Boolean decideHasDiary(MoimMemo groupMemo, String userMemo) {
		if (groupMemo != null && userMemo != null) {
			return Boolean.TRUE;
		}
		if (groupMemo != null && userMemo == null) {
			return Boolean.FALSE;
		}
		return null;
	}

	public static ScheduleResponse.DiaryDto toDiaryDto(ScheduleProjection.DiaryDto diaryDto) {
		return ScheduleResponse.DiaryDto.builder()
			.scheduleId(diaryDto.getScheduleId())
			.name(diaryDto.getName())
			.startDate(DateUtil.toSeconds(diaryDto.getStartDate()))
			.contents(diaryDto.getContents())
			.categoryId(diaryDto.getCategoryId())
			.color(diaryDto.getColor())
			.placeName(diaryDto.getPlaceName())
			.urls(diaryDto.getImages().stream()
				.map(Image::getImgUrl)
				.collect(Collectors.toList()))
			.build();
	}

	public static ScheduleResponse.DiaryDto toDiaryDto(MoimScheduleAndUser groupScheduleAndUser) {
		return ScheduleResponse.DiaryDto.builder()
			.scheduleId(groupScheduleAndUser.getMoimSchedule().getId())
			.name(groupScheduleAndUser.getMoimSchedule().getName())
			.startDate(DateUtil.toSeconds((groupScheduleAndUser.getMoimSchedule().getPeriod().getStartDate())))
			.contents(groupScheduleAndUser.getMemo())
			.categoryId(groupScheduleAndUser.getCategory().getId())
			.color(groupScheduleAndUser.getCategory().getPalette().getId())
			.placeName(groupScheduleAndUser.getMoimSchedule().getLocation().getLocationName())
			.urls(groupScheduleAndUser.getMoimSchedule().getMoimMemo()
				.getMoimMemoLocations()
				.stream()
				.flatMap(location -> location.getMoimMemoLocationImgs().stream())
				.map(MoimMemoLocationImg::getUrl)
				.limit(3)
				.collect(Collectors.toList()))
			.build();
	}

	public static List<ScheduleResponse.GetScheduleDto> toGetScheduleDtos(
		List<ScheduleProjection.ScheduleDto> personalSchedules,
		List<MoimScheduleProjection.ScheduleDto> groupSchedules) {
		List<ScheduleResponse.GetScheduleDto> results = personalSchedules.stream()
			.map(ScheduleResponseConverter::toGetScheduleRes)
			.collect(Collectors.toList());
		if (groupSchedules != null) {
			results.addAll(groupSchedules.stream().map(ScheduleResponseConverter::toGetScheduleRes)
				.collect(Collectors.toList()));
		}
		return results;
	}

}
