package com.namo.spring.application.external.api.individual.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.namo.spring.application.external.api.individual.dto.CategoryRequest;
import com.namo.spring.core.common.code.status.ErrorStatus;
import com.namo.spring.core.common.exception.IndividualException;
import com.namo.spring.db.mysql.domains.individual.domain.Category;
import com.namo.spring.db.mysql.domains.individual.domain.Palette;
import com.namo.spring.db.mysql.domains.individual.repository.category.CategoryRepository;
import com.namo.spring.db.mysql.domains.individual.type.CategoryKind;
import com.namo.spring.db.mysql.domains.individual.type.CategoryStatus;
import com.namo.spring.db.mysql.domains.user.domain.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService {
	private final CategoryRepository categoryRepository;

	public Category createCategory(Category category) {
		return categoryRepository.save(category);
	}

	public List<Category> getCategories(Long userId) {
		return categoryRepository.findCategoriesByUserIdAndStatusEquals(userId, CategoryStatus.ACTIVE);
	}

	public void deleteCategory(Long categoryId, Long userId) {
		Category category = getCategory(categoryId);
		validateUsersCategory(userId, category);
		validateBaseCategory(category);
		category.delete();
	}

	public void removeCategoriesByUser(User user) {
		categoryRepository.deleteAllByUser(user);
	}

	public Category getCategory(Long categoryId) {
		return categoryRepository.findById(categoryId)
			.orElseThrow(() -> new IndividualException(ErrorStatus.NOT_FOUND_CATEGORY_FAILURE));
	}

	public Category modifyCategory(Long categoryId, CategoryRequest.PostCategoryDto dto, Palette palette, Long userId) {
		Category category = getCategory(categoryId);
		validateUsersCategory(userId, category);
		category.update(dto.getName(), dto.isShare(), palette);
		return category;
	}

	public void validateUsersCategory(Long userId, Category category) {
		if (category.isNotCreatedByUser(userId)) {
			throw new IndividualException(ErrorStatus.NOT_USERS_CATEGORY);
		}
	}

	private void validateBaseCategory(Category category) {
		if (category.isBaseCategory()) {
			throw new IndividualException(ErrorStatus.NOT_DELETE_BASE_CATEGORY_FAILURE);
		}
	}

	public List<Category> getGroupUsersCategories(List<User> users) {
		List<Category> groupCategoriesByUsers = categoryRepository.findMoimCategoriesByUsers(users, CategoryKind.MOIM);
		validateGroupUsersCategories(users, groupCategoriesByUsers);
		return groupCategoriesByUsers;
	}

	private void validateGroupUsersCategories(List<User> users, List<Category> groupCategoriesByUsers) {
		Set<User> groupCategoriesUsers = groupCategoriesByUsers.stream()
			.map(Category::getUser)
			.collect(Collectors.toSet());
		if (users.size() != groupCategoriesUsers.size()) {
			throw new IndividualException(ErrorStatus.NOT_HAS_GROUP_CATEGORIES_USERS);
		}
	}

}
