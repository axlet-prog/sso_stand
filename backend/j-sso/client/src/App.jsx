import React from 'react';

import {Route, Routes} from 'react-router-dom';
import LoginPage from './pages/LoginPage';

function App() {
    return (
        <Routes>
            {/* Этот маршрут должен совпадать с тем, что отдает Spring Controller,
           либо с корневым путем, если вы делаете SPA */}
            <Route path="/ui-test" element={<LoginPage/>}/>
            <Route path="/login" element={<LoginPage/>}/>

            {/* Другие маршруты */}
        </Routes>
    );
}

export default App
