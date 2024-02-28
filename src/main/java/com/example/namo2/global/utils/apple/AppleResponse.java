package com.example.namo2.global.utils.apple;

import java.util.List;

import com.example.namo2.global.common.exception.BaseException;

import lombok.Getter;
import lombok.Setter;

public class AppleResponse {
	public AppleResponse(){
		throw new IllegalStateException("Utility class");
	}
	@Getter
	@Setter
	public static class ApplePublicKeyDto {
		private String kty;
		private String kid;
		private String use;
		private String alg;
		private String n;
		private String e;
	}

	@Getter
	@Setter
	public static class ApplePublicKeyListDto {
		private List<ApplePublicKeyDto> keys;
	}
}
