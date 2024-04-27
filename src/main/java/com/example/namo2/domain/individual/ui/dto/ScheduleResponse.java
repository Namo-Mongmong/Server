package com.example.namo2.domain.individual.ui.dto;

import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Slice;

import com.example.namo2.domain.memo.domain.MoimMemoLocationImg;

import com.example.namo2.domain.moim.domain.MoimScheduleAndUser;

import com.example.namo2.domain.individual.domain.Image;
import com.example.namo2.domain.individual.domain.Schedule;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ScheduleResponse {
	private ScheduleResponse() {
		throw new IllegalStateException("Utility class");
	}

	@Getter
	@AllArgsConstructor
	@Builder
	public static class ScheduleIdDto {
		private Long scheduleId;
	}

	@Getter
	@NoArgsConstructor
	@Builder
	@AllArgsConstructor
	public static class GetScheduleDto {
		private Long scheduleId;
		private String name;
		private Long startDate;
		private Long endDate;
		private List<Integer> alarmDate;
		private Integer interval;
		private Double x;
		private Double y;
		private String locationName;
		private String kakaoLocationId;
		private Long categoryId;
		private boolean hasDiary;
		private boolean isMoimSchedule;
	}

	@AllArgsConstructor
	@Getter
	@Builder
	public static class GetDiaryByUserDto {
		private Long scheduleId;
		private String contents;
		private List<String> urls;
	}

	@AllArgsConstructor
	@Getter
	@Builder
	public static class GetDiaryByScheduleDto {
		private String contents;
		private List<String> urls;
	}

	@Getter
	@Builder
	@AllArgsConstructor
	public static class SliceDiaryDto {
		private List<DiaryDto> content;
		private int currentPage;
		private int size;
		private boolean first;
		private boolean last;

		public SliceDiaryDto(Slice<DiaryDto> slice) {
			this.content = slice.getContent();
			this.currentPage = slice.getNumber();
			this.size = content.size();
			this.first = slice.isFirst();
			this.last = slice.isLast();
		}
	}

	@Getter
	@Builder
	@AllArgsConstructor
	public static class DiaryDto {
		private Long scheduleId;
		private String name;
		private Long startDate;
		private String contents;
		private List<String> urls;
		private Long categoryId;
		private Long color;
		private String placeName;

		public DiaryDto(Schedule schedule) {
			this.scheduleId = schedule.getId();
			this.name = schedule.getName();
			this.startDate = schedule.getPeriod().getStartDate().atZone(ZoneId.systemDefault())
				.toInstant()
				.getEpochSecond();
			this.contents = schedule.getContents();
			this.categoryId = schedule.getCategory().getId();
			this.color = schedule.getCategory().getPalette().getId();
			this.placeName = schedule.getLocation().getLocationName();
			this.urls = schedule.getImages().stream()
				.map(Image::getImgUrl)
				.collect(Collectors.toList());
		}

		public DiaryDto(MoimScheduleAndUser moimScheduleAndUser) {
			this.scheduleId = moimScheduleAndUser.getMoimSchedule().getId();
			this.name = moimScheduleAndUser.getMoimSchedule().getName();
			this.startDate = moimScheduleAndUser.getMoimSchedule()
				.getPeriod()
				.getStartDate()
				.atZone(ZoneId.systemDefault())
				.toInstant()
				.getEpochSecond();
			this.contents = moimScheduleAndUser.getMemo();
			this.categoryId = moimScheduleAndUser.getCategory().getId();
			this.color = moimScheduleAndUser.getCategory().getPalette().getId();
			this.placeName = moimScheduleAndUser.getMoimSchedule().getLocation().getLocationName();
			this.urls = moimScheduleAndUser.getMoimSchedule().getMoimMemo()
				.getMoimMemoLocations()
				.stream()
				.flatMap(location -> location.getMoimMemoLocationImgs().stream())
				.map(MoimMemoLocationImg::getUrl)
				.limit(3)
				.collect(Collectors.toList());
		}
	}
}
