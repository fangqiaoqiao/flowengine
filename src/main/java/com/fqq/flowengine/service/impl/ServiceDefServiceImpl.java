package com.fqq.flowengine.service.impl;

import com.fqq.flowengine.model.dto.ServiceDefDto;
import com.fqq.flowengine.model.dto.ServiceInputParamDto;
import com.fqq.flowengine.model.dto.ServiceOutputParamDto;
import com.fqq.flowengine.model.entity.ServiceDef;
import com.fqq.flowengine.model.entity.ServiceInputParam;
import com.fqq.flowengine.model.entity.ServiceOutputParam;
import com.fqq.flowengine.repository.ServiceDefRepository;
import com.fqq.flowengine.repository.ServiceInputParamRepository;
import com.fqq.flowengine.repository.ServiceOutputParamRepository;
import com.fqq.flowengine.service.ServiceDefService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ServiceDefServiceImpl implements ServiceDefService {

    @Autowired
    private ServiceDefRepository serviceDefRepository;
    @Autowired
    private ServiceInputParamRepository inputParamRepository;
    @Autowired
    private ServiceOutputParamRepository outputParamRepository;

    @Override
    public List<ServiceDef> findAll() {
        return serviceDefRepository.findAll();
    }

    @Override
    public ServiceDef findById(Long id) {
        return serviceDefRepository.findById(id).orElseThrow(() -> new RuntimeException("服务不存在, id=" + id));
    }

    @Override
    public ServiceDef findByServiceCode(String serviceCode) {
        return serviceDefRepository.findByServiceCode(serviceCode)
                .orElseThrow(() -> new RuntimeException("服务不存在, serviceCode=" + serviceCode));
    }

    @Override
    public ServiceDef save(ServiceDef serviceDef) {
        return serviceDefRepository.save(serviceDef);
    }

    @Override
    public void deleteById(Long id) {
        // 由于数据库设置了 ON DELETE CASCADE，删除服务会级联删除入参出参
        serviceDefRepository.deleteById(id);
    }

    @Override
    public ServiceDef saveWithParams(ServiceDefDto dto) {
        ServiceDef def = new ServiceDef();
        if (dto.getId() != null) {
            def = findById(dto.getId());
        }
        def.setServiceCode(dto.getServiceCode());
        def.setServiceName(dto.getServiceName());
        def.setDescription(dto.getDescription());
        def.setProtocol(dto.getProtocol());
        def.setUrl(dto.getUrl());
        def.setMethod(dto.getMethod());
        def.setHeaders(dto.getHeaders());
        ServiceDef saved = serviceDefRepository.save(def);

        // 处理入参：先删除旧的，再添加新的
        inputParamRepository.deleteByServiceId(saved.getId());
        if (dto.getInputParams() != null) {
            for (ServiceInputParamDto paramDto : dto.getInputParams()) {
                ServiceInputParam param = new ServiceInputParam();
                param.setServiceId(saved.getId());
                param.setParamName(paramDto.getParamName());
                param.setParamType(paramDto.getParamType());
                param.setRequired(paramDto.getRequired());
                param.setDescription(paramDto.getDescription());
                inputParamRepository.save(param);
            }
        }

        // 处理出参
        outputParamRepository.deleteByServiceId(saved.getId());
        if (dto.getOutputParams() != null) {
            for (ServiceOutputParamDto paramDto : dto.getOutputParams()) {
                ServiceOutputParam out = new ServiceOutputParam();
                out.setServiceId(saved.getId());
                out.setParamName(paramDto.getParamName());
                out.setParamType(paramDto.getParamType());
                out.setDescription(paramDto.getDescription());
                outputParamRepository.save(out);
            }
        }
        return saved;
    }
}