// Логика для страницы бронирований
document.addEventListener('DOMContentLoaded', async function() {
    initMobileMenu();
    updateAuthUI();
    setupTabs();
    setupEventListeners();

    // Проверка авторизации
    const user = utils.checkAuth();
    if (!user) {
        utils.showMessage('Для просмотра бронирований необходимо войти в систему', 'error');
        window.location.href = 'login.html';
        return;
    }

    // Загрузка данных
    await loadAllReservations();
});

function setupTabs() {
    const tabBtns = document.querySelectorAll('.tab-btn');
    const tabContents = document.querySelectorAll('.tab-content');

    tabBtns.forEach(btn => {
        btn.addEventListener('click', function() {
            const tabId = this.dataset.tab;

            // Убираем активный класс у всех кнопок и контента
            tabBtns.forEach(b => b.classList.remove('active'));
            tabContents.forEach(c => c.classList.remove('active'));

            // Добавляем активный класс текущей кнопке и контенту
            this.classList.add('active');
            document.getElementById(tabId).classList.add('active');

            // Загружаем данные для активной вкладки
            if (tabId === 'active-reservations') {
                renderActiveReservations();
            } else if (tabId === 'pending-reservations') {
                renderPendingReservations();
            } else if (tabId === 'history-reservations') {
                renderHistoryReservations();
            }
        });
    });
}

function setupEventListeners() {
    // Фильтр для активных бронирований
    const activeFilter = document.getElementById('activeFilter');
    if (activeFilter) {
        activeFilter.addEventListener('change', renderActiveReservations);
    }

    // Фильтры для истории
    const historyFilter = document.getElementById('historyStatusFilter');
    const applyDateFilter = document.getElementById('applyDateFilter');
    const resetDateFilter = document.getElementById('resetDateFilter');

    if (historyFilter) {
        historyFilter.addEventListener('change', renderHistoryReservations);
    }

    if (applyDateFilter) {
        applyDateFilter.addEventListener('click', renderHistoryReservations);
    }

    if (resetDateFilter) {
        resetDateFilter.addEventListener('click', function() {
            document.getElementById('startDate').value = '';
            document.getElementById('endDate').value = '';
            renderHistoryReservations();
        });
    }

    // Модальные окна
    setupModalListeners();
}

let allReservations = [];
let allItems = [];

async function loadAllReservations() {
    try {
        const user = utils.checkAuth();
        if (!user) return;

        // Загружаем все бронирования пользователя
        const reservations = await api.getReservations();

        // Фильтруем бронирования текущего пользователя
        allReservations = reservations.filter(res => res.userId === user.id);

        // Загружаем предметы для отображения информации
        const itemsResponse = await api.getItems();
        allItems = itemsResponse || [];

        // Рендерим все вкладки
        renderActiveReservations();
        renderPendingReservations();
        renderHistoryReservations();

    } catch (error) {
        console.error('Error loading reservations:', error);
        utils.showMessage('Ошибка загрузки бронирований', 'error');
    }
}

function renderActiveReservations() {
    const today = new Date();
    const filterType = document.getElementById('activeFilter')?.value || 'all';

    // Фильтруем активные бронирования (утверждённые и не завершённые)
    let activeReservations = allReservations.filter(res => {
        if (res.status !== 'APPROVED') return false;

        const endDate = new Date(res.endDate);
        return endDate >= today;
    });

    // Применяем дополнительный фильтр
    if (filterType !== 'all') {
        activeReservations = activeReservations.filter(res => {
            const startDate = new Date(res.startDate);
            const endDate = new Date(res.endDate);

            switch (filterType) {
                case 'current':
                    return startDate <= today && endDate >= today;
                case 'upcoming':
                    return startDate > today;
                case 'ending':
                    const daysLeft = Math.ceil((endDate - today) / (1000 * 60 * 60 * 24));
                    return daysLeft <= 3 && daysLeft >= 0;
                default:
                    return true;
            }
        });
    }

    const grid = document.getElementById('activeReservationsGrid');
    const noItems = document.getElementById('noActiveReservations');

    if (activeReservations.length === 0) {
        grid.innerHTML = '';
        noItems.style.display = 'block';
        return;
    }

    noItems.style.display = 'none';
    grid.innerHTML = '';

    activeReservations.forEach(reservation => {
        const item = allItems.find(i => i.id === reservation.itemId);
        if (!item) return;

        const card = createReservationCard(reservation, item);
        grid.appendChild(card);
    });
}

