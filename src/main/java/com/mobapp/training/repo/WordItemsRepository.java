package com.mobapp.training.repo;

import com.mobapp.training.models.Theme;
import com.mobapp.training.models.WordItems;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface WordItemsRepository extends JpaRepository<WordItems, UUID> {
    List<WordItems> findAllByTheme(Theme theme);

    void deleteByThemeId(UUID themeId);

    void deleteAllByThemeId(UUID themeId);
}
