import {defineConfig, loadEnv} from 'vite'
import react from '@vitejs/plugin-react-swc'

// https://vite.dev/config/
export default defineConfig(({command, mode}) => {
    const env = loadEnv(mode, process.cwd(), '')

    return {
        plugins: [react()],

        // Вот здесь мы подставляем значение из .env файла
        // Если переменной нет, по умолчанию ставим '/'
        base: env.VITE_APP_BASE_PATH || '/',

        build: {
            outDir: 'dist',
        }
    }
})
