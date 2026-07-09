import { BrowserRouter, Routes, Route } from 'react-router-dom'
import Layout from './components/layout/layout.jsx'
import Home from './pages/home_page.jsx'
import NotFoundPage from './pages/not_found.jsx'
import ArtistsPage from './pages/artists_page.jsx'
import GenresPage from './pages/genres_page.jsx'
import SongsPage from './pages/songs_page.jsx'
import SettingsPage from './pages/settings_page.jsx'

function App() {
    return (
        <BrowserRouter>
            <Routes>
                <Route element={<Layout />}>
                    <Route index element={<Home />} />
                    <Route path="artists" element={<ArtistsPage />} />
                    <Route path="genres" element={<GenresPage />} />
                    <Route path="songs" element={<SongsPage />} />
                    <Route path="settings" element={<SettingsPage />} />
                    <Route path="*" element={<NotFoundPage />} />
                </Route>
            </Routes>
        </BrowserRouter>
    )
}

export default App