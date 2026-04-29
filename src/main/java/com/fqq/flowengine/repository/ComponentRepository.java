package com.fqq.flowengine.repository;

import com.fqq.flowengine.model.entity.Component;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ComponentRepository extends JpaRepository<Component, Long> {
    Optional<Component> findByComponentId(String componentId);
    void deleteByComponentId(String componentId);
}