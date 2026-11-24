import React, {useState} from 'react';
import AuthService from '../services/AuthService';
// Если у вас есть CSS модуль или глобальные стили, импортируйте их
// import './LoginPage.css';

const LoginPage = () => {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState(null);
    const [isLoading, setIsLoading] = useState(false);

    const handleLogin = async (e) => {
        e.preventDefault();
        setError(null);
        setIsLoading(true);

        try {
            await AuthService.login(username, password);
            // Редирект произойдет внутри сервиса, нам ничего делать не надо
        } catch (err) {
            console.error("Login failed", err);
            // Обработка ошибок (обычно 401 Bad Credentials)
            if (err.response && err.response.status === 401) {
                setError("Неверный логин или пароль");
            } else {
                setError("Произошла ошибка при входе. Попробуйте позже.");
            }
            setIsLoading(false);
        }
    };

    const handleSocialLogin = (provider) => {
        AuthService.loginWith(provider);
    };

    return (
        <div style={styles.container}>
            <div style={styles.card}>
                <h2>Вход в систему</h2>

                {error && <div style={styles.error}>{error}</div>}

                <form onSubmit={handleLogin} style={styles.form}>
                    <div style={styles.inputGroup}>
                        <label htmlFor="username">Логин</label>
                        <input
                            type="text"
                            id="username"
                            value={username}
                            onChange={(e) => setUsername(e.target.value)}
                            required
                            style={styles.input}
                        />
                    </div>

                    <div style={styles.inputGroup}>
                        <label htmlFor="password">Пароль</label>
                        <input
                            type="password"
                            id="password"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            required
                            style={styles.input}
                        />
                    </div>

                    <button type="submit" disabled={isLoading} style={styles.button}>
                        {isLoading ? 'Вход...' : 'Войти'}
                    </button>
                </form>

                <div style={styles.divider}>или</div>

                <div style={styles.socialButtons}>
                    <button onClick={() => handleSocialLogin('google')} style={{...styles.socialBtn, ...styles.google}}>
                        Google
                    </button>
                    <button onClick={() => handleSocialLogin('github')} style={{...styles.socialBtn, ...styles.github}}>
                        GitHub
                    </button>
                    <button onClick={() => handleSocialLogin('yandex')} style={{...styles.socialBtn, ...styles.yandex}}>
                        Yandex
                    </button>
                </div>
            </div>
        </div>
    );
};

// Простые инлайн-стили для примера (лучше вынести в CSS)
const styles = {
    container: {
        display: 'flex',
        justifyContent: 'center',
        alignItems: 'center',
        height: '100vh',
        backgroundColor: '#f0f2f5'
    },
    card: {
        background: 'white',
        padding: '2rem',
        borderRadius: '8px',
        boxShadow: '0 4px 6px rgba(0,0,0,0.1)',
        width: '100%',
        maxWidth: '400px',
        textAlign: 'center'
    },
    error: {
        color: 'red',
        marginBottom: '1rem',
        fontSize: '0.9rem'
    },
    form: {
        display: 'flex',
        flexDirection: 'column',
        gap: '1rem'
    },
    inputGroup: {
        textAlign: 'left'
    },
    input: {
        width: '100%',
        padding: '0.5rem',
        marginTop: '0.25rem',
        borderRadius: '4px',
        border: '1px solid #ccc',
        boxSizing: 'border-box'
    },
    button: {
        padding: '0.75rem',
        backgroundColor: '#007bff',
        color: 'white',
        border: 'none',
        borderRadius: '4px',
        cursor: 'pointer',
        fontSize: '1rem'
    },
    divider: {
        margin: '1.5rem 0',
        color: '#666'
    },
    socialButtons: {
        display: 'flex',
        flexDirection: 'column',
        gap: '0.5rem'
    },
    socialBtn: {
        padding: '0.5rem',
        border: 'none',
        borderRadius: '4px',
        cursor: 'pointer',
        color: 'white',
        fontWeight: 'bold'
    },
    google: {backgroundColor: '#db4437'},
    github: {backgroundColor: '#333'},
    yandex: {backgroundColor: '#fc3f1d'}
};

export default LoginPage;