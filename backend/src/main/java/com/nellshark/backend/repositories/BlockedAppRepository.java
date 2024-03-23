package com.nellshark.backend.repositories;

import com.nellshark.backend.models.BlockedApp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlockedAppRepository extends JpaRepository<BlockedApp, Long> {

}
