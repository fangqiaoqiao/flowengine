package com.fqq.flowengine.repository;

import com.fqq.flowengine.model.entity.ServiceDef;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ServiceDefRepository extends JpaRepository<ServiceDef, Long> {
    Optional<ServiceDef> findByServiceCode(String serviceCode);
    boolean existsByServiceCode(String serviceCode);
    void deleteByServiceCode(String serviceCode);
}