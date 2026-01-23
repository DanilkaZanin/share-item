// Логика для страницы предметов
document.addEventListener('DOMContentLoaded', async function() {
    // Инициализация
    initMobileMenu();
    updateAuthUI();
    loadItems();

    // Обработчики событий
    setupEventListeners();

    // Проверка авторизации
    const user = utils.checkAuth();
    if (!user) {
        utils.showMessage('Для бронирования предметов необходимо войти в систему', 'warning');
    }
});

// Загрузка предметов
async function loadItems() {
    try {
        const itemsGrid = document.getElementById('itemsGrid');
        itemsGrid.innerHTML = '<div class="loading"><i class="fas fa-spinner fa-spin"></i> Загрузка предметов...</div>';

        const items = await api.getItems();

        if (!items || items.length === 0) {
            itemsGrid.innerHTML = '';
            document.getElementById('noItems').style.display = 'block';
            return;
        }

        document.getElementById('noItems').style.display = 'none';
        renderItems(items);

    } catch (error) {
        console.error('Error loading items:', error);
        document.getElementById('itemsGrid').innerHTML = `
            <div class="loading">
                <i class="fas fa-exclamation-triangle"></i>
                <p>Ошибка загрузки. Проверьте подключение к интернету</p>
                <button onclick="loadItems()" class="btn btn-primary">Повторить</button>
            </div>
        `;
    }
}

// Отображение предметов
function renderItems(items) {
    const itemsGrid = document.getElementById('itemsGrid');
    const currentUser = utils.checkAuth();

    itemsGrid.innerHTML = '';

    items.forEach(item => {
        const itemElement = document.createElement('div');
        itemElement.className = 'item-card';
        itemElement.dataset.id = item.id;
        itemElement.dataset.category = item.category || 'OTHER';
        itemElement.dataset.status = item.status || 'AVAILABLE';

        // Определяем иконку по категории
        const categoryIcons = {
            'ELECTRONICS': 'fas fa-laptop',
            'BOOKS': 'fas fa-book',
            'CLOTHING': 'fas fa-tshirt',
            'SPORTS': 'fas fa-futbol',
            'HOME': 'fas fa-home',
            'OTHER': 'fas fa-box'
        };

        const icon = categoryIcons[item.category] || 'fas fa-box';

        // Форматируем дату
        const createdDate = item.createdAt ? utils.formatDate(item.createdAt) : 'Не указано';

        // Определяем статус
        const isAvailable = item.status === 'AVAILABLE';
        const statusText = isAvailable ? 'Доступно' : 'Забронировано';
        const statusClass = isAvailable ? 'status-available' : 'status-reserved';

        // Проверяем, является ли текущий пользователь владельцем
        const isOwner = currentUser && currentUser.id === item.ownerId;

        itemElement.innerHTML = `
            <div class="item-image" style="background: linear-gradient(135deg, #${getColorHash(item.id)} 0%, #${getColorHash(item.name)} 100%);">
                <i class="${icon}"></i>
            </div>
            <div class="item-content">
                <h3 class="item-title">${utils.escapeHtml(item.name)}</h3>
                <p class="item-description">${utils.escapeHtml(item.description || 'Без описания')}</p>
                
                <div class="item-details">
                    <span>${createdDate}</span>
                    <span class="item-status ${statusClass}">${statusText}</span>
                </div>
                
                <div class="item-category">${getCategoryName(item.category)}</div>
                
                <div class="item-owner">
                    <i class="fas fa-user"></i>
                    <span>${utils.escapeHtml(item.ownerName || 'Аноним')}</span>
                </div>
                
                <div class="item-actions">
                    ${isOwner ? `
                        <button class="btn btn-secondary" onclick="editItem('${item.id}')">
                            <i class="fas fa-edit"></i> Изменить
                        </button>
                        <button class="btn btn-danger" onclick="deleteItem('${item.id}')">
                            <i class="fas fa-trash"></i> Удалить
                        </button>
                    ` : `
                        ${isAvailable ? `
                            <button class="btn btn-primary" onclick="showReservationModal('${item.id}')">
                                <i class="fas fa-calendar-plus"></i> Забронировать
                            </button>
                        ` : `
                            <button class="btn btn-secondary" disabled>
                                <i class="fas fa-calendar-times"></i> Недоступно
                            </button>
                        `}
                        <button class="btn btn-secondary" onclick="viewItem('${item.id}')">
                            <i class="fas fa-eye"></i> Подробнее
                        </button>
                    `}
                </div>
            </div>
        `;

        itemsGrid.appendChild(itemElement);
    });
}

