// Импортируем наш настроенный экземпляр
import api from '../api/axios';
import type {TokenResponse} from '../types/auth.types';

const SERVER_URL = import.meta.env.VITE_OAUTH_URL;
const CLIENT_ID = import.meta.env.VITE_OAUTH_CLIENT_ID;
const AUTH_HEADER = import.meta.env.VITE_OAUTH_AUTH_HEADER;
const REDIRECT_URI = import.meta.env.VITE_OAUTH_REDIRECT_URI;

const ACCESS_TOKEN_KEY = "access_token";

export const AuthService = {
    // 1. Редирект на сервер авторизации (тут axios не нужен, это браузерный переход)
    login() {
        const params = new URLSearchParams({
            response_type: "code",
            client_id: CLIENT_ID,
            redirect_uri: REDIRECT_URI,
            scope: 'read.scope write.scope'
        });
        window.location.href = `${SERVER_URL}/oauth2/authorize?${params.toString()}`;
    },

    // 2. Обмен кода на токены
    async getTokens(code: string): Promise<void> {
        // ИСПОЛЬЗУЕМ URLSearchParams вместо FormData
        const params = new URLSearchParams();
        params.append('grant_type', 'authorization_code');
        params.append('code', code);
        params.append('redirect_uri', REDIRECT_URI);
        params.append('client_id', CLIENT_ID);

        const response = await api.post<TokenResponse>('/oauth2/token', params, {
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
                'Authorization': AUTH_HEADER
            }
        });

        console.log("Tokens received:", response.data);
        window.sessionStorage.setItem(ACCESS_TOKEN_KEY, response.data.access_token);
    },

    // 3. Интроспекция (получение инфо о токене)
    async getTokenInfo() {
        const token = window.sessionStorage.getItem(ACCESS_TOKEN_KEY);
        if (!token) return null;

        // ИСПОЛЬЗУЕМ URLSearchParams
        const params = new URLSearchParams();
        params.append('token', token);

        return api.post('/oauth2/token-info', params, {
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded', // Важный заголовок
                'Authorization': AUTH_HEADER
            }
        });
    },

    isAuthenticated(): boolean {
        return !!window.sessionStorage.getItem(ACCESS_TOKEN_KEY);
    },

    logout() {
        window.sessionStorage.removeItem(ACCESS_TOKEN_KEY);
        window.location.href = '/login';
    }
};