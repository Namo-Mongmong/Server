package com.example.namo2.domain.category.dao.repository;

import com.example.namo2.domain.category.domain.Palette;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaletteRepository extends JpaRepository<Palette, Long> {
}