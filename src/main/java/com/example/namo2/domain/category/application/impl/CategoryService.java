package com.example.namo2.domain.category.application.impl;

import static com.example.namo2.global.common.response.BaseResponseStatus.*;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.namo2.domain.category.dao.repository.CategoryRepository;
import com.example.namo2.domain.category.domain.Category;
import com.example.namo2.domain.category.domain.CategoryStatus;
import com.example.namo2.domain.category.domain.Palette;
import com.example.namo2.domain.category.ui.dto.CategoryRequest;

import com.example.namo2.domain.user.domain.User;

import com.example.namo2.global.common.exception.BaseException;
import com.example.namo2.global.common.response.BaseResponseStatus;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService {
	private final CategoryRepository categoryRepository;

	public Category create(Category category) {
		return categoryRepository.save(category);
	}

	public List<Category> getCategories(Long userId) {
		return categoryRepository.findCategoriesByUserIdAndStatusEquals(userId, CategoryStatus.ACTIVE);
	}

	public void delete(Long categoryId, Long userId) {
		Category category = getCategory(categoryId);
		validateBaseCategory(category);
		validateUsersCategory(userId, category);
		category.delete();
	}

	public void removeCategoriesByUser(User user) {
		categoryRepository.deleteAllByUser(user);
	}

	public Category getCategory(Long categoryId) {
		return categoryRepository.findById(categoryId)
			.orElseThrow(() -> new BaseException(NOT_FOUND_CATEGORY_FAILURE));
	}

	public Category modifyCategory(Long categoryId, CategoryRequest.PostCategoryDto dto, Palette palette, Long userId) {
		Category category = getCategory(categoryId);
		validateBaseCategory(category);
		validateUsersCategory(userId, category);
		category.update(dto.getName(), dto.isShare(), palette);
		return category;
	}

	private void validateUsersCategory(Long userId, Category category) {
		if (category.isNotCreatedByUser(userId)) {
			throw new BaseException(BaseResponseStatus.NOT_USERS_CATEGORY);
		}
	}

	private void validateBaseCategory(Category category) {
		if (category.getName().equals("일정") || category.getName().equals("모임")) {
			throw new BaseException(NOT_DELETE_BASE_CATEGORY_FAILURE);
		}
	}

	public List<Category> getMoimUsersCategories(List<User> users) {
		return categoryRepository.findMoimCategoriesByUsers(users);
	}
}
