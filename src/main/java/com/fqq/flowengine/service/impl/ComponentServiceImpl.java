package com.fqq.flowengine.service.impl;

import com.fqq.flowengine.model.entity.Component;
import com.fqq.flowengine.exception.ComponentNotFoundException;
import com.fqq.flowengine.repository.ComponentRepository;
import com.fqq.flowengine.service.ComponentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ComponentServiceImpl implements ComponentService {

    @Autowired
    private ComponentRepository componentRepository;

    @Override
    public List<Component> findAll() {
        return componentRepository.findAll();
    }

    @Override
    public Component findByComponentId(String componentId) {
        return componentRepository.findByComponentId(componentId)
                .orElseThrow(() -> new ComponentNotFoundException("组件不存在: " + componentId));
    }

    @Override
    public Component findById(Long id) {
        return componentRepository.findById(id)
                .orElseThrow(() -> new ComponentNotFoundException("组件ID不存在: " + id));
    }

    @Override
    public Component save(Component component) {
        return componentRepository.save(component);
    }

    @Override
    public void deleteById(Long id) {
        componentRepository.deleteById(id);
    }

    @Override
    public void deleteByComponentId(String componentId) {
        Component component = findByComponentId(componentId);
        componentRepository.delete(component);
    }
}