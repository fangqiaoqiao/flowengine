// 服务管理
function loadServices() {
    httpGet('/services').then(data => {
        const tbody = document.querySelector('#service-table tbody');
        tbody.innerHTML = '';
        if (!Array.isArray(data)) return;
        data.forEach(svc => {
            const row = tbody.insertRow();
            row.insertCell(0).innerText = svc.id;
            row.insertCell(1).innerText = svc.serviceCode;
            row.insertCell(2).innerText = svc.serviceName;
            row.insertCell(3).innerText = svc.protocol || 'HTTP';
            row.insertCell(4).innerText = svc.url || '';
            row.insertCell(5).innerText = svc.method || 'POST';
            const actions = row.insertCell(6);
            const editBtn = document.createElement('button');
            editBtn.innerText = '编辑';
            editBtn.className = 'edit';
            editBtn.onclick = () => editService(svc);
            const delBtn = document.createElement('button');
            delBtn.innerText = '删除';
            delBtn.className = 'danger';
            delBtn.onclick = () => deleteService(svc.id);
            actions.appendChild(editBtn);
            actions.appendChild(delBtn);
        });
    });
}

function editService(service) {
    httpGet(`/services/${service.id}`).then(full => {
        const formHtml = `
            <h3>编辑服务</h3>
            <div class="form-group"><label>服务编码</label><input type="text" id="serviceCode" value="${escapeHtml(full.serviceCode)}" readonly></div>
            <div class="form-group"><label>服务名称</label><input type="text" id="serviceName" value="${escapeHtml(full.serviceName)}"></div>
            <div class="form-group"><label>描述</label><textarea id="description">${escapeHtml(full.description || '')}</textarea></div>
            <div class="form-group"><label>协议</label><select id="protocol"><option ${full.protocol==='HTTP'?'selected':''}>HTTP</option><option ${full.protocol==='DUBBO'?'selected':''}>DUBBO</option></select></div>
            <div class="form-group"><label>URL</label><input type="text" id="url" value="${escapeHtml(full.url || '')}"></div>
            <div class="form-group"><label>方法</label><input type="text" id="method" value="${escapeHtml(full.method || 'POST')}"></div>
            <div class="form-group"><label>请求头(JSON)</label><textarea id="headers">${escapeHtml(full.headers || '{}')}</textarea></div>
            <hr/>
            <h4>入参规范</h4>
            <div id="inputParamsContainer">
                ${ (full.inputParams || []).map((p, idx) => `
                    <div class="param-item" data-idx="${idx}">
                        <input type="text" placeholder="参数名（如 ROOT.BODY.caseId）" value="${escapeHtml(p.paramName)}" class="inp-name">
                        <select class="inp-type">
                            <option ${p.paramType==='string'?'selected':''}>string</option>
                            <option ${p.paramType==='number'?'selected':''}>number</option>
                            <option ${p.paramType==='boolean'?'selected':''}>boolean</option>
                            <option ${p.paramType==='object'?'selected':''}>object</option>
                            <option ${p.paramType==='array'?'selected':''}>array</option>
                        </select>
                        <label>必填<input type="checkbox" class="inp-required" ${p.required?'checked':''}></label>
                        <input type="text" placeholder="描述" value="${escapeHtml(p.description||'')}" class="inp-desc">
                        <button type="button" class="remove-param">删除</button>
                    </div>
                `).join('') }
            </div>
            <button type="button" id="addInputParam">添加入参</button>
            <hr/>
            <h4>出参规范</h4>
            <div id="outputParamsContainer">
                ${ (full.outputParams || []).map((p, idx) => `
                    <div class="param-item" data-idx="${idx}">
                        <input type="text" placeholder="参数名（如 ROOT.BODY.RETURN_CODE）" value="${escapeHtml(p.paramName)}" class="out-name">
                        <select class="out-type">
                            <option ${p.paramType==='string'?'selected':''}>string</option>
                            <option ${p.paramType==='number'?'selected':''}>number</option>
                            <option ${p.paramType==='boolean'?'selected':''}>boolean</option>
                            <option ${p.paramType==='object'?'selected':''}>object</option>
                            <option ${p.paramType==='array'?'selected':''}>array</option>
                        </select>
                        <input type="text" placeholder="描述" value="${escapeHtml(p.description||'')}" class="out-desc">
                        <button type="button" class="remove-param">删除</button>
                    </div>
                `).join('') }
            </div>
            <button type="button" id="addOutputParam">添加出参</button>
            <div class="toolbar"><button id="saveServiceBtn">保存</button></div>
        `;
        showModal(formHtml, null);

        // 添加入参动态行
        document.getElementById('addInputParam').onclick = () => {
            const container = document.getElementById('inputParamsContainer');
            const div = document.createElement('div');
            div.className = 'param-item';
            div.innerHTML = `
                <input type="text" placeholder="参数名（如 ROOT.BODY.caseId）" class="inp-name">
                <select class="inp-type"><option>string</option><option>number</option><option>boolean</option><option>object</option><option>array</option></select>
                <label>必填<input type="checkbox" class="inp-required"></label>
                <input type="text" placeholder="描述" class="inp-desc">
                <button type="button" class="remove-param">删除</button>
            `;
            container.appendChild(div);
            bindRemoveParam(div);
        };

        // 添加出参动态行
        document.getElementById('addOutputParam').onclick = () => {
            const container = document.getElementById('outputParamsContainer');
            const div = document.createElement('div');
            div.className = 'param-item';
            div.innerHTML = `
                <input type="text" placeholder="参数名（如 ROOT.BODY.RETURN_CODE）" class="out-name">
                <select class="out-type"><option>string</option><option>number</option><option>boolean</option><option>object</option><option>array</option></select>
                <input type="text" placeholder="描述" class="out-desc">
                <button type="button" class="remove-param">删除</button>
            `;
            container.appendChild(div);
            bindRemoveParam(div);
        };

        // 绑定现有删除按钮
        document.querySelectorAll('#inputParamsContainer .remove-param, #outputParamsContainer .remove-param').forEach(btn => {
            bindRemoveParam(btn.parentElement);
        });

        // 保存
        document.getElementById('saveServiceBtn').onclick = () => {
            const inputParams = Array.from(document.querySelectorAll('#inputParamsContainer .param-item')).map(item => ({
                paramName: item.querySelector('.inp-name').value,
                paramType: item.querySelector('.inp-type').value,
                required: item.querySelector('.inp-required').checked,
                description: item.querySelector('.inp-desc').value
            }));
            const outputParams = Array.from(document.querySelectorAll('#outputParamsContainer .param-item')).map(item => ({
                paramName: item.querySelector('.out-name').value,
                paramType: item.querySelector('.out-type').value,
                description: item.querySelector('.out-desc').value
            }));
            const payload = {
                id: full.id,
                serviceCode: document.getElementById('serviceCode').value,
                serviceName: document.getElementById('serviceName').value,
                description: document.getElementById('description').value,
                protocol: document.getElementById('protocol').value,
                url: document.getElementById('url').value,
                method: document.getElementById('method').value,
                headers: document.getElementById('headers').value,
                inputParams: inputParams,
                outputParams: outputParams
            };
            httpPut(`/services/${full.id}`, payload).then(() => {
                hideModal();
                loadServices();
                showToast('保存成功');
            }).catch(() => showToast('保存失败', true));
        };
    });
}

