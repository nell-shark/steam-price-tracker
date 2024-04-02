package com.nellshark.backend.repositories;

import com.nellshark.backend.models.entities.App;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

@Repository
public interface AppRepository extends JpaRepository<App, Long> {

  @Override
  @EntityGraph(attributePaths = {"platforms", "prices"})
  @NonNull
  Optional<App> findById(@NonNull Long id);

  @Query("""
       SELECT a.id
       FROM App a
      """)
  List<Long> findAllIds();

  @Query("""
       SELECT a
       FROM App a
       WHERE LOWER(a.name)
       LIKE LOWER(CONCAT(:prefixName, '%'))
       ORDER BY CASE WHEN a.type = 'GAME' THEN 0 ELSE 1 END
      """)
  Page<App> findByNameStartsWithIgnoreCaseOrderByType(
      @NonNull String prefixName,
      @NonNull Pageable pageable);
}
