package com.namo.spring.db.mysql.domains.individual.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.namo.spring.db.mysql.domains.group.domain.MoimMemo;
import com.namo.spring.db.mysql.domains.group.domain.MoimScheduleAlarm;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class MoimScheduleProjection {
	@Getter
	@NoArgsConstructor
	@Builder
	@AllArgsConstructor
	public static class ScheduleDto {
		private Long scheduleId;
		private String name;
		private LocalDateTime startDate;
		private LocalDateTime endDate;
		private List<MoimScheduleAlarm> alarms;
		private Integer interval;
		private Double x;
		private Double y;
		private String locationName;
		private String kakaoLocationId;
		private Long categoryId;
		private MoimMemo moimMemo;
		private String userMemo;
	}
}
