// 组件管理逻辑
let components = [];

function loadComponents() {
    httpGet('/components').then(data => {
        if (Array.isArray(data)) {
            components = data;
            renderComponentTable();
        } else {
            showToast('加载组件失败', true);
        }
    });
}

function renderComponentTable() {
    const tbody = document.querySelector('#component-table tbody');
    tbody.innerHTML = '';
    components.forEach(comp => {
        const row = tbody.insertRow();
        row.insertCell(0).innerText = comp.id;
        row.insertCell(1).innerText = comp.componentId;
        row.insertCell(2).innerText = comp.name;
        row.insertCell(3).innerText = comp.description || '';
        const actions = row.insertCell(4);
        const editBtn = document.createElement('button');
        editBtn.innerText = '编辑';
        editBtn.className = 'edit';
        editBtn.onclick = () => editComponent(comp);
        const delBtn = document.createElement('button');
        delBtn.innerText = '删除';
        delBtn.className = 'danger';
        delBtn.onclick = () => deleteComponent(comp.id);
        actions.appendChild(editBtn);
        actions.appendChild(delBtn);
    });
}

function editComponent(comp) {
    const formHtml = `
        <h3>编辑组件</h3>
        <div class="form-group"><label>组件ID</label><input type="text" id="compId" value="${comp.componentId}" readonly></div>
        <div class="form-group"><label>名称</label><input type="text" id="name" value="${comp.name}"></div>
        <div class="form-group"><label>描述</label><textarea id="desc">${comp.description || ''}</textarea></div>
        <button id="saveCompBtn">保存</button>
    `;
    showModal(formHtml, null);
    document.getElementById('saveCompBtn').onclick = () => {
        const updated = {
            componentId: comp.componentId,
            name: document.getElementById('name').value,
            description: document.getElementById('desc').value
        };
        httpPut(`/components/${comp.id}`, updated).then(() => {
            hideModal();
            loadComponents();
            showToast('更新成功');
        }).catch(() => showToast('更新失败', true));
    };
}

function deleteComponent(id) {
    if (confirm('确定删除该组件吗？会级联删除所有环节和参数')) {
        httpDelete(`/components/${id}`).then(() => {
            loadComponents();
            showToast('删除成功');
        });
    }
}

// 新增组件按钮
document.getElementById('add-component-btn').addEventListener('click', () => {
    const formHtml = `
        <h3>新增组件</h3>
        <div class="form-group"><label>组件ID</label><input type="text" id="compId" placeholder="例如 SQ007070"></div>
        <div class="form-group"><label>名称</label><input type="text" id="name"></div>
        <div class="form-group"><label>描述</label><textarea id="desc"></textarea></div>
        <button id="createCompBtn">创建</button>
    `;
    showModal(formHtml, null);
    document.getElementById('createCompBtn').onclick = () => {
        const newComp = {
            componentId: document.getElementById('compId').value,
            name: document.getElementById('name').value,
            description: document.getElementById('desc').value
        };
        httpPost('/components', newComp).then(() => {
            hideModal();
            loadComponents();
            showToast('创建成功');
        }).catch(() => showToast('创建失败', true));
    };
});