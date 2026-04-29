// 环节管理（完整版，消除编辑器报红）
let currentComponentId = '';
let allServices = [];

function loadComponentsForSelect() {
    httpGet('/components').then(data => {
        if (Array.isArray(data)) {
            const select = document.getElementById('component-select');
            select.innerHTML = '<option value="">请选择组件</option>';
            data.forEach(comp => {
                const option = document.createElement('option');
                option.value = comp.componentId;
                option.textContent = `${comp.componentId} - ${comp.name}`;
                select.appendChild(option);
            });
            select.onchange = () => {
                currentComponentId = select.value;
                if (currentComponentId) {
                    document.getElementById('add-node-btn').disabled = false;
                    loadNodes(currentComponentId);
                } else {
                    document.getElementById('add-node-btn').disabled = true;
                    document.querySelector('#node-table tbody').innerHTML = '';
                }
            };
        }
    });
    loadServiceList();
}

function loadServiceList() {
    httpGet('/services').then(data => {
        if (Array.isArray(data)) allServices = data;
    });
}

function loadNodes(componentId) {
    httpGet(`/components/${componentId}/nodes`).then(data => {
        const tbody = document.querySelector('#node-table tbody');
        tbody.innerHTML = '';
        if (!Array.isArray(data)) return;
        data.forEach(node => {
            const row = tbody.insertRow();
            row.insertCell(0).innerText = node.id;
            row.insertCell(1).innerText = node.nodeId;
            row.insertCell(2).innerText = node.nodeName;
            row.insertCell(3).innerText = node.nodeOrder;
            row.insertCell(4).innerText = node.processorType;
            const serviceName = node.serviceId ? (allServices.find(s => s.id === node.serviceId)?.serviceName || '') : '';
            row.insertCell(5).innerText = node.serviceId ? `${node.serviceId} (${serviceName})` : '未关联';
            const actions = row.insertCell(6);
            const editBtn = document.createElement('button');
            editBtn.innerText = '编辑';
            editBtn.className = 'edit';
            editBtn.onclick = () => editNode(node);
            const delBtn = document.createElement('button');
            delBtn.innerText = '删除';
            delBtn.className = 'danger';
            delBtn.onclick = () => deleteNode(node.nodeId);
            actions.appendChild(editBtn);
            actions.appendChild(delBtn);
        });
    });
}