function renderPendingReservations() {
    const pendingReservations = allReservations.filter(res => res.status === 'PENDING');
    const tbody = document.getElementById('pendingReservationsTable');

    if (pendingReservations.length === 0) {
        tbody.innerHTML = `
            <tr>
                <td colspan="6" class="empty-cell">
                    <i class="fas fa-calendar-check"></i>
                    <p>Нет ожидающих подтверждения бронирований</p>
                </td>
            </tr>
        `;
        return;
    }

    tbody.innerHTML = '';

    pendingReservations.forEach(reservation => {
        const item = allItems.find(i => i.id === reservation.itemId);
        if (!item) return;

        const row = document.createElement('tr');
        row.innerHTML = `
            <td>
                <strong>${utils.escapeHtml(item.name)}</strong>
                <small>${getCategoryName(item.category)}</small>
            </td>
            <td>
                <div class="user-info">
                    <i class="fas fa-user"></i>
                    <span>${utils.escapeHtml(item.ownerName || 'Владелец')}</span>
                </div>
            </td>
            <td>${utils.formatDate(reservation.startDate)}</td>
            <td>${utils.formatDate(reservation.endDate)}</td>
            <td><span class="status-badge status-pending">Ожидает подтверждения</span></td>
            <td>
                <button class="btn btn-sm btn-danger" onclick="cancelReservation('${reservation.id}')">
                    <i class="fas fa-times"></i> Отменить
                </button>
            </td>
        `;

        tbody.appendChild(row);
    });
}

function renderHistoryReservations() {
    const statusFilter = document.getElementById('historyStatusFilter')?.value || 'all';
    const startDateInput = document.getElementById('startDate')?.value;
    const endDateInput = document.getElementById('endDate')?.value;

    // Фильтруем завершённые/отменённые бронирования
    let historyReservations = allReservations.filter(res =>
        res.status === 'COMPLETED' || res.status === 'CANCELLED' || res.status === 'REJECTED'
    );

    // Фильтр по статусу
    if (statusFilter !== 'all') {
        historyReservations = historyReservations.filter(res => res.status === statusFilter);
    }

    // Фильтр по дате
    if (startDateInput) {
        const startDate = new Date(startDateInput);
        historyReservations = historyReservations.filter(res =>
            new Date(res.endDate) >= startDate
        );
    }

    if (endDateInput) {
        const endDate = new Date(endDateInput);
        historyReservations = historyReservations.filter(res =>
            new Date(res.endDate) <= endDate
        );
    }

    // Сортируем по дате окончания (новые первыми)
    historyReservations.sort((a, b) => new Date(b.endDate) - new Date(a.endDate));

    const list = document.getElementById('historyReservationsList');
    const noItems = document.getElementById('noHistoryReservations');

    if (historyReservations.length === 0) {
        list.innerHTML = '';
        noItems.style.display = 'block';
        return;
    }

    noItems.style.display = 'none';
    list.innerHTML = '';

    historyReservations.forEach(reservation => {
        const item = allItems.find(i => i.id === reservation.itemId);
        if (!item) return;

        const historyItem = createHistoryItem(reservation, item);
        list.appendChild(historyItem);
    });
}

function createReservationCard(reservation, item) {
    const today = new Date();
    const startDate = new Date(reservation.startDate);
    const endDate = new Date(reservation.endDate);

    let status = 'Активно';
    let statusClass = 'status-active';

    if (startDate > today) {
        status = 'Предстоящее';
        statusClass = 'status-approved';
    } else if (endDate < today) {
        status = 'Завершено';
        statusClass = 'status-completed';
    }

    const daysLeft = Math.ceil((endDate - today) / (1000 * 60 * 60 * 24));

    const card = document.createElement('div');
    card.className = 'reservation-card';
    card.dataset.id = reservation.id;

    card.innerHTML = `
        <div class="reservation-header">
            <h3>${utils.escapeHtml(item.name)}</h3>
            <span class="reservation-status ${statusClass}">${status}</span>
        </div>
        
        <div class="reservation-body">
            <div class="reservation-item">
                <div class="reservation-item-icon">
                    <i class="${getCategoryIcon(item.category)}"></i>
                </div>
                <div class="reservation-item-info">
                    <h4>${getCategoryName(item.category)}</h4>
                    <p>Владелец: ${utils.escapeHtml(item.ownerName || 'Не указан')}</p>
                </div>
            </div>
            
            ${daysLeft <= 3 && daysLeft >= 0 ? `
                <div class="status-banner warning">
                    <i class="fas fa-clock"></i>
                    <span>Заканчивается через ${daysLeft} ${getDayWord(daysLeft)}</span>
                </div>
            ` : ''}
            
            <div class="reservation-dates">
                <div class="date-info">
                    <span class="label">Начало</span>
                    <span class="date">${utils.formatDate(reservation.startDate)}</span>
                </div>
                <div class="date-info">
                    <span class="label">Окончание</span>
                    <span class="date">${utils.formatDate(reservation.endDate)}</span>
                </div>
            </div>
            
            <div class="reservation-notes">
                <p><strong>Комментарий:</strong> ${reservation.notes || 'Без комментария'}</p>
            </div>
            
            <div class="reservation-actions">
                ${endDate < today ? `
                    <button class="btn btn-primary" onclick="rateItem('${reservation.id}', '${item.id}')">
                        <i class="fas fa-star"></i> Оценить
                    </button>
                ` : `
                    <button class="btn btn-danger" onclick="cancelReservation('${reservation.id}')">
                        <i class="fas fa-times"></i> Отменить
                    </button>
                `}
                <button class="btn btn-secondary" onclick="contactOwner('${item.ownerId}')">
                    <i class="fas fa-envelope"></i> Написать
                </button>
            </div>
        </div>
    `;

    return card;
}

