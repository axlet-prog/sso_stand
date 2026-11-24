import {defineConfig} from 'vite'
import react from '@vitejs/plugin-react'

// https://vite.dev/config/
export default defineConfig({
    plugins: [react()],
    server: {
        port: 8080, // Устанавливаем порт
        strictPort: true, // (Опционально) Vite упадет с ошибкой, если порт 8080 занят, вместо переключения на 8081
    }
})
