package com.mobapp.training.repo;

import com.mobapp.training.models.Domains;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface DomainsRepository extends JpaRepository<Domains, UUID> {
    Optional<Domains> findByDomain(String domain);
    boolean existsByDomain(String domain);
}