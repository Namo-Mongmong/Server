package com.namo.spring.application.external.domain.group.repository.diary;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.namo.spring.application.external.domain.group.domain.MoimMemoLocation;
import com.namo.spring.application.external.domain.group.domain.MoimMemoLocationAndUser;

import com.namo.spring.application.external.domain.user.domain.User;

public interface MoimMemoLocationAndUserRepository extends JpaRepository<MoimMemoLocationAndUser, Long> {
	@Modifying
	@Query("delete from MoimMemoLocationAndUser lau where lau.moimMemoLocation = :moimMemoLocation")
	void deleteMoimMemoLocationAndUserByMoimMemoLocation(MoimMemoLocation moimMemoLocation);

	@Modifying
	@Query("delete from MoimMemoLocationAndUser lau where lau.moimMemoLocation in :moimMemoLocation")
	void deleteMoimMemoLocationAndUserByMoimMemoLocation(List<MoimMemoLocation> moimMemoLocation);

	void deleteAllByUser(User user);
}