function createHistoryItem(reservation, item) {
    const statusText = {
        'COMPLETED': 'Завершено',
        'CANCELLED': 'Отменено',
        'REJECTED': 'Отклонено'
    }[reservation.status] || 'Завершено';

    const statusClass = {
        'COMPLETED': 'status-completed',
        'CANCELLED': 'status-rejected',
        'REJECTED': 'status-rejected'
    }[reservation.status] || 'status-completed';

    const div = document.createElement('div');
    div.className = 'history-item';

    div.innerHTML = `
        <div class="history-item-main">
            <div class="history-item-title">${utils.escapeHtml(item.name)}</div>
            <div class="history-item-details">
                ${getCategoryName(item.category)} • 
                Владелец: ${utils.escapeHtml(item.ownerName || 'Не указан')}
            </div>
            <div class="history-item-date">
                ${utils.formatDate(reservation.startDate)} — ${utils.formatDate(reservation.endDate)}
            </div>
        </div>
        <div>
            <span class="history-item-status ${statusClass}">${statusText}</span>
        </div>
        ${reservation.status === 'COMPLETED' ? `
            <div>
                <button class="btn btn-sm btn-primary" onclick="rateItem('${reservation.id}', '${item.id}')">
                    <i class="fas fa-star"></i> Оценить
                </button>
            </div>
        ` : ''}
    `;

    return div;
}

function setupModalListeners() {
    // Модальное окно отмены
    const cancelModal = document.getElementById('cancelReservationModal');
    const closeCancelModal = document.getElementById('closeCancelModal');
    const backFromCancel = document.getElementById('backFromCancel');

    if (closeCancelModal) {
        closeCancelModal.addEventListener('click', () => {
            cancelModal.classList.remove('show');
        });
    }

    if (backFromCancel) {
        backFromCancel.addEventListener('click', () => {
            cancelModal.classList.remove('show');
        });
    }

    if (cancelModal) {
        cancelModal.addEventListener('click', function(e) {
            if (e.target === cancelModal) {
                cancelModal.classList.remove('show');
            }
        });
    }

    // Модальное окно оценки
    const rateModal = document.getElementById('rateModal');
    const closeRateModal = document.getElementById('closeRateModal');
    const skipRating = document.getElementById('skipRating');

    if (closeRateModal) {
        closeRateModal.addEventListener('click', () => {
            rateModal.classList.remove('show');
        });
    }

    if (skipRating) {
        skipRating.addEventListener('click', () => {
            rateModal.classList.remove('show');
        });
    }

    if (rateModal) {
        rateModal.addEventListener('click', function(e) {
            if (e.target === rateModal) {
                rateModal.classList.remove('show');
            }
        });
    }
}

// Функции для действий
function cancelReservation(reservationId) {
    const reservation = allReservations.find(r => r.id === reservationId);
    const item = allItems.find(i => i.id === reservation.itemId);

    if (!reservation || !item) {
        utils.showMessage('Бронирование не найдено', 'error');
        return;
    }

    // Заполняем модальное окно данными
    const detailsDiv = document.getElementById('cancelReservationDetails');
    detailsDiv.innerHTML = `
        <div class="detail-item">
            <span class="detail-label">Предмет:</span>
            <span class="detail-value">${utils.escapeHtml(item.name)}</span>
        </div>
        <div class="detail-item">
            <span class="detail-label">Период:</span>
            <span class="detail-value">${utils.formatDate(reservation.startDate)} — ${utils.formatDate(reservation.endDate)}</span>
        </div>
        <div class="detail-item">
            <span class="detail-label">Владелец:</span>
            <span class="detail-value">${utils.escapeHtml(item.ownerName || 'Не указан')}</span>
        </div>
    `;

    // Обработчик подтверждения отмены
    const confirmCancel = document.getElementById('confirmCancel');
    const originalHandler = confirmCancel.onclick;

    confirmCancel.onclick = async function() {
        try {
            const reason = document.getElementById('cancelReason').value;

            // Здесь должен быть API вызов для отмены бронирования
            // await api.cancelReservation(reservationId, { reason });

            // Пока используем удаление
            await api.deleteReservation(reservationId);

            utils.showMessage('Бронирование отменено', 'success');
            document.getElementById('cancelReservationModal').classList.remove('show');

            // Обновляем данные
            await loadAllReservations();

        } catch (error) {
            console.error('Error cancelling reservation:', error);
            utils.showMessage('Ошибка при отмене бронирования', 'error');
        }
    };

    // Показываем модальное окно
    document.getElementById('cancelReservationModal').classList.add('show');
}

