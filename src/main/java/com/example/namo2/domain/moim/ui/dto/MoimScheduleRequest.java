package com.example.namo2.domain.moim.ui.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class MoimScheduleRequest {
	private MoimScheduleRequest() {
		throw new IllegalStateException("Util class");
	}

	@NoArgsConstructor
	@AllArgsConstructor
	@Getter
	public static class PostMoimScheduleDto {
		@NotNull
		private Long moimId;
		@NotBlank
		private String name;

		@NotNull
		private Long startDate;
		@NotNull
		private Long endDate;
		@NotNull
		private Integer interval;

		@SuppressWarnings("checkstyle:MemberName")
		private Double x;
		@SuppressWarnings("checkstyle:MemberName")
		private Double y;
		private String locationName;
		private String kakaoLocationId;

		@NotNull
		private List<Long> users;
	}

	@NoArgsConstructor
	@AllArgsConstructor
	@Getter
	public static class PatchMoimScheduleDto {
		@NotNull
		private Long moimScheduleId;
		@NotBlank
		private String name;

		@NotNull
		private Long startDate;
		@NotNull
		private Long endDate;
		@NotNull
		private Integer interval;

		private Double x;
		private Double y;
		private String locationName;
		private String kakaoLocationId;

		@NotNull
		private List<Long> users;
	}

	@NoArgsConstructor
	@AllArgsConstructor
	@Getter
	public static class PatchMoimScheduleCategoryDto {
		@NotNull
		private Long moimScheduleId;

		@NotNull
		private Long categoryId;
	}

	@NoArgsConstructor
	@Getter
	public static class PostMoimScheduleAlarmDto {
		private Long moimScheduleId;
		private List<Integer> alarmDates;
	}

	@Getter
	@AllArgsConstructor
	@NoArgsConstructor
	public static class PostMoimScheduleTextDto {
		private String text;
	}
}
