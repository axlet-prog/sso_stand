import axios from 'axios';

class AuthService {
    // В Vite переменные окружения доступны через import.meta.env
    // И они должны начинаться с VITE_
    LOCATION_HEADER = import.meta.env.VITE_APP_SSO_LOCATION_HEADER;
    LOGIN_URL = "/login";
    OAUTH_AUTHORIZATION_URL = "/oauth2/authorization/";

    /**
     * Вход через логин/пароль.
     */
    async login(username, password) {
        // Spring Security по умолчанию ожидает form-data или x-www-form-urlencoded
        const formData = new FormData();
        formData.append("username", username);
        formData.append("password", password);

        try {
            const response = await axios.post(this.LOGIN_URL, formData);

            // Axios возвращает заголовки в нижнем регистре
            const headerName = this.LOCATION_HEADER ? this.LOCATION_HEADER.toLowerCase() : null;

            if (headerName && response.headers[headerName]) {
                // Если заголовок есть — делаем жесткий редирект браузера
                window.location.href = response.headers[headerName];
            } else {
                // Если заголовка нет, но статус 200 - возможно мы уже на главной или логика другая
                // Можно сделать редирект на корень
                window.location.href = "/";
            }
        } catch (error) {
            throw error; // Пробрасываем ошибку в компонент, чтобы показать "Неверный логин/пароль"
        }
    }

    /**
     * Вход через соцсети
     */
    loginWith(providerName) {
        window.location.href = `${this.OAUTH_AUTHORIZATION_URL}${providerName}`;
    }
}

export default new AuthService();