function editNode(node) {
    httpGet(`/nodes/${node.nodeId}`).then(fullNode => {
        httpGet('/services').then(services => {
            const serviceOptions = '<option value="">无</option>' + services.map(s => `<option value="${s.id}" ${fullNode.serviceId == s.id ? 'selected' : ''}>${s.serviceCode} - ${s.serviceName}</option>`).join('');
            let mappingsPlaceholder = '';
            if (fullNode.serviceId) {
                mappingsPlaceholder = '<div id="dynamicMappings"></div>';
            } else {
                mappingsPlaceholder = '<div id="dynamicMappings"></div>';
            }
            const formHtml = `
                <h3>编辑环节</h3>
                <div class="form-group"><label>环节ID</label><input type="text" id="nodeId" value="${fullNode.nodeId}" readonly></div>
                <div class="form-group"><label>环节名称</label><input type="text" id="nodeName" value="${fullNode.nodeName}"></div>
                <div class="form-group"><label>顺序</label><input type="number" id="nodeOrder" value="${fullNode.nodeOrder}"></div>
                <div class="form-group"><label>处理器类型</label><select id="processorType">
                    <option ${fullNode.processorType === 'http' ? 'selected' : ''}>http</option>
                    <option ${fullNode.processorType === 'script' ? 'selected' : ''}>script</option>
                    <option ${fullNode.processorType === 'dummy' ? 'selected' : ''}>dummy</option>
                </select></div>
                <div class="form-group"><label>处理器配置(JSON/脚本)</label><textarea id="processorConfig">${fullNode.processorConfig || ''}</textarea></div>
                <div class="form-group"><label>关联服务</label><select id="serviceSelect">${serviceOptions}</select></div>
                ${mappingsPlaceholder}
                <div class="toolbar"><button id="saveNodeBtn">保存</button></div>
            `;
            showModal(formHtml, null);
            const serviceSelect = document.getElementById('serviceSelect');
            const dynamicDiv = document.getElementById('dynamicMappings');
            function loadMappingsForService(serviceId) {
                if (!serviceId) {
                    dynamicDiv.innerHTML = '';
                    return;
                }
                const service = services.find(s => s.id == serviceId);
                if (!service || !service.inputParams || service.inputParams.length === 0) {
                    dynamicDiv.innerHTML = '<div class="warning">该服务未定义入参规范，无需配置映射</div>';
                    return;
                }
                httpGet(`/nodes/${fullNode.nodeId}/mappings`).then(existingMappings => {
                    const mappingMap = new Map();
                    if (Array.isArray(existingMappings)) {
                        existingMappings.forEach(m => mappingMap.set(m.serviceInputParamId, m));
                    }
                    let html = '<h4>服务入参映射</h4><div class="mappings-container">';
                    service.inputParams.forEach(param => {
                        const existing = mappingMap.get(param.id);
                        const sourceType = existing ? existing.sourceType : 'constant';
                        const sourceValue = existing ? existing.sourceValue : '';
                        html += `
                            <div class="mapping-item" data-param-id="${param.id}" data-param-name="${param.paramName}">
                                <strong>${param.paramName}</strong> (${param.paramType}, ${param.required ? '必填' : '可选'})<br/>
                                <label>取值方式:</label>
                                <select class="source-type">
                                    <option value="constant" ${sourceType === 'constant' ? 'selected' : ''}>常量(固定值)</option>
                                    <option value="current_input" ${sourceType === 'current_input' ? 'selected' : ''}>当前环节前端传入的字段值</option>
                                    <option value="context_field" ${sourceType === 'context_field' ? 'selected' : ''}>从已缓存环节字段值获取</option>
                                </select>
                                <input type="text" class="source-value" placeholder="常量值 / 字段名 / 表达式(如${'$'}{SQ01_out.field})" value="${escapeHtml(sourceValue)}" style="width:300px;">
                            </div>
                        `;
                    });
                    html += '</div>';
                    dynamicDiv.innerHTML = html;
                });
            }
            serviceSelect.onchange = () => {
                const selectedId = serviceSelect.value;
                if (selectedId) {
                    loadMappingsForService(parseInt(selectedId));
                } else {
                    dynamicDiv.innerHTML = '';
                }
            };
            if (fullNode.serviceId) {
                serviceSelect.value = fullNode.serviceId;
                loadMappingsForService(fullNode.serviceId);
            }
            document.getElementById('saveNodeBtn').onclick = () => {
                const updated = {
                    nodeId: fullNode.nodeId,
                    componentId: fullNode.componentId,
                    nodeName: document.getElementById('nodeName').value,
                    nodeOrder: parseInt(document.getElementById('nodeOrder').value),
                    processorType: document.getElementById('processorType').value,
                    processorConfig: document.getElementById('processorConfig').value,
                    serviceId: serviceSelect.value ? parseInt(serviceSelect.value) : null,
                    inputMappings: []
                };
                if (updated.serviceId) {
                    const mappingItems = document.querySelectorAll('#dynamicMappings .mapping-item');
                    mappingItems.forEach(item => {
                        const paramId = parseInt(item.getAttribute('data-param-id'));
                        const sourceType = item.querySelector('.source-type').value;
                        const sourceValue = item.querySelector('.source-value').value;
                        updated.inputMappings.push({
                            serviceInputParamId: paramId,
                            sourceType: sourceType,
                            sourceValue: sourceValue
                        });
                    });
                }
                httpPut(`/nodes/${fullNode.nodeId}`, updated).then(() => {
                    hideModal();
                    loadNodes(currentComponentId);
                    showToast('更新成功');
                }).catch(() => showToast('更新失败', true));
            };
        });
    });
}

function deleteNode(nodeId) {
    if (confirm('删除环节会同时删除其入参/出参配置及服务映射，确定？')) {
        httpDelete(`/nodes/${nodeId}`).then(() => {
            loadNodes(currentComponentId);
            showToast('删除成功');
        });
    }
}

