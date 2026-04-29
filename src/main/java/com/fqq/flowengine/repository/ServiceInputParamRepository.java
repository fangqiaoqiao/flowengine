package com.fqq.flowengine.repository;

import com.fqq.flowengine.model.entity.ServiceInputParam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceInputParamRepository extends JpaRepository<ServiceInputParam, Long> {
    List<ServiceInputParam> findByServiceIdOrderByIdAsc(Long serviceId);
    void deleteByServiceId(Long serviceId);
}