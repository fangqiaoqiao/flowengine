// 公共 AJAX 工具
const API_BASE = '/api/admin';

function httpGet(url) {
    return fetch(API_BASE + url).then(res => res.json());
}
function httpPost(url, data) {
    return fetch(API_BASE + url, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data)
    }).then(res => res.json());
}
function httpPut(url, data) {
    return fetch(API_BASE + url, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data)
    }).then(res => res.json());
}
function httpDelete(url) {
    return fetch(API_BASE + url, { method: 'DELETE' });
}

function showModal(content, onClose) {
    const modal = document.getElementById('modal');
    const modalBody = document.getElementById('modal-body');
    modalBody.innerHTML = content;
    modal.style.display = 'block';
    const closeBtn = modal.querySelector('.close');
    closeBtn.onclick = () => {
        modal.style.display = 'none';
        if (onClose) onClose();
    };
    window.onclick = (e) => {
        if (e.target === modal) {
            modal.style.display = 'none';
            if (onClose) onClose();
        }
    };
}

function hideModal() {
    document.getElementById('modal').style.display = 'none';
}

function showToast(message, isError = false) {
    // 简单 alert，实际可美化
    alert(message);
}