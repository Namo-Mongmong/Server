package com.example.namo2.domain.moim.dao.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.namo2.domain.moim.domain.MoimSchedule;

public interface MoimScheduleRepository extends JpaRepository<MoimSchedule, Long> {
	@Query("select ms"
		+ " from MoimSchedule ms"
		+ " left join fetch ms.moimMemo"
		+ " where ms.id = :id")
	Optional<MoimSchedule> findMoimScheduleWithMoimMemoById(Long id);

	@Query("select ms"
		+ " from MoimSchedule ms"
		+ " join fetch ms.moimScheduleAndUsers"
		+ " where ms.id= :id")
	Optional<MoimSchedule> findMoimSheduleAndMoimScheduleAndUsersById(Long id);
}
