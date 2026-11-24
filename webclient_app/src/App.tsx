import {BrowserRouter, Navigate, Route, Routes} from 'react-router-dom';
import Login from './pages/Login';
import Home from './pages/Home';
import Callback from './pages/Callback';

function App() {
    return (
        <BrowserRouter>
            <Routes>
                <Route path="/login" element={<Login/>}/>
                {/* Обработчик редиректа от Auth Server */}
                <Route path="/code" element={<Callback/>}/>

                {/* Главная страница */}
                <Route path="/" element={<Home/>}/>

                {/* Любой другой путь редиректим на Home (или Login) */}
                <Route path="*" element={<Navigate to="/" replace/>}/>
            </Routes>
        </BrowserRouter>
    );
}

export default App;