package com.example.namo2.global.utils;

import static com.example.namo2.global.common.response.BaseResponseStatus.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.google.gson.Gson;

import com.example.namo2.domain.user.ui.dto.UserRequest;

import com.example.namo2.global.common.exception.BaseException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class SocialUtils {
	private static final String naverApiURL = "https://openapi.naver.com/v1/nid/me";
	private static final String kakaoApiURL = "https://kapi.kakao.com/v2/user/me";

	public HttpURLConnection connectKakaoResourceServer(UserRequest.SocialSignUpDto signUpReq) throws IOException {
		URL url = new URL(kakaoApiURL);
		HttpURLConnection conn = (HttpURLConnection)url.openConnection();
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Authorization", "Bearer " + signUpReq.getAccessToken());
		return conn;
	}

	public HttpURLConnection connectNaverResourceServer(UserRequest.SocialSignUpDto signUpReq) throws IOException {
		URL url = new URL(naverApiURL);
		HttpURLConnection con = (HttpURLConnection)url.openConnection();
		con.setRequestMethod("GET");
		con.setRequestProperty("Authorization", "Bearer " + signUpReq.getAccessToken());
		return con;
	}

	public void validateSocialAccessToken(HttpURLConnection con) throws IOException, BaseException {
		if (con.getResponseCode() != 200) {
			throw new BaseException(SOCIAL_LOGIN_FAILURE);
		}
	}

	public String findSocialLoginUsersInfo(HttpURLConnection con) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
		String line = "";
		String result = "";
		while ((line = br.readLine()) != null) {
			result += line;
		}
		return result;
	}

	public Map<String, String> findResponseFromNaver(String result) throws BaseException {
		Gson gson = new Gson();
		Map<String, Object> jsonMap = new HashMap<>();
		jsonMap = gson.fromJson(result, jsonMap.getClass());

		if (!jsonMap.get("resultcode").equals("00")) {
			throw new BaseException(SOCIAL_LOGIN_FAILURE);
		}
		/**
		 * 네이버 로그인 시에는 nickname이 null 값으로 받아집니다.
		 * toUser를 일원화시키려면 아래 같은 식으로 name 값을 nickname 값으로 바꿔주셔야해요.
		 */
		Map<String, String> response = (Map<String, String>)jsonMap.get("response");
		response.put("nickname", response.get("name"));
		return response;
	}

	public Map<String, String> findResponseFromKakako(String result) throws BaseException {
		Gson gson = new Gson();
		Map<String, Object> jsonMap = new HashMap<>();
		jsonMap = gson.fromJson(result, jsonMap.getClass());

		Map<String, String> properties = (Map<String, String>)jsonMap.get("properties");
		Map<String, String> kakaoAccount = (Map<String, String>)jsonMap.get("kakao_account");
		if (!(kakaoAccount.get("has_email") != "true")) {
			throw new BaseException(SOCIAL_LOGIN_FAILURE);
		}
		properties.put("email", kakaoAccount.get("email"));
		if ((kakaoAccount.get("has_birthday") == "true")) {
			properties.put("birthday", kakaoAccount.get("birthday"));
		}
		return properties;
	}
}
