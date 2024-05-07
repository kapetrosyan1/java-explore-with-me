package ru.practicum.ewm.category.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.category.model.Category;

public interface JpaCategoryRepository extends JpaRepository<Category, Long> {
}
