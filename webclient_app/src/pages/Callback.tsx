import React, {useEffect, useRef} from 'react';
import {useNavigate, useSearchParams} from 'react-router-dom';
import {AuthService} from '../services/AuthService';

const Callback: React.FC = () => {
    const [searchParams] = useSearchParams();
    const navigate = useNavigate();

    // This ref prevents the double-call in React Strict Mode
    const called = useRef(false);

    useEffect(() => {
        const code = searchParams.get('code');

        if (code && !called.current) {
            called.current = true; // Mark as called immediately

            AuthService.getTokens(code)
                .then(() => {
                    navigate('/'); // Success -> Go Home
                })
                .catch((error) => {
                    console.error("Token exchange failed:", error);
                    // Do NOT redirect to login immediately while debugging
                    // navigate('/login');
                    alert("Error exchanging token. Check console.");
                });
        }
    }, [searchParams, navigate]);

    return <div>Processing login...</div>;
};

export default Callback;