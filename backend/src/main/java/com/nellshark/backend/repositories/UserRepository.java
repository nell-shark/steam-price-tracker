package com.nellshark.backend.repositories;

import com.nellshark.backend.models.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  @Query("""
       SELECT u
       FROM User u
       WHERE u.email = :email
      """)
  Optional<User> findByEmail(@NonNull String email);

  @Query("""
       SELECT COUNT(u) > 0
       FROM User u
       WHERE u.email = :email
      """)
  boolean isEmailTaken(@NonNull String email);
}
