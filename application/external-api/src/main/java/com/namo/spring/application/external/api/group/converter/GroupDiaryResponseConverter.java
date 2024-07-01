package com.namo.spring.application.external.api.group.converter;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.SliceImpl;

import com.namo.spring.application.external.api.group.dto.GroupDiaryResponse;
import com.namo.spring.core.common.utils.DateUtil;
import com.namo.spring.db.mysql.domains.group.domain.MoimMemo;
import com.namo.spring.db.mysql.domains.group.domain.MoimMemoLocation;
import com.namo.spring.db.mysql.domains.group.domain.MoimMemoLocationAndUser;
import com.namo.spring.db.mysql.domains.group.domain.MoimMemoLocationImg;
import com.namo.spring.db.mysql.domains.group.domain.MoimScheduleAndUser;
import com.namo.spring.db.mysql.domains.individual.domain.Image;
import com.namo.spring.db.mysql.domains.individual.domain.Schedule;

public class GroupDiaryResponseConverter {
	private GroupDiaryResponseConverter() {
		throw new IllegalStateException("Utill Classes");
	}

	public static GroupDiaryResponse.GroupDiaryDto toGroupDiaryDto(
		MoimMemo groupMemo,
		List<MoimMemoLocation> groupActivities,
		List<MoimMemoLocationAndUser> groupActivityAndUsers) {
		List<GroupDiaryResponse.GroupUserDto> users = groupMemo.getMoimSchedule().getMoimScheduleAndUsers().stream()
			.map(GroupDiaryResponseConverter::toGroupUserDto)
			.toList();
		return GroupDiaryResponse.GroupDiaryDto.fromMoimMemo(groupMemo,
			toGroupActivityDtos(groupActivities, groupActivityAndUsers));
	}

	public static GroupDiaryResponse.GroupUserDto toGroupUserDto(MoimScheduleAndUser groupScheduleAndUser) {
		return GroupDiaryResponse.GroupUserDto
			.builder()
			.userId(groupScheduleAndUser.getUser().getId())
			.userName(groupScheduleAndUser.getUser().getName())
			.build();
	}

	private static List<GroupDiaryResponse.MoimActivityDto> toGroupActivityDtos(
		List<MoimMemoLocation> groupActivities,
		List<MoimMemoLocationAndUser> groupActivityAndUsers) {
		Map<MoimMemoLocation, List<MoimMemoLocationAndUser>> groupActivityMappingUsers = groupActivityAndUsers
			.stream()
			.collect(Collectors.groupingBy(MoimMemoLocationAndUser::getMoimMemoLocation));

		return groupActivities.stream()
			.map(groupActivity -> toGroupActivityDto(groupActivityMappingUsers, groupActivity))
			.collect(Collectors.toList());
	}

	private static GroupDiaryResponse.MoimActivityDto toGroupActivityDto(
		Map<MoimMemoLocation, List<MoimMemoLocationAndUser>> groupActivityMappingUsers,
		MoimMemoLocation groupActivity) {
		List<String> urls = groupActivity.getMoimMemoLocationImgs().stream()
			.map(MoimMemoLocationImg::getUrl)
			.toList();
		List<Long> participants = groupActivityMappingUsers.get(groupActivity).stream()
			.map(groupActivityAndUser -> groupActivityAndUser.getUser().getId())
			.toList();
		return GroupDiaryResponse.MoimActivityDto
			.builder()
			.moimActivityId(groupActivity.getId())
			.name(groupActivity.getName())
			.money(groupActivity.getTotalAmount())
			.urls(urls)
			.participants(participants)
			.build();
	}

	public static GroupDiaryResponse.SliceDiaryDto toSliceDiaryDto(
		List<MoimScheduleAndUser> groupScheduleAndUsers,
		Pageable page
	) {
		boolean hasNext = false;
		if (groupScheduleAndUsers.size() > page.getPageSize()) {
			groupScheduleAndUsers.remove(page.getPageSize());
			hasNext = true;
		}
		SliceImpl<MoimScheduleAndUser> groupSchedulesSlice = new SliceImpl<>(groupScheduleAndUsers, page, hasNext);
		return GroupDiaryResponse.SliceDiaryDto.builder()
			.content(
				groupSchedulesSlice.stream().map(GroupDiaryResponseConverter::toDiaryDto).collect(Collectors.toList()))
			.currentPage(groupSchedulesSlice.getNumber())
			.size(groupSchedulesSlice.getSize())
			.first(groupSchedulesSlice.isFirst())
			.last(groupSchedulesSlice.isLast())
			.build();
	}

	public static GroupDiaryResponse.DiaryDto toDiaryDto(Schedule schedule) {
		List<String> urls = schedule.getImages().stream()
			.map(Image::getImgUrl)
			.toList();
		return GroupDiaryResponse.DiaryDto.builder()
			.scheduleId(schedule.getId())
			.name(schedule.getName())
			.startDate(DateUtil.toSeconds(schedule.getPeriod().getStartDate()))
			.contents(schedule.getContents())
			.urls(urls)
			.categoryId(schedule.getCategory().getId())
			.color(schedule.getCategory().getPalette().getId())
			.placeName(schedule.getLocation().getLocationName())
			.build();
	}

	public static GroupDiaryResponse.DiaryDto toDiaryDto(MoimScheduleAndUser groupScheduleAndUser) {
		List<String> urls = groupScheduleAndUser.getMoimSchedule().getMoimMemo()
			.getMoimMemoLocations()
			.stream()
			.flatMap(location -> location
				.getMoimMemoLocationImgs()
				.stream())
			.map(MoimMemoLocationImg::getUrl)
			.limit(3)
			.collect(Collectors.toList());
		return GroupDiaryResponse.DiaryDto.builder()
			.scheduleId(groupScheduleAndUser.getMoimSchedule().getId())
			.name(groupScheduleAndUser.getMoimSchedule().getName())
			.startDate(DateUtil.toSeconds(groupScheduleAndUser.getMoimSchedule().getPeriod().getStartDate()))
			.contents(groupScheduleAndUser.getMemo())
			.urls(urls)
			.categoryId(groupScheduleAndUser.getCategory().getId())
			.color(groupScheduleAndUser.getCategory().getPalette().getId())
			.placeName(groupScheduleAndUser.getMoimSchedule().getLocation().getLocationName())
			.build();
	}
}