// Получение имени категории
function getCategoryName(category) {
    const categories = {
        'ELECTRONICS': 'Электроника',
        'BOOKS': 'Книги',
        'CLOTHING': 'Одежда',
        'SPORTS': 'Спорт',
        'HOME': 'Для дома',
        'OTHER': 'Другое'
    };
    return categories[category] || 'Другое';
}

// Генерация цвета по хешу
function getColorHash(str) {
    let hash = 0;
    for (let i = 0; i < str.length; i++) {
        hash = str.charCodeAt(i) + ((hash << 5) - hash);
    }

    const colors = [
        '667eea', '764ba2', 'f093fb', 'f5576c',
        '4facfe', '00f2fe', '43e97b', '38f9d7',
        'fa709a', 'fee140', '30cfd0', '330867'
    ];

    return colors[Math.abs(hash) % colors.length];
}

// Продолжение js/items.js

// Настройка обработчиков событий
function setupEventListeners() {
    // Поиск
    const searchInput = document.getElementById('searchInput');
    if (searchInput) {
        searchInput.addEventListener('input', debounce(filterItems, 300));
    }

    // Фильтры
    const categoryFilter = document.getElementById('categoryFilter');
    const statusFilter = document.getElementById('statusFilter');

    if (categoryFilter) {
        categoryFilter.addEventListener('change', filterItems);
    }

    if (statusFilter) {
        statusFilter.addEventListener('change', filterItems);
    }

    // Сброс фильтров
    const resetBtn = document.getElementById('resetFilters');
    if (resetBtn) {
        resetBtn.addEventListener('click', resetFilters);
    }

    // Модальное окно бронирования
    const modal = document.getElementById('reservationModal');
    const closeModal = document.getElementById('closeModal');
    const cancelBtn = document.getElementById('cancelReservation');

    if (closeModal) {
        closeModal.addEventListener('click', hideModal);
    }

    if (cancelBtn) {
        cancelBtn.addEventListener('click', hideModal);
    }

    // Клик вне модального окна
    if (modal) {
        modal.addEventListener('click', function(e) {
            if (e.target === modal) {
                hideModal();
            }
        });
    }

    // Форма бронирования
    const reservationForm = document.getElementById('reservationForm');
    if (reservationForm) {
        reservationForm.addEventListener('submit', handleReservation);
    }

    // Установка минимальной даты (сегодня)
    const today = new Date().toISOString().split('T')[0];
    const startDateInput = document.getElementById('reserveStartDate');
    const endDateInput = document.getElementById('reserveEndDate');

    if (startDateInput) {
        startDateInput.min = today;
        startDateInput.addEventListener('change', function() {
            if (endDateInput) {
                endDateInput.min = this.value;
            }
        });
    }

    if (endDateInput) {
        endDateInput.min = today;
    }
}

// Функция debounce для поиска
function debounce(func, wait) {
    let timeout;
    return function executedFunction(...args) {
        const later = () => {
            clearTimeout(timeout);
            func(...args);
        };
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
    };
}

// Фильтрация предметов
function filterItems() {
    const searchTerm = document.getElementById('searchInput')?.value.toLowerCase() || '';
    const category = document.getElementById('categoryFilter')?.value || '';
    const status = document.getElementById('statusFilter')?.value || '';

    const items = document.querySelectorAll('.item-card');
    let visibleCount = 0;

    items.forEach(item => {
        const title = item.querySelector('.item-title').textContent.toLowerCase();
        const description = item.querySelector('.item-description').textContent.toLowerCase();
        const itemCategory = item.dataset.category;
        const itemStatus = item.dataset.status;

        const matchesSearch = !searchTerm ||
            title.includes(searchTerm) ||
            description.includes(searchTerm);

        const matchesCategory = !category || itemCategory === category;
        const matchesStatus = !status || itemStatus === status;

        if (matchesSearch && matchesCategory && matchesStatus) {
            item.style.display = 'block';
            visibleCount++;
        } else {
            item.style.display = 'none';
        }
    });

    // Показываем сообщение, если ничего не найдено
    const noItems = document.getElementById('noItems');
    if (noItems) {
        noItems.style.display = visibleCount === 0 ? 'block' : 'none';
    }
}

// Сброс фильтров
function resetFilters() {
    const searchInput = document.getElementById('searchInput');
    const categoryFilter = document.getElementById('categoryFilter');
    const statusFilter = document.getElementById('statusFilter');

    if (searchInput) searchInput.value = '';
    if (categoryFilter) categoryFilter.value = '';
    if (statusFilter) statusFilter.value = '';

    filterItems();
}

