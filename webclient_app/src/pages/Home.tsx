import React, {useEffect, useState} from 'react';
import {useNavigate} from 'react-router-dom';
import {AuthService} from '../services/AuthService';

const Home: React.FC = () => {
    const [tokenInfo, setTokenInfo] = useState<any>(null);
    const navigate = useNavigate();

    useEffect(() => {
        if (!AuthService.isAuthenticated()) {
            navigate('/login');
            return;
        }

        // Загружаем инфо о токене
        AuthService.getTokenInfo()
            .then(response => {
                setTokenInfo(response?.data);
            })
            .catch(err => {
                console.error(err);
                // Если токен протух - разлогиниваем
                // AuthService.logout();
                alert("Ошибка! Смотри консоль F12");
            });
    }, [navigate]);

    return (
        <div style={{padding: '20px'}}>
            <h1>Home Page</h1>
            <p>You are authorized!</p>
            <button onClick={() => AuthService.logout()}>Logout</button>

            <h3>Token Info (Introspection):</h3>
            <pre style={{background: '#f4f4f4', padding: '10px'}}>
                {tokenInfo ? JSON.stringify(tokenInfo, null, 2) : "Loading..."}
            </pre>

            <h3>Raw Access Token:</h3>
            <div style={{wordBreak: 'break-all', fontSize: '12px', color: '#555'}}>
                {window.sessionStorage.getItem("access_token")}
            </div>
        </div>
    );
};

export default Home;