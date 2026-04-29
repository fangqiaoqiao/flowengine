// 参数管理
let currentNodeId = '';

function loadNodesForSelect() {
    // 先加载组件列表，再根据选中组件加载环节
    httpGet('/components').then(components => {
        const select = document.getElementById('node-select');
        select.innerHTML = '<option value="">请先选择组件</option>';
        components.forEach(comp => {
            const group = document.createElement('optgroup');
            group.label = `${comp.componentId} - ${comp.name}`;
            httpGet(`/components/${comp.componentId}/nodes`).then(nodes => {
                nodes.forEach(node => {
                    const option = document.createElement('option');
                    option.value = node.nodeId;
                    option.textContent = `${node.nodeId} (${node.nodeName})`;
                    group.appendChild(option);
                });
                select.appendChild(group);
            });
        });
        select.onchange = () => {
            currentNodeId = select.value;
            if (currentNodeId) {
                loadInputParams(currentNodeId);
                loadOutputParams(currentNodeId);
                document.getElementById('add-input-btn').disabled = false;
                document.getElementById('add-output-btn').disabled = false;
            } else {
                document.getElementById('add-input-btn').disabled = true;
                document.getElementById('add-output-btn').disabled = true;
            }
        };
    });
}

function loadInputParams(nodeId) {
    httpGet(`/nodes/${nodeId}/input-params`).then(data => {
        const tbody = document.querySelector('#input-param-table tbody');
        tbody.innerHTML = '';
        if (!Array.isArray(data)) return;
        data.forEach(param => {
            const row = tbody.insertRow();
            row.insertCell(0).innerText = param.paramName;
            row.insertCell(1).innerText = param.paramSource;
            row.insertCell(2).innerText = param.sourceExpression || '';
            row.insertCell(3).innerText = param.required ? '是' : '否';
            const actions = row.insertCell(4);
            const editBtn = document.createElement('button');
            editBtn.innerText = '编辑';
            editBtn.className = 'edit';
            editBtn.onclick = () => editInputParam(param);
            const delBtn = document.createElement('button');
            delBtn.innerText = '删除';
            delBtn.className = 'danger';
            delBtn.onclick = () => deleteInputParam(param.id);
            actions.appendChild(editBtn);
            actions.appendChild(delBtn);
        });
    });
}

function loadOutputParams(nodeId) {
    httpGet(`/nodes/${nodeId}/output-params`).then(data => {
        const tbody = document.querySelector('#output-param-table tbody');
        tbody.innerHTML = '';
        if (!Array.isArray(data)) return;
        data.forEach(param => {
            const row = tbody.insertRow();
            row.insertCell(0).innerText = param.paramName;
            row.insertCell(1).innerText = param.paramValueExpression || '';
            const actions = row.insertCell(2);
            const editBtn = document.createElement('button');
            editBtn.innerText = '编辑';
            editBtn.className = 'edit';
            editBtn.onclick = () => editOutputParam(param);
            const delBtn = document.createElement('button');
            delBtn.innerText = '删除';
            delBtn.className = 'danger';
            delBtn.onclick = () => deleteOutputParam(param.id);
            actions.appendChild(editBtn);
            actions.appendChild(delBtn);
        });
    });
}

// 入参编辑
function editInputParam(param) {
    const formHtml = `
        <h3>编辑入参</h3>
        <div class="form-group"><label>参数名</label><input type="text" id="paramName" value="${param.paramName}"></div>
        <div class="form-group"><label>来源</label>
            <select id="paramSource"><option ${param.paramSource==='request'?'selected':''}>request</option><option ${param.paramSource==='context'?'selected':''}>context</option><option ${param.paramSource==='constant'?'selected':''}>constant</option></select>
        </div>
        <div class="form-group"><label>表达式/常量</label><textarea id="sourceExpression">${param.sourceExpression||''}</textarea></div>
        <div class="form-group"><label>必填</label><input type="checkbox" id="required" ${param.required?'checked':''}></div>
        <button id="saveParamBtn">保存</button>
    `;
    showModal(formHtml, null);
    document.getElementById('saveParamBtn').onclick = () => {
        const updated = {
            nodeId: currentNodeId,
            paramName: document.getElementById('paramName').value,
            paramSource: document.getElementById('paramSource').value,
            sourceExpression: document.getElementById('sourceExpression').value,
            required: document.getElementById('required').checked
        };
        httpPut(`/input-params/${param.id}`, updated).then(() => {
            hideModal();
            loadInputParams(currentNodeId);
            showToast('更新成功');
        });
    };
}

