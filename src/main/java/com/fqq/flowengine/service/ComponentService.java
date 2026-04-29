package com.fqq.flowengine.service;

import com.fqq.flowengine.model.entity.Component;

import java.util.List;

public interface ComponentService {
    List<Component> findAll();
    Component findByComponentId(String componentId);
    Component findById(Long id);
    Component save(Component component);
    void deleteById(Long id);
    void deleteByComponentId(String componentId);
}