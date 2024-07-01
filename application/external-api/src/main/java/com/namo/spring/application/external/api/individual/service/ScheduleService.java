package com.namo.spring.application.external.api.individual.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.namo.spring.application.external.api.individual.converter.DiaryResponseConverter;
import com.namo.spring.application.external.api.individual.converter.ScheduleResponseConverter;
import com.namo.spring.application.external.api.individual.dto.DiaryResponse;
import com.namo.spring.application.external.api.individual.dto.ScheduleResponse;
import com.namo.spring.core.common.code.status.ErrorStatus;
import com.namo.spring.core.common.exception.IndividualException;
import com.namo.spring.db.mysql.domains.individual.domain.Schedule;
import com.namo.spring.db.mysql.domains.individual.dto.MoimScheduleProjection;
import com.namo.spring.db.mysql.domains.individual.dto.ScheduleProjection;
import com.namo.spring.db.mysql.domains.individual.repository.schedule.ScheduleRepository;
import com.namo.spring.db.mysql.domains.user.domain.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ScheduleService {
	private final ScheduleRepository scheduleRepository;

	public Schedule createSchedule(Schedule schedule) {
		return scheduleRepository.save(schedule);
	}

	public Schedule getScheduleById(Long scheduleId) {
		return scheduleRepository.findById(scheduleId)
			.orElseThrow(() -> new IndividualException(ErrorStatus.NOT_FOUND_SCHEDULE_FAILURE));
	}

	public List<Schedule> getSchedulesByUser(User user) {
		return scheduleRepository.findAllByUser(user);
	}

	public List<ScheduleResponse.GetScheduleDto> getGroupSchedulesByUser(User user, LocalDateTime startDate,
		LocalDateTime endDate
	) {
		return scheduleRepository.findMoimSchedulesByUserId(user, startDate, endDate)
			.stream()
			.map(ScheduleResponseConverter::toGetScheduleRes)
			.collect(Collectors.toList());
	}

	public List<ScheduleResponse.GetScheduleDto> getSchedulesByUser(User user, LocalDateTime startDate,
		LocalDateTime endDate) {
		List<ScheduleProjection.ScheduleDto> personalSchedules = scheduleRepository.findPersonalSchedulesByUserId(user,
			startDate,
			endDate);
		List<MoimScheduleProjection.ScheduleDto> groupSchedules = scheduleRepository.findMoimSchedulesByUserId(user,
			startDate, endDate);
		return ScheduleResponseConverter.toGetScheduleDtos(personalSchedules, groupSchedules);
	}

	public List<ScheduleResponse.GetScheduleDto> getAllGroupSchedulesByUser(User user) {
		return scheduleRepository.findMoimSchedulesByUserId(user, null, null)
			.stream()
			.map(ScheduleResponseConverter::toGetScheduleRes)
			.collect(Collectors.toList());
	}

	public DiaryResponse.SliceDiaryDto getScheduleDiaryByUser(
		User user,
		LocalDateTime startDate,
		LocalDateTime endDate,
		Pageable pageable
	) {
		return DiaryResponseConverter.toSliceDiaryDto(
			(scheduleRepository.findScheduleDiaryByMonth(user, startDate, endDate, pageable)));
	}

	public List<DiaryResponse.GetDiaryByUserDto> getAllDiariesByUser(User user) {
		return scheduleRepository.findAllScheduleDiary(user)
			.stream()
			.map(DiaryResponseConverter::toGetDiaryByUserRes)
			.collect(Collectors.toList());
	}

	public void removeSchedule(Schedule schedule) {
		scheduleRepository.delete(schedule);
	}

	public void removeSchedules(List<Schedule> schedules) {
		scheduleRepository.deleteAll(schedules);
	}

	public List<Schedule> getSchedules(List<User> users) {
		return scheduleRepository.findSchedulesByUsers(users);
	}
}
