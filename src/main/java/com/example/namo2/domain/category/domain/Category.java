package com.example.namo2.domain.category.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import com.example.namo2.domain.user.domain.User;

import com.example.namo2.global.common.entity.BaseTimeEntity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "category_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "palette_id")
	private Palette palette;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@Column(length = 20)
	private String name;

	@Column(nullable = false, columnDefinition = "TINYINT(1)")
	private Boolean share;

	@Enumerated(EnumType.STRING)
	private CategoryStatus status;

	@Builder
	public Category(Palette palette, User user, String name, Boolean share) {
		this.palette = palette;
		this.user = user;
		this.name = name;
		this.share = share;
		this.status = CategoryStatus.ACTIVE;
	}

	public void update(String name, Boolean share, Palette palette) {
		this.name = name;
		this.share = share;
		this.palette = palette;
	}

	public void delete() {
		this.status = CategoryStatus.DELETE;
	}

	public boolean isNotCreatedByUser(Long userId) {
		return this.user.getId() == userId;
	}
}