function deleteService(id) {
    if (confirm('删除服务将同时删除其入参出参规范，确定吗？')) {
        httpDelete(`/services/${id}`).then(() => {
            loadServices();
            showToast('删除成功');
        });
    }
}

function bindRemoveParam(div) {
    const btn = div.querySelector('.remove-param');
    if (btn) btn.onclick = () => div.remove();
}

document.getElementById('add-service-btn').addEventListener('click', () => {
    const formHtml = `
        <h3>新增服务</h3>
        <div class="form-group"><label>服务编码</label><input type="text" id="serviceCode"></div>
        <div class="form-group"><label>服务名称</label><input type="text" id="serviceName"></div>
        <div class="form-group"><label>描述</label><textarea id="description"></textarea></div>
        <div class="form-group"><label>协议</label><select id="protocol"><option>HTTP</option><option>DUBBO</option></select></div>
        <div class="form-group"><label>URL</label><input type="text" id="url"></div>
        <div class="form-group"><label>方法</label><input type="text" id="method" value="POST"></div>
        <div class="form-group"><label>请求头(JSON)</label><textarea id="headers">{}</textarea></div>
        <div class="toolbar"><button id="createServiceBtn">创建</button></div>
    `;
    showModal(formHtml, null);
    document.getElementById('createServiceBtn').onclick = () => {
        const payload = {
            serviceCode: document.getElementById('serviceCode').value,
            serviceName: document.getElementById('serviceName').value,
            description: document.getElementById('description').value,
            protocol: document.getElementById('protocol').value,
            url: document.getElementById('url').value,
            method: document.getElementById('method').value,
            headers: document.getElementById('headers').value,
            inputParams: [],
            outputParams: []
        };
        httpPost('/services', payload).then(() => {
            hideModal();
            loadServices();
            showToast('创建成功');
        }).catch(() => showToast('创建失败', true));
    };
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