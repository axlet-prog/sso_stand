import React from 'react';
import {AuthService} from '../services/AuthService';

const Login: React.FC = () => {
    const handleLogin = () => {
        AuthService.login();
    };

    return (
        <div style={{display: 'flex', justifyContent: 'center', marginTop: '50px'}}>
            <div style={{textAlign: 'center'}}>
                <h1>Login Page</h1>
                <p>Welcome to Test Client</p>
                <button onClick={handleLogin} style={{padding: '10px 20px', cursor: 'pointer'}}>
                    Login via J-SSO
                </button>
            </div>
        </div>
    );
};

export default Login;