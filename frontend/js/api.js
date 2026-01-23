// Базовый URL вашего бэкенда
const API_BASE_URL = 'http://localhost:8080/api';

// Функция для получения токена из localStorage
function getToken() {
    return localStorage.getItem('token');
}

// Функция для получения заголовков с авторизацией
function getAuthHeaders() {
    const token = getToken();
    return {
        'Content-Type': 'application/json',
        'Authorization': token ? `Bearer ${token}` : ''
    };
}

// Базовые функции для работы с API
const api = {
    // Аутентификация
    async register(userData) {
        const response = await fetch(`${API_BASE_URL}/auth/signup`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(userData)
        });
        return await response.json();
    },

    async login(credentials) {
        const response = await fetch(`${API_BASE_URL}/auth/signin`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(credentials)
        });
        return await response.json();
    },

    async logout() {
        const response = await fetch(`${API_BASE_URL}/auth/signout`, {
            method: 'POST',
            headers: getAuthHeaders()
        });
        return await response.json();
    },

    // Пользователи
    async getCurrentUser() {
        const response = await fetch(`${API_BASE_URL}/user/me`, {
            headers: getAuthHeaders()
        });
        return await response.json();
    },

    async updateUser(id, userData) {
        const response = await fetch(`${API_BASE_URL}/user/${id}`, {
            method: 'PUT',
            headers: getAuthHeaders(),
            body: JSON.stringify(userData)
        });
        return await response.json();
    },

    // Предметы
    async getItems() {
        const response = await fetch(`${API_BASE_URL}/items`, {
            headers: getAuthHeaders()
        });
        return await response.json();
    },

    async getItem(id) {
        const response = await fetch(`${API_BASE_URL}/items/${id}`, {
            headers: getAuthHeaders()
        });
        return await response.json();
    },

    async createItem(itemData) {
        const response = await fetch(`${API_BASE_URL}/items`, {
            method: 'POST',
            headers: getAuthHeaders(),
            body: JSON.stringify(itemData)
        });
        return await response.json();
    },

    async updateItem(id, itemData) {
        const response = await fetch(`${API_BASE_URL}/items/${id}`, {
            method: 'PUT',
            headers: getAuthHeaders(),
            body: JSON.stringify(itemData)
        });
        return await response.json();
    },

    async deleteItem(id) {
        const response = await fetch(`${API_BASE_URL}/items/${id}`, {
            method: 'DELETE',
            headers: getAuthHeaders()
        });
        return await response.json();
    },

    async getUserItems(userId) {
        const response = await fetch(`${API_BASE_URL}/items/user/${userId}`, {
            headers: getAuthHeaders()
        });
        return await response.json();
    },

    async getAvailableItems() {
        const response = await fetch(`${API_BASE_URL}/items/available`, {
            headers: getAuthHeaders()
        });
        return await response.json();
    },

    // Бронирования
    async getReservations() {
        const response = await fetch(`${API_BASE_URL}/reservations`, {
            headers: getAuthHeaders()
        });
        return await response.json();
    },

    async createReservation(reservationData) {
        const response = await fetch(`${API_BASE_URL}/reservations`, {
            method: 'POST',
            headers: getAuthHeaders(),
            body: JSON.stringify(reservationData)
        });
        return await response.json();
    },

    async deleteReservation(id) {
        const response = await fetch(`${API_BASE_URL}/reservations/${id}`, {
            method: 'DELETE',
            headers: getAuthHeaders()
        });
        return await response.json();
    }
};

// Экспортируем API для использования в других файлах
window.api = api;