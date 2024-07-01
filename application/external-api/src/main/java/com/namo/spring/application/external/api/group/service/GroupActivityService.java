package com.namo.spring.application.external.api.group.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.namo.spring.core.common.code.status.ErrorStatus;
import com.namo.spring.core.common.exception.GroupException;
import com.namo.spring.db.mysql.domains.group.domain.MoimMemo;
import com.namo.spring.db.mysql.domains.group.domain.MoimMemoLocation;
import com.namo.spring.db.mysql.domains.group.domain.MoimMemoLocationAndUser;
import com.namo.spring.db.mysql.domains.group.domain.MoimMemoLocationImg;
import com.namo.spring.db.mysql.domains.group.domain.MoimSchedule;
import com.namo.spring.db.mysql.domains.group.repository.diary.MoimMemoLocationAndUserRepository;
import com.namo.spring.db.mysql.domains.group.repository.diary.MoimMemoLocationImgRepository;
import com.namo.spring.db.mysql.domains.group.repository.diary.MoimMemoLocationRepository;
import com.namo.spring.db.mysql.domains.user.domain.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GroupActivityService {
	private final MoimMemoLocationRepository moimMemoLocationRepository;
	private final MoimMemoLocationAndUserRepository moimMemoLocationAndUserRepository;
	private final MoimMemoLocationImgRepository moimMemoLocationImgRepository;

	public MoimMemoLocation createGroupActivity(MoimMemoLocation groupActivity, MoimMemo groupMemo) {
		if (groupMemo.isFullLocationSize()) {
			throw new GroupException(ErrorStatus.GROUP_MEMO_IS_FULL_ERROR);
		}
		return moimMemoLocationRepository.save(groupActivity);
	}

	public List<MoimMemoLocationAndUser> createGroupActivityAndUsers(
		List<MoimMemoLocationAndUser> groupActivities) {
		return moimMemoLocationAndUserRepository.saveAll(groupActivities);
	}

	public MoimMemoLocationImg createGroupActivityImg(MoimMemoLocationImg groupActivityImg) {
		return moimMemoLocationImgRepository.save(groupActivityImg);
	}

	public MoimMemoLocation getGroupActivityWithImgs(Long memoLocationId) {
		return moimMemoLocationRepository.findMoimMemoLocationWithImgsById(memoLocationId)
			.orElseThrow(() -> new GroupException(ErrorStatus.NOT_FOUND_GROUP_MEMO_LOCATION_FAILURE));
	}

	public void removeGroupActivityAndUsers(MoimMemoLocation groupActivity) {
		moimMemoLocationAndUserRepository.deleteMoimMemoLocationAndUserByMoimMemoLocation(groupActivity);
	}

	public void removeGroupActivityAndUsers(List<MoimMemoLocation> groupActivity) {
		moimMemoLocationAndUserRepository.deleteMoimMemoLocationAndUserByMoimMemoLocation(groupActivity);
	}

	public void removeGroupActivityAndUsersByUser(User user) {
		moimMemoLocationAndUserRepository.deleteAllByUser(user);
	}

	public void removeGroupActivityImgs(MoimMemoLocation groupActivity) {
		moimMemoLocationImgRepository.deleteMoimMemoLocationImgByMoimMemoLocation(groupActivity);
	}

	public void removeGroupActivityImgs(List<MoimMemoLocation> groupActivities) {
		moimMemoLocationImgRepository.deleteMoimMemoLocationImgByMoimMemoLocation(groupActivities);
	}

	public void removeGroupActivity(MoimMemoLocation groupActivity) {
		moimMemoLocationRepository.delete(groupActivity);
	}

	public List<MoimMemoLocation> getGroupActivityWithImgs(MoimMemo groupMemo) {
		return moimMemoLocationRepository.findMoimMemo(groupMemo);
	}

	public List<MoimMemoLocationImg> getGroupActivityImgs(List<MoimMemoLocation> groupActivities) {
		return moimMemoLocationImgRepository
			.findMoimMemoLocationImgsByMoimMemoLocations(groupActivities);
	}

	public List<MoimMemoLocation> getGroupActivities(MoimSchedule groupSchedule) {
		return moimMemoLocationRepository.findMoimMemoLocationsWithImgs(groupSchedule);
	}

	public List<MoimMemoLocationAndUser> getGroupActivityAndUsers(List<MoimMemoLocation> groupActivities) {
		return moimMemoLocationRepository.findMoimMemoLocationAndUsers(groupActivities);
	}

}
