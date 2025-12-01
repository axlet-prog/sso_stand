export interface TokenResponse {
    access_token: string;
    refresh_token?: string;
    scope: string;
    id_token?: string;
    token_type: string;
    expires_in: number;
}

export interface TokenInfo {
    // Структура зависит от того, что возвращает ваш endpoint /oauth2/token-info
    // Обычно это claims токена
    active: boolean;
    sub?: string;
    aud?: string[];

    [key: string]: any;
}