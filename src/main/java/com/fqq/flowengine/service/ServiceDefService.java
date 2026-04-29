package com.fqq.flowengine.service;

import com.fqq.flowengine.model.dto.ServiceDefDto;
import com.fqq.flowengine.model.entity.ServiceDef;

import java.util.List;

public interface ServiceDefService {
    List<ServiceDef> findAll();
    ServiceDef findById(Long id);
    ServiceDef findByServiceCode(String serviceCode);
    ServiceDef save(ServiceDef serviceDef);
    void deleteById(Long id);
    
    // 带参数列表的保存（事务性，同时保存入参出参）
    ServiceDef saveWithParams(ServiceDefDto dto);
    
    // 获取服务的入参列表（通过serviceId）
    // 获取服务的出参列表
    // 这些通常由Repository直接调用，但为分层可在此声明
}