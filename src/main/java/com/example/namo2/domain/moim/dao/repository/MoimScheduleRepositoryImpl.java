package com.example.namo2.domain.moim.dao.repository;

import com.example.namo2.domain.moim.dao.repository.MoimScheduleRepositoryCustom;
import com.example.namo2.domain.moim.domain.MoimScheduleAndUser;
import com.example.namo2.domain.schedule.dto.DiaryDto;
import com.example.namo2.domain.schedule.dto.SliceDiaryDto;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.time.LocalDateTime;
import java.util.List;

import static com.example.namo2.domain.category.domain.QCategory.category;
import static com.example.namo2.domain.moim.domain.QMoimSchedule.moimSchedule;
import static com.example.namo2.domain.moim.domain.QMoimScheduleAndUser.moimScheduleAndUser;

public class MoimScheduleRepositoryImpl implements MoimScheduleRepositoryCustom {
	private final JPAQueryFactory queryFactory;

	public MoimScheduleRepositoryImpl(EntityManager em) {
		this.queryFactory = new JPAQueryFactory(em);
	}

	@Override
	public SliceDiaryDto<DiaryDto> findMoimScheduleMemoByMonth(Long userId, List<LocalDateTime> dates,
		Pageable pageable) {
		List<MoimScheduleAndUser> content = queryFactory.select(moimScheduleAndUser)
			.from(moimScheduleAndUser)
			.join(moimScheduleAndUser.moimSchedule, moimSchedule).fetchJoin()
			.join(moimSchedule.moimMemo).fetchJoin()
			.join(moimScheduleAndUser.category, category).fetchJoin()
			.join(category.palette).fetchJoin()
			.where(moimSchedule.period.startDate.before(dates.get(1)),
				moimSchedule.period.endDate.after(dates.get(0)),
				moimScheduleAndUser.user.id.eq(userId)
			)
			.orderBy(moimSchedule.period.startDate.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize() + 1)
			.fetch();
		boolean hasNext = false;
		if (content.size() > pageable.getPageSize()) {
			content.remove(pageable.getPageSize());
			hasNext = true;
		}
		SliceImpl<MoimScheduleAndUser> moimSchedulesSlice = new SliceImpl<>(content, pageable, hasNext);
		Slice<DiaryDto> diarySlices = moimSchedulesSlice.map(DiaryDto::new);
		return new SliceDiaryDto(diarySlices);
	}
}