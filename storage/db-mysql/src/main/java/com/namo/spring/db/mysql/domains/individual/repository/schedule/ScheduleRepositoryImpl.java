package com.namo.spring.db.mysql.domains.individual.repository.schedule;

import static com.namo.spring.db.mysql.domains.group.domain.QMoimSchedule.*;
import static com.namo.spring.db.mysql.domains.group.domain.QMoimScheduleAndUser.*;
import static com.namo.spring.db.mysql.domains.individual.domain.QCategory.*;
import static com.namo.spring.db.mysql.domains.individual.domain.QImage.*;
import static com.namo.spring.db.mysql.domains.individual.domain.QPalette.*;
import static com.namo.spring.db.mysql.domains.individual.domain.QSchedule.*;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.EntityManager;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import com.namo.spring.db.mysql.domains.group.type.VisibleStatus;
import com.namo.spring.db.mysql.domains.individual.domain.Schedule;
import com.namo.spring.db.mysql.domains.individual.dto.MoimScheduleProjection;
import com.namo.spring.db.mysql.domains.individual.dto.ScheduleProjection;
import com.namo.spring.db.mysql.domains.user.domain.User;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ScheduleRepositoryImpl implements ScheduleRepositoryCustom {

	private final JPAQueryFactory queryFactory;
	private final EntityManager em;

	public ScheduleRepositoryImpl(EntityManager em) {
		queryFactory = new JPAQueryFactory(em);
		this.em = em;
	}

	@Override
	public List<ScheduleProjection.ScheduleDto> findPersonalSchedulesByUserId(User user, LocalDateTime startDate,
		LocalDateTime endDate) {
		return queryFactory
			.select(Projections.constructor(
				ScheduleProjection.ScheduleDto.class,
				schedule.id,
				schedule.name,
				schedule.period.startDate,
				schedule.period.endDate,
				schedule.alarms,
				schedule.period.dayInterval,
				schedule.location.x,
				schedule.location.y,
				schedule.location.locationName,
				schedule.location.kakaoLocationId,
				schedule.category.id,
				schedule.hasDiary
			)).distinct()
			.from(schedule)
			.leftJoin(schedule.alarms).fetchJoin()
			.where(schedule.user.eq(user),
				scheduleDateLoe(endDate),
				scheduleDateGoe(startDate))
			.fetch();
	}

	private BooleanExpression scheduleDateLoe(LocalDateTime endDate) {
		return endDate != null ? schedule.period.startDate.before(endDate) : null;
	}

	private BooleanExpression scheduleDateGoe(LocalDateTime startDate) {
		return startDate != null ? schedule.period.endDate.after(startDate) : null;
	}

	@Override
	public List<MoimScheduleProjection.ScheduleDto> findMoimSchedulesByUserId(
		User user,
		LocalDateTime startDate, LocalDateTime endDate
	) {
		return queryFactory
			.select(Projections.constructor(MoimScheduleProjection.ScheduleDto.class,
					moimScheduleAndUser.moimSchedule.id,
					moimScheduleAndUser.moimSchedule.name,
					moimScheduleAndUser.moimSchedule.period.startDate,
					moimScheduleAndUser.moimSchedule.period.endDate,
					moimScheduleAndUser.moimScheduleAlarms,
					moimScheduleAndUser.moimSchedule.period.dayInterval,
					moimScheduleAndUser.moimSchedule.location.x,
					moimScheduleAndUser.moimSchedule.location.y,
					moimScheduleAndUser.moimSchedule.location.locationName,
					moimScheduleAndUser.moimSchedule.location.kakaoLocationId,
					moimScheduleAndUser.category.id,
					moimScheduleAndUser.moimSchedule.moimMemo,
					moimScheduleAndUser.memo
				)
			)
			.distinct()
			.from(moimScheduleAndUser)
			.join(moimScheduleAndUser.moimSchedule, moimSchedule).fetchJoin()
			.leftJoin(moimScheduleAndUser.moimScheduleAlarms).fetchJoin()
			.leftJoin(moimSchedule.moimMemo).fetchJoin()
			.where(
				moimScheduleAndUser.user.eq(user),
				moimScheduleDateLoe(endDate),
				moimScheduleDateGoe(startDate),
				moimScheduleAndUser.visibleStatus.ne(VisibleStatus.NOT_SEEN_PERSONAL_SCHEDULE)
			).fetch();
	}

	private BooleanExpression moimScheduleDateGoe(LocalDateTime startDate) {
		return startDate != null ? moimSchedule.period.endDate.after(startDate) : null;
	}

	private BooleanExpression moimScheduleDateLoe(LocalDateTime endDate) {
		return endDate != null ? moimSchedule.period.startDate.before(endDate) : null;
	}

	@Override
	public Slice<ScheduleProjection.DiaryDto> findScheduleDiaryByMonth(User user, LocalDateTime startDate,
		LocalDateTime endDate, Pageable pageable) {
		List<ScheduleProjection.DiaryDto> content = queryFactory
			.select(Projections.constructor(ScheduleProjection.DiaryDto.class,
				schedule.id,
				schedule.name,
				schedule.period.startDate,
				schedule.contents,
				schedule.category.id,
				schedule.images,
				schedule.category.palette.id,
				schedule.location.locationName,
				schedule.images))
			.from(schedule)
			.join(schedule.category, category).fetchJoin()
			.join(category.palette, palette).fetchJoin()
			.where(schedule.user.eq(user)
				.and(schedule.period.startDate.before(endDate)
					.and(schedule.period.endDate.after(startDate))
					.and(schedule.hasDiary.isTrue())
				))
			.orderBy(schedule.period.startDate.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize() + 1)
			.fetch();
		boolean hasNext = false;
		if (content.size() > pageable.getPageSize()) {
			content.remove(pageable.getPageSize());
			hasNext = true;
		}
		return new SliceImpl<>(content, pageable, hasNext);
	}

	@Override
	public List<ScheduleProjection.DiaryByUserDto> findAllScheduleDiary(User user) {
		return queryFactory
			.select(Projections.constructor(ScheduleProjection.DiaryByUserDto.class,
				schedule.id,
				schedule.contents,
				schedule.images
			)).distinct()
			.from(schedule)
			.leftJoin(schedule.images, image).fetchJoin()
			.where(schedule.user.eq(user),
				schedule.hasDiary.isTrue()
			)
			.fetch();
	}

	@Override
	public Schedule findOneScheduleAndImages(Long scheduleId) {
		return queryFactory
			.select(schedule)
			.distinct()
			.from(schedule)
			.leftJoin(schedule.images, image).fetchJoin()
			.where(schedule.id.eq(scheduleId))
			.fetchOne();
	}
}
