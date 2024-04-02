package com.nellshark.backend.repositories;

import com.nellshark.backend.models.entities.Price;
import java.time.LocalDateTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

@Repository
public interface PriceRepository extends JpaRepository<Price, Long> {

  @Modifying
  @Query("""
       DELETE FROM Price p
       WHERE p.createdTime < :deletingTime
      """)
  void deleteOlderThan(@NonNull LocalDateTime deletingTime);
}
