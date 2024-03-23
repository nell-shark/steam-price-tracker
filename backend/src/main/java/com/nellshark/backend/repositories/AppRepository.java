package com.nellshark.backend.repositories;

import com.nellshark.backend.models.App;
import java.util.List;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AppRepository extends JpaRepository<App, Long> {

  @Override
  @NotNull
  @EntityGraph(attributePaths = {"platforms", "prices"})
  Optional<App> findById(@NotNull Long id);

  @Query("""
       SELECT g.id
       FROM App g
      """)
  List<Long> findAllIds();

  @Query("""
       SELECT g
       FROM App g
       WHERE LOWER(g.name)
       LIKE LOWER(CONCAT(:prefixName, '%'))
       ORDER BY CASE WHEN g.type = 'GAME' THEN 0 ELSE 1 END, g.type
      """)
  List<App> findByNameStartsWithIgnoreCaseOrderByType(@NotNull String prefixName);
}