function deleteInputParam(id) {
    if (confirm('删除入参配置？')) {
        httpDelete(`/input-params/${id}`).then(() => loadInputParams(currentNodeId));
    }
}

// 出参编辑
function editOutputParam(param) {
    const formHtml = `
        <h3>编辑出参</h3>
        <div class="form-group"><label>参数名</label><input type="text" id="paramName" value="${param.paramName}"></div>
        <div class="form-group"><label>值表达式(JSONPath或SpEL)</label><textarea id="paramValueExpression">${param.paramValueExpression||''}</textarea></div>
        <button id="saveOutputBtn">保存</button>
    `;
    showModal(formHtml, null);
    document.getElementById('saveOutputBtn').onclick = () => {
        const updated = {
            nodeId: currentNodeId,
            paramName: document.getElementById('paramName').value,
            paramValueExpression: document.getElementById('paramValueExpression').value
        };
        httpPut(`/output-params/${param.id}`, updated).then(() => {
            hideModal();
            loadOutputParams(currentNodeId);
            showToast('更新成功');
        });
    };
}

function deleteOutputParam(id) {
    if (confirm('删除出参配置？')) {
        httpDelete(`/output-params/${id}`).then(() => loadOutputParams(currentNodeId));
    }
}

document.getElementById('add-input-btn').addEventListener('click', () => {
    const formHtml = `
        <h3>新增入参</h3>
        <div class="form-group"><label>参数名</label><input type="text" id="paramName"></div>
        <div class="form-group"><label>来源</label><select id="paramSource"><option>request</option><option>context</option><option>constant</option></select></div>
        <div class="form-group"><label>表达式/常量</label><textarea id="sourceExpression"></textarea></div>
        <div class="form-group"><label>必填</label><input type="checkbox" id="required"></div>
        <button id="createInputParamBtn">创建</button>
    `;
    showModal(formHtml, null);
    document.getElementById('createInputParamBtn').onclick = () => {
        const newParam = {
            nodeId: currentNodeId,
            paramName: document.getElementById('paramName').value,
            paramSource: document.getElementById('paramSource').value,
            sourceExpression: document.getElementById('sourceExpression').value,
            required: document.getElementById('required').checked
        };
        httpPost('/input-params', newParam).then(() => {
            hideModal();
            loadInputParams(currentNodeId);
            showToast('创建成功');
        });
    };
});

document.getElementById('add-output-btn').addEventListener('click', () => {
    const formHtml = `
        <h3>新增出参</h3>
        <div class="form-group"><label>参数名</label><input type="text" id="paramName"></div>
        <div class="form-group"><label>值表达式(JSONPath或SpEL)</label><textarea id="paramValueExpression"></textarea></div>
        <button id="createOutputParamBtn">创建</button>
    `;
    showModal(formHtml, null);
    document.getElementById('createOutputParamBtn').onclick = () => {
        const newParam = {
            nodeId: currentNodeId,
            paramName: document.getElementById('paramName').value,
            paramValueExpression: document.getElementById('paramValueExpression').value
        };
        httpPost('/output-params', newParam).then(() => {
            hideModal();
            loadOutputParams(currentNodeId);
            showToast('创建成功');
        });
    };
});

// 子Tab切换
document.querySelectorAll('.sub-tab-btn').forEach(btn => {
    btn.addEventListener('click', () => {
        const subtab = btn.getAttribute('data-subtab');
        document.querySelectorAll('.sub-tab-content').forEach(tc => tc.classList.remove('active'));
        document.getElementById(`${subtab}-param-panel`).classList.add('active');
        document.querySelectorAll('.sub-tab-btn').forEach(b => b.classList.remove('active'));
        btn.classList.add('active');
    });
});