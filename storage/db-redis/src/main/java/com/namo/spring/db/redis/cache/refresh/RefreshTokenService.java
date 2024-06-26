package com.namo.spring.db.redis.cache.refresh;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class RefreshTokenService {
	private final RefreshTokenRepository refreshTokenRepository;

	public void save(RefreshToken refreshToken) {
		refreshTokenRepository.save(refreshToken);
	}

	public RefreshToken refresh(
		Long userId,
		String oldRefreshToken,
		String newRefreshToken
	) throws IllegalArgumentException, IllegalStateException {
		RefreshToken refreshToken = findOrElseThrow(userId);

		validateToken(oldRefreshToken, refreshToken);

		refreshToken.rotation(newRefreshToken);
		refreshTokenRepository.save(refreshToken);

		return refreshToken;
	}

	public void delete(
		Long userId,
		String refreshToken
	) throws IllegalArgumentException {
		RefreshToken token = findOrElseThrow(userId);
		refreshTokenRepository.delete(token);
	}

	// TODO: 2024.06.22. Custom exception handling 적용하기 - 루카
	// HACK: 2024.06.22. 임시로 public 으로 변경 - 루카
	public RefreshToken findOrElseThrow(Long userId) {
		return refreshTokenRepository.findById(userId)
			.orElseThrow(() -> new IllegalArgumentException("RefreshToken not found"));
	}

	// TODO: 2024.06.22. Custom exception handling 적용하기 - 루카
	private void validateToken(String oldRefreshToken, RefreshToken refreshToken) {
		if (isTakenAway(oldRefreshToken, refreshToken.getToken())) {
			refreshTokenRepository.delete(refreshToken);
			throw new IllegalStateException("refresh token mismatched");
		}
	}

	private boolean isTakenAway(String requestRefreshToken, String expectedRefreshToken) {
		return !requestRefreshToken.equals(expectedRefreshToken);
	}

}