// Продолжение js/reservations.js - функция rateItem

function rateItem(reservationId, itemId) {
    const item = allItems.find(i => i.id === itemId);
    if (!item) {
        utils.showMessage('Предмет не найден', 'error');
        return;
    }

    // Заполняем информацию о предмете
    const infoDiv = document.getElementById('rateItemInfo');
    infoDiv.innerHTML = `
        <h3>${utils.escapeHtml(item.name)}</h3>
        <p>${getCategoryName(item.category)}</p>
    `;

    // Настройка звёзд
    const stars = document.querySelectorAll('#ratingStars i');
    const ratingText = document.getElementById('ratingText');
    let selectedRating = 0;

    // Обработчики для звёзд
    stars.forEach(star => {
        star.addEventListener('mouseover', function() {
            const rating = parseInt(this.dataset.rating);
            highlightStars(rating);
            updateRatingText(rating);
        });

        star.addEventListener('click', function() {
            selectedRating = parseInt(this.dataset.rating);
            highlightStars(selectedRating);
            updateRatingText(selectedRating, true);
        });
    });

    // Сброс при уходе мыши
    document.getElementById('ratingStars').addEventListener('mouseleave', function() {
        highlightStars(selectedRating);
        updateRatingText(selectedRating, true);
    });

    function highlightStars(rating) {
        stars.forEach(star => {
            const starRating = parseInt(star.dataset.rating);
            if (starRating <= rating) {
                star.classList.add('active');
            } else {
                star.classList.remove('active');
            }
        });
    }

    function updateRatingText(rating, selected = false) {
        const texts = {
            1: 'Плохо',
            2: 'Удовлетворительно',
            3: 'Хорошо',
            4: 'Отлично',
            5: 'Превосходно'
        };

        if (selected && rating > 0) {
            ratingText.textContent = `Ваша оценка: ${rating} звёзд - ${texts[rating]}`;
            ratingText.style.color = '#4CAF50';
        } else if (rating > 0) {
            ratingText.textContent = `Оценить: ${rating} звёзд - ${texts[rating]}`;
            ratingText.style.color = '#666';
        } else {
            ratingText.textContent = 'Выберите оценку';
            ratingText.style.color = '#666';
        }
    }

    // Обработчик отправки оценки
    const submitRating = document.getElementById('submitRating');
    submitRating.onclick = async function() {
        if (selectedRating === 0) {
            utils.showMessage('Пожалуйста, выберите оценку', 'warning');
            return;
        }

        try {
            const reviewText = document.getElementById('reviewText').value;

            // Здесь должен быть API вызов для отправки отзыва
            // await api.submitRating(reservationId, itemId, selectedRating, reviewText);

            utils.showMessage('Спасибо за ваш отзыв!', 'success');
            document.getElementById('rateModal').classList.remove('show');

            // Обновляем данные
            await loadAllReservations();

        } catch (error) {
            console.error('Error submitting rating:', error);
            utils.showMessage('Ошибка при отправке отзыва', 'error');
        }
    };

    // Показываем модальное окно
    document.getElementById('rateModal').classList.add('show');
}

function contactOwner(ownerId) {
    // В будущем можно реализовать чат
    alert('Функция связи с владельцем будет реализована позже');
}

// Вспомогательные функции
function getDayWord(days) {
    if (days === 1) return 'день';
    if (days >= 2 && days <= 4) return 'дня';
    return 'дней';
}

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

function getCategoryIcon(category) {
    const icons = {
        'ELECTRONICS': 'fas fa-laptop',
        'BOOKS': 'fas fa-book',
        'CLOTHING': 'fas fa-tshirt',
        'SPORTS': 'fas fa-futbol',
        'HOME': 'fas fa-home',
        'OTHER': 'fas fa-box'
    };
    return icons[category] || 'fas fa-box';
}

// Экспортируем функции для использования в других файлах
window.reservationsModule = {
    loadAllReservations,
    cancelReservation,
    rateItem,
    contactOwner
};