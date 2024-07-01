package com.namo.spring.application.external.api.group.converter;

import com.namo.spring.db.mysql.domains.group.domain.MoimMemo;
import com.namo.spring.db.mysql.domains.group.domain.MoimSchedule;

public class GroupMemoConverter {
	private GroupMemoConverter() {
		throw new IllegalStateException("Utill Classes");
	}

	public static MoimMemo toGroupMemo(MoimSchedule groupSchedule) {
		return MoimMemo.builder()
			.moimSchedule(groupSchedule)
			.build();
	}
}
