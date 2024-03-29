package com.example.namo2.domain.user.ui;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.example.namo2.domain.user.application.UserFacade;
import com.example.namo2.domain.user.ui.dto.UserRequest;
import com.example.namo2.domain.user.ui.dto.UserResponse;

import com.example.namo2.global.common.response.BaseResponse;

import lombok.RequiredArgsConstructor;

@Tag(name = "Auth", description = "로그인, 회원가입 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
	private final UserFacade userFacade;

	@Operation(summary = "kakao 회원가입", description = "kakao 소셜 로그인을 통한 회원가입")
	@PostMapping(value = "/kakao/signup")
	public BaseResponse<UserResponse.SignUpDto> kakaoSignup(
		@Valid @RequestBody UserRequest.SocialSignUpDto signUpDto
	) {
		UserResponse.SignUpDto signupDto = userFacade.signupKakao(signUpDto);
		return new BaseResponse<>(signupDto);
	}

	@Operation(summary = "naver 회원가입", description = "naver 소셜 로그인을 통한 회원가입")
	@PostMapping(value = "/naver/signup")
	public BaseResponse<UserResponse.SignUpDto> naverSignup(
		@Valid @RequestBody UserRequest.SocialSignUpDto signUpDto
	) {
		UserResponse.SignUpDto signupDto = userFacade.signupNaver(signUpDto);
		return new BaseResponse<>(signupDto);
	}

	@Operation(summary = "apple 회원가입", description = "apple 소셜 로그인을 통한 회원가입.")
	@PostMapping(value = "/apple/signup")
	public BaseResponse<UserResponse.SignUpDto> appleSignup(
		@Valid @RequestBody UserRequest.AppleSignUpDto dto
	) {
		UserResponse.SignUpDto signupDto = userFacade.signupApple(dto);
		return new BaseResponse<>(signupDto);
	}

	@Operation(summary = "토큰 재발급", description = "토큰 재발급")
	@PostMapping(value = "/reissuance")
	public BaseResponse<UserResponse.SignUpDto> reissueAccessToken(
		@Valid @RequestBody UserRequest.SignUpDto signUpDto
	) {
		UserResponse.SignUpDto signupDto = userFacade.reissueAccessToken(signUpDto);
		return new BaseResponse<>(signupDto);
	}

	@Operation(summary = "로그아웃", description = "로그아웃")
	@PostMapping(value = "/logout")
	public BaseResponse logout(
		@Valid @RequestBody UserRequest.LogoutDto logoutDto
	) {
		userFacade.logout(logoutDto);
		return BaseResponse.ok();
	}

	@Operation(summary = "kakao 회원 탈퇴", description = "kakao 회원 탈퇴")
	@PostMapping("/kakao/delete")
	public BaseResponse<?> removeKakaoUser(
		HttpServletRequest request,
		@Valid @RequestBody UserRequest.DeleteUserDto deleteUserDto
	) {
		userFacade.removeKakaoUser(request, deleteUserDto.getAccessToken());
		return BaseResponse.ok();
	}

	@Operation(summary = "네이버 회원 탈퇴", description = "네이버 회원 탈퇴")
	@PostMapping("/naver/delete")
	public BaseResponse<?> removeNaverUser(
		HttpServletRequest request,
		@Valid @RequestBody UserRequest.DeleteUserDto deleteUserDto
	) {
		userFacade.removeNaverUser(request, deleteUserDto.getAccessToken());
		return BaseResponse.ok();
	}

	@SuppressWarnings({"checkstyle:WhitespaceAround", "checkstyle:RegexpMultiline"})
	@Operation(summary = "애플 회원 탈퇴", description = "애플 회원 탈퇴")
	@PostMapping("/apple/delete")
	public BaseResponse<?> removeAppleUser(
		HttpServletRequest request,
		@Valid @RequestBody UserRequest.DeleteAppleUserDto deleteAppleUserDto
	) {
		userFacade.removeAppleUser(request, deleteAppleUserDto.getAuthorizationCode());
		return BaseResponse.ok();
	}
}
