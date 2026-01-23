// Инициализация при загрузке страницы
document.addEventListener('DOMContentLoaded', async function() {
    // Инициализация мобильного меню
    initMobileMenu();

    // Обновление UI в зависимости от авторизации
    updateAuthUI();

    // Загрузка статистики
    await loadStats();
});

// Мобильное меню
function initMobileMenu() {
    const menuBtn = document.getElementById('menuBtn');
    const navLinks = document.getElementById('navLinks');

    if (menuBtn) {
        menuBtn.addEventListener('click', () => {
            navLinks.classList.toggle('active');
        });
    }

    // Закрытие меню при клике на ссылку
    document.querySelectorAll('.nav-links a').forEach(link => {
        link.addEventListener('click', () => {
            navLinks.classList.remove('active');
        });
    });
}

// Обновление UI для авторизованного/неавторизованного пользователя
function updateAuthUI() {
    const authButtons = document.getElementById('authButtons');
    const user = utils.checkAuth();

    if (!authButtons) return;

    if (user) {
        // Пользователь авторизован
        authButtons.innerHTML = `
            <div class="user-dropdown">
                <button class="user-btn">
                    <i class="fas fa-user-circle"></i>
                    ${utils.escapeHtml(user.username || user.email)}
                    <i class="fas fa-chevron-down"></i>
                </button>
                <div class="dropdown-menu">
                    <a href="profile.html"><i class="fas fa-user"></i> Профиль</a>
                    <a href="my-items.html"><i class="fas fa-box"></i> Мои вещи</a>
                    <a href="#" id="logoutBtn"><i class="fas fa-sign-out-alt"></i> Выйти</a>
                </div>
            </div>
        `;

        // Добавляем обработчик выхода
        document.getElementById('logoutBtn')?.addEventListener('click', async (e) => {
            e.preventDefault();
            try {
                await api.logout();
                localStorage.removeItem('token');
                localStorage.removeItem('user');
                localStorage.removeItem('refreshToken');
                window.location.href = 'index.html';
            } catch (error) {
                console.error('Logout error:', error);
                // В любом случае очищаем localStorage
                localStorage.removeItem('token');
                localStorage.removeItem('user');
                localStorage.removeItem('refreshToken');
                window.location.href = 'index.html';
            }
        });

        // Управление dropdown
        const userBtn = document.querySelector('.user-btn');
        const dropdownMenu = document.querySelector('.dropdown-menu');

        userBtn?.addEventListener('click', (e) => {
            e.stopPropagation();
            dropdownMenu.classList.toggle('show');
        });

        // Закрытие dropdown при клике вне его
        document.addEventListener('click', () => {
            dropdownMenu?.classList.remove('show');
        });
    } else {
        // Пользователь не авторизован
        authButtons.innerHTML = `
            <a href="login.html" class="btn btn-outline">
                <i class="fas fa-sign-in-alt"></i> Войти
            </a>
            <a href="register.html" class="btn btn-primary">
                <i class="fas fa-user-plus"></i> Регистрация
            </a>
        `;
    }
}

// Загрузка статистики
async function loadStats() {
    try {
        // В реальном приложении здесь были бы запросы к API
        // Для демо используем случайные числа

        // Получаем токен для проверки авторизации
        const token = localStorage.getItem('token');

        if (token) {
            try {
                // Пробуем получить реальные данные
                const items = await api.getItems();
                const users = await api.getCurrentUser().catch(() => null);
                const reservations = await api.getReservations().catch(() => []);

                document.getElementById('totalItems').textContent = items?.length || 42;
                document.getElementById('totalUsers').textContent = users ? 156 : 0;
                document.getElementById('activeReservations').textContent = reservations?.length || 18;
                document.getElementById('itemsShared').textContent = 89;
            } catch (error) {
                // Если API недоступно, используем демо-данные
                setDemoStats();
            }
        } else {
            // Демо-данные для неавторизованных пользователей
            setDemoStats();
        }
    } catch (error) {
        console.error('Error loading stats:', error);
        setDemoStats();
    }
}

function setDemoStats() {
    document.getElementById('totalItems').textContent = 42;
    document.getElementById('totalUsers').textContent = 156;
    document.getElementById('activeReservations').textContent = 18;
    document.getElementById('itemsShared').textContent = 89;
}

// Добавляем CSS для dropdown
const dropdownStyle = document.createElement('style');
dropdownStyle.textContent = `
    .user-dropdown {
        position: relative;
        display: inline-block;
    }
    
    .user-btn {
        background: none;
        border: 1px solid #ddd;
        padding: 8px 15px;
        border-radius: 20px;
        cursor: pointer;
        display: flex;
        align-items: center;
        gap: 8px;
        font-size: 14px;
        color: #555;
    }
    
    .dropdown-menu {
        position: absolute;
        top: 100%;
        right: 0;
        background: white;
        box-shadow: 0 5px 15px rgba(0,0,0,0.1);
        border-radius: 8px;
        padding: 10px 0;
        min-width: 200px;
        display: none;
        z-index: 1000;
    }
    
    .dropdown-menu.show {
        display: block;
    }
    
    .dropdown-menu a {
        display: block;
        padding: 10px 20px;
        color: #555;
        text-decoration: none;
        transition: background 0.3s;
    }
    
    .dropdown-menu a:hover {
        background: #f5f5f5;
        color: #4CAF50;
    }
    
    .btn-outline {
        background: transparent;
        border: 2px solid #4CAF50;
        color: #4CAF50;
    }
    
    .btn-outline:hover {
        background: #4CAF50;
        color: white;
    }
`;
document.head.appendChild(dropdownStyle);