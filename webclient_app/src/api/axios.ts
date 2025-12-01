import axios from 'axios';

// Получаем URL из переменных окружения (Vite использует import.meta.env)
// Если переменная не задана, используем http://localhost:7777 как fallback
const BASE_URL = import.meta.env.VITE_OAUTH_URL || 'http://localhost:7777';

const api = axios.create({
    baseURL: BASE_URL,
    headers: {
        'Content-Type': 'application/json',
    },
    // Опционально: таймаут запроса (например, 10 секунд)
    timeout: 10000,
});

export default api;