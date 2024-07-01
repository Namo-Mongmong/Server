package com.namo.spring.application.external.api.group.converter;

import com.namo.spring.db.mysql.domains.group.domain.Moim;

public class GroupConverter {
	private GroupConverter() {
		throw new IllegalStateException("Util Class");
	}

	public static Moim toGroup(String name, String imgUrl) {
		return Moim.builder()
			.name(name)
			.imgUrl(imgUrl)
			.build();
	}
}
