let fileInput, numberInput, submitBtn, resetBtn, resultDiv;

document.addEventListener('DOMContentLoaded', function() {
    fileInput = document.getElementById('fileInput');
    numberInput = document.getElementById('numberInput');
    submitBtn = document.getElementById('submitBtn');
    resetBtn = document.getElementById('resetBtn');
    resultDiv = document.getElementById('result');

    fileInput.addEventListener('change', updateButtonState);
    numberInput.addEventListener('input', updateButtonState);

    updateButtonState();
});

function updateButtonState() {
    if (fileInput.files.length > 0 && numberInput.value && parseInt(numberInput.value) > 0) {
        submitBtn.disabled = false;
    } else {
        submitBtn.disabled = true;
    }
}

function resetForm() {
    fileInput.value = '';
    numberInput.value = '';
    resultDiv.classList.remove('show');
    updateButtonState();
}

function findNthMin() {
    const file = fileInput.files[0];
    const n = parseInt(numberInput.value);

    if (!file) {
        showResult('Пожалуйста, выберите XLSX файл.', 'error');
        return;
    }
    if (isNaN(n) || n < 1) {
        showResult('Пожалуйста, введите корректное число N (N >= 1).', 'error');
        return;
    }

    const formData = new FormData();
    formData.append('file', file);
    formData.append('n', n);


    const apiEndpoint = '/api/findMin';

    showResult('Обработка...', 'success');

    fetch(apiEndpoint, {
        method: 'POST',
        body: formData
    })
        .then(response => {
            if (!response.ok) {
                return response.text().then(text => { throw new Error(`Ошибка HTTP! Статус: ${response.status}, сообщение: ${text}`); });
            }
            return response.text();
        })
        .then(data => {
            showResult(`N-е минимальное число (${n}-е): ${data}`, 'success');
        })
        .catch(error => {
            console.error('Ошибка:', error);
            showResult(`Произошла ошибка: ${error.message}`, 'error');
        });
}

function showResult(message, type) {
    resultDiv.textContent = message;
    resultDiv.classList.remove('success', 'error', 'show');
    resultDiv.classList.add(type);
    void resultDiv.offsetWidth;
    resultDiv.classList.add('show');
}