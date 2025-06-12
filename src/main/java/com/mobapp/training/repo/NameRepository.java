package com.mobapp.training.repo;

import com.mobapp.training.models.NameEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NameRepository extends JpaRepository<NameEntity, UUID> {
    @Query(value = "SELECT * FROM names WHERE format_type = :format ORDER BY random() LIMIT :count", nativeQuery = true)
    List<NameEntity> findRandomNamesByFormat(@Param("format") String format, @Param("count") int count);
}