document.getElementById('add-node-btn').addEventListener('click', () => {
    httpGet('/services').then(services => {
        const serviceOptions = '<option value="">无</option>' + services.map(s => `<option value="${s.id}">${s.serviceCode} - ${s.serviceName}</option>`).join('');
        const formHtml = `
            <h3>新增环节</h3>
            <div class="form-group"><label>环节ID</label><input type="text" id="nodeId" placeholder="例如 SQ21"></div>
            <div class="form-group"><label>环节名称</label><input type="text" id="nodeName"></div>
            <div class="form-group"><label>顺序</label><input type="number" id="nodeOrder" value="0"></div>
            <div class="form-group"><label>处理器类型</label><select id="processorType">
                <option>http</option><option>script</option><option>dummy</option>
            </select></div>
            <div class="form-group"><label>处理器配置</label><textarea id="processorConfig"></textarea></div>
            <div class="form-group"><label>关联服务</label><select id="serviceSelect">${serviceOptions}</select></div>
            <div id="dynamicMappingsNew"></div>
            <div class="toolbar"><button id="createNodeBtn">创建</button></div>
        `;
        showModal(formHtml, null);
        const serviceSelect = document.getElementById('serviceSelect');
        const dynamicDiv = document.getElementById('dynamicMappingsNew');
        function loadMappingsForServiceNew(serviceId) {
            if (!serviceId) {
                dynamicDiv.innerHTML = '';
                return;
            }
            const service = services.find(s => s.id == serviceId);
            if (!service || !service.inputParams || service.inputParams.length === 0) {
                dynamicDiv.innerHTML = '<div class="warning">该服务未定义入参规范，无需配置映射</div>';
                return;
            }
            let html = '<h4>服务入参映射</h4><div class="mappings-container">';
            service.inputParams.forEach(param => {
                html += `
                    <div class="mapping-item" data-param-id="${param.id}" data-param-name="${param.paramName}">
                        <strong>${param.paramName}</strong> (${param.paramType}, ${param.required ? '必填' : '可选'})<br/>
                        <label>取值方式:</label>
                        <select class="source-type">
                            <option value="constant">常量(固定值)</option>
                            <option value="current_input">当前环节前端传入的字段值</option>
                            <option value="context_field">从已缓存环节字段值获取</option>
                        </select>
                        <input type="text" class="source-value" placeholder="常量值 / 字段名 / 表达式(如${'$'}{SQ01_out.field})" style="width:300px;">
                    </div>
                `;
            });
            html += '</div>';
            dynamicDiv.innerHTML = html;
        }
        serviceSelect.onchange = () => {
            const selectedId = serviceSelect.value;
            if (selectedId) loadMappingsForServiceNew(parseInt(selectedId));
            else dynamicDiv.innerHTML = '';
        };
        document.getElementById('createNodeBtn').onclick = () => {
            const newNode = {
                componentId: currentComponentId,
                nodeId: document.getElementById('nodeId').value,
                nodeName: document.getElementById('nodeName').value,
                nodeOrder: parseInt(document.getElementById('nodeOrder').value),
                processorType: document.getElementById('processorType').value,
                processorConfig: document.getElementById('processorConfig').value,
                serviceId: serviceSelect.value ? parseInt(serviceSelect.value) : null,
                inputMappings: []
            };
            if (newNode.serviceId) {
                const mappingItems = document.querySelectorAll('#dynamicMappingsNew .mapping-item');
                mappingItems.forEach(item => {
                    newNode.inputMappings.push({
                        serviceInputParamId: parseInt(item.getAttribute('data-param-id')),
                        sourceType: item.querySelector('.source-type').value,
                        sourceValue: item.querySelector('.source-value').value
                    });
                });
            }
            httpPost('/nodes', newNode).then(() => {
                hideModal();
                loadNodes(currentComponentId);
                showToast('创建成功');
            }).catch(() => showToast('创建失败', true));
        };
    });
});

function escapeHtml(str) {
    if (!str) return '';
    return str.replace(/[&<>]/g, function(m) {
        if (m === '&') return '&amp;';
        if (m === '<') return '&lt;';
        if (m === '>') return '&gt;';
        return m;
    });
}