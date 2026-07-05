package com.enzo.finpulse.repository;

import com.enzo.finpulse.model.Category;
import com.enzo.finpulse.model.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findByUserId(Long userId);

    List<Category> findByUserIdAndTipo(Long userId, TransactionType tipo);

    Optional<Category> findByIdAndUserId(Long id, Long userId);
}