// Показать модальное окно бронирования
function showReservationModal(itemId) {
    const user = utils.checkAuth();
    if (!user) {
        utils.showMessage('Для бронирования необходимо войти в систему', 'error');
        window.location.href = 'login.html';
        return;
    }

    const modal = document.getElementById('reservationModal');
    const itemIdInput = document.getElementById('reserveItemId');

    if (itemIdInput) {
        itemIdInput.value = itemId;
    }

    // Сброс формы
    const form = document.getElementById('reservationForm');
    if (form) {
        form.reset();

        // Установка дат по умолчанию
        const today = new Date().toISOString().split('T')[0];
        const tomorrow = new Date();
        tomorrow.setDate(tomorrow.getDate() + 1);
        const tomorrowStr = tomorrow.toISOString().split('T')[0];

        const startDate = document.getElementById('reserveStartDate');
        const endDate = document.getElementById('reserveEndDate');

        if (startDate) startDate.value = today;
        if (endDate) endDate.value = tomorrowStr;
    }

    if (modal) {
        modal.classList.add('show');
        document.body.style.overflow = 'hidden';
    }
}

// Скрыть модальное окно
function hideModal() {
    const modal = document.getElementById('reservationModal');
    if (modal) {
        modal.classList.remove('show');
        document.body.style.overflow = 'auto';
    }
}

// Обработка бронирования
async function handleReservation(e) {
    e.preventDefault();

    const itemId = document.getElementById('reserveItemId').value;
    const startDate = document.getElementById('reserveStartDate').value;
    const endDate = document.getElementById('reserveEndDate').value;
    const notes = document.getElementById('reserveNotes').value;

    if (!itemId || !startDate || !endDate) {
        utils.showMessage('Заполните обязательные поля', 'error');
        return;
    }

    // Проверка дат
    const start = new Date(startDate);
    const end = new Date(endDate);

    if (end <= start) {
        utils.showMessage('Дата окончания должна быть позже даты начала', 'error');
        return;
    }

    const user = utils.checkAuth();
    if (!user) {
        utils.showMessage('Сессия истекла. Войдите снова', 'error');
        return;
    }

    const reservationData = {
        itemId: itemId,
        userId: user.id,
        startDate: startDate,
        endDate: endDate,
        notes: notes || null,
        status: 'PENDING'
    };

    try {
        const submitBtn = e.target.querySelector('button[type="submit"]');
        const originalText = submitBtn.innerHTML;
        submitBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Бронирование...';
        submitBtn.disabled = true;

        const response = await api.createReservation(reservationData);

        utils.showMessage('Бронирование успешно создано!', 'success');
        hideModal();

        // Обновляем список предметов
        setTimeout(() => {
            loadItems();
        }, 1000);

    } catch (error) {
        console.error('Reservation error:', error);

        let message = 'Ошибка при бронировании';
        if (error.message.includes('409')) {
            message = 'Предмет уже забронирован на эти даты';
        } else if (error.message.includes('403')) {
            message = 'Недостаточно прав для бронирования';
        } else if (error.message.includes('Network')) {
            message = 'Проверьте подключение к интернету';
        }

        utils.showMessage(message, 'error');
    } finally {
        const submitBtn = e.target.querySelector('button[type="submit"]');
        submitBtn.innerHTML = originalText;
        submitBtn.disabled = false;
    }
}

// Просмотр деталей предмета
function viewItem(itemId) {
    // В будущем можно сделать отдельную страницу детального просмотра
    // Сейчас просто показываем информацию в alert или модальном окне
    alert(`Просмотр предмета ID: ${itemId}\n\nЭта функция будет реализована позже`);
}

// Редактирование предмета
function editItem(itemId) {
    // Перенаправляем на страницу редактирования
    window.location.href = `add-item.html?edit=${itemId}`;
}

// Удаление предмета
async function deleteItem(itemId) {
    if (!confirm('Вы уверены, что хотите удалить этот предмет?')) {
        return;
    }

    try {
        const response = await api.deleteItem(itemId);
        utils.showMessage('Предмет успешно удален', 'success');

        // Удаляем элемент из DOM
        const itemElement = document.querySelector(`.item-card[data-id="${itemId}"]`);
        if (itemElement) {
            itemElement.remove();
        }

        // Проверяем, остались ли предметы
        const items = document.querySelectorAll('.item-card');
        if (items.length === 0) {
            document.getElementById('noItems').style.display = 'block';
        }

    } catch (error) {
        console.error('Delete error:', error);
        utils.showMessage('Ошибка при удалении предмета', 'error');
    }
}

// Экспортируем функции для использования в других файлах
window.itemsModule = {
    loadItems,
    renderItems,
    filterItems,
    showReservationModal,
    viewItem,
    editItem,
    deleteItem
};