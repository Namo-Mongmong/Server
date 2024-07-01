package com.namo.spring.application.external.api.group.dto;

import java.util.ArrayList;
import java.util.List;

import com.namo.spring.core.common.utils.DateUtil;
import com.namo.spring.db.mysql.domains.group.domain.MoimSchedule;
import com.namo.spring.db.mysql.domains.individual.domain.Schedule;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

public class GroupScheduleResponse {
	private GroupScheduleResponse() {
		throw new IllegalStateException("Util Class");
	}

	@AllArgsConstructor
	@Getter
	@Builder
	public static class GroupScheduleDto {
		private String name;
		private Long startDate;
		private Long endDate;
		private Integer interval;
		private List<MoimScheduleUserDto> users = new ArrayList<>();
		private Long groupId;
		private Long moimScheduleId;
		private boolean isCurMoimSchedule = false;
		private Double x;
		private Double y;
		private String locationName;
		private String kakaoLocationId;
		private boolean hasDiaryPlace;

		public static GroupScheduleDto fromSchedule(Schedule schedule, List<MoimScheduleUserDto> moimScheduleUserDtos) {
			return GroupScheduleResponse.GroupScheduleDto.builder()
				.name(schedule.getName())
				.startDate(DateUtil.toSeconds(schedule.getPeriod().getStartDate()))
				.endDate(DateUtil.toSeconds(schedule.getPeriod().getEndDate()))
				.interval(schedule.getPeriod().getDayInterval())
				.users(moimScheduleUserDtos)
				.hasDiaryPlace(false)
				.build();
		}

		public static GroupScheduleDto fromMoimSchedule(MoimSchedule moimSchedule,
			List<MoimScheduleUserDto> moimScheduleUserDtos, boolean isCurMoimSchedule) {
			return GroupScheduleDto.builder()
				.name(moimSchedule.getName())
				.startDate(DateUtil.toSeconds(moimSchedule.getPeriod().getStartDate()))
				.endDate(DateUtil.toSeconds(moimSchedule.getPeriod().getEndDate()))
				.interval(moimSchedule.getPeriod().getDayInterval())
				.groupId(moimSchedule.getMoim().getId())
				.moimScheduleId(moimSchedule.getId())
				.x(moimSchedule.getLocation().getX())
				.y(moimSchedule.getLocation().getY())
				.locationName(moimSchedule.getLocation().getLocationName())
				.kakaoLocationId(moimSchedule.getLocation().getKakaoLocationId())
				.users(moimScheduleUserDtos)
				.isCurMoimSchedule(isCurMoimSchedule)
				.hasDiaryPlace(moimSchedule.getMoimMemo() != null)
				.build();
		}
	}

	@AllArgsConstructor
	@Getter
	@Builder
	public static class MoimScheduleUserDto {
		private Long userId;
		private String userName;
		private Integer color;
	}

}
