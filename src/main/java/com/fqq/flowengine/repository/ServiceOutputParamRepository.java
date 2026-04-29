package com.fqq.flowengine.repository;

import com.fqq.flowengine.model.entity.ServiceOutputParam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceOutputParamRepository extends JpaRepository<ServiceOutputParam, Long> {
    List<ServiceOutputParam> findByServiceIdOrderByIdAsc(Long serviceId);
    void deleteByServiceId(Long serviceId);
}