import React, { useState, useEffect, useMemo } from 'react';
import { Button } from "@/components/ui/button.jsx";
import { RotateCcw } from "lucide-react";
import SongForm from "../components/songs/song_form.jsx";
import SongTable from "../components/songs/song_table.jsx";

// Initial seed values for offline fallback
const SEED_SONGS = [
    {
        id: "S001",
        title: "Lạc Trôi",
        file_url: "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
        cover_url: "https://upload.wikimedia.org/wikipedia/vi/a/a2/Lactroi.jpg",
        duration: 292,
        views: 120450,
        artists: [
            { artist_id: "A001", name: "Sơn Tùng M-TP", is_main_artist: true }
        ],
        genres: [
            { genre_id: "G001", name: "Pop-Ballad" }
        ],
        created_at: new Date().toISOString()
    },
    {
        id: "S002",
        title: "Trốn Tìm",
        file_url: "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3",
        cover_url: "https://upload.wikimedia.org/wikipedia/vi/c/c8/Tr%E1%BB%91n_T%C3%ACm_-_Rapper_%C4%90en_V%C3%A2u.png",
        duration: 250,
        views: 89400,
        artists: [
            { artist_id: "A002", name: "Đen Vâu", is_main_artist: true }
        ],
        genres: [
            { genre_id: "G002", name: "Indie-Pop" }
        ],
        created_at: new Date().toISOString()
    }
];

const API_SONGS_URL = 'http://localhost:3000/api/songs';
const API_ARTISTS_URL = 'http://localhost:3000/api/artists';
const API_GENRES_URL = 'http://localhost:3000/api/genres';

// Helper to generate next ID
const generateSongId = (songsList) => {
    if (!songsList || !Array.isArray(songsList) || songsList.length === 0) return 'S001';
    const ids = songsList
        .map(s => {
            if (!s || !s.id) return 0;
            const match = String(s.id).match(/\d+/);
            return match ? parseInt(match[0], 10) : 0;
        })
        .filter(n => n > 0);
    const maxId = ids.length > 0 ? Math.max(...ids) : 0;
    const nextNum = maxId + 1;
    return `S${String(nextNum).padStart(3, '0')}`;
};

export default function SongsPage() {
    const [songs, setSongs] = useState([]);
    const [artists, setArtists] = useState([]); // Loaded from MySQL to populate selectors
    const [genres, setGenres] = useState([]);   // Loaded from MySQL to populate selectors
    
    const [editingSong, setEditingSong] = useState(null);
    const [isLoading, setIsLoading] = useState(false);
    const [errorMsg, setErrorMsg] = useState('');
    const [successMsg, setSuccessMsg] = useState('');

    // Fetch master registries and songs from backend
    const fetchData = async () => {
        setIsLoading(true);
        setErrorMsg('');
        
        try {
            // 1. Fetch Artists list
            console.log(`Loading artists from ${API_ARTISTS_URL}`);
            const artRes = await fetch(API_ARTISTS_URL).catch(() => null);
            if (artRes && artRes.ok) {
                const artData = await artRes.json();
                const artList = Array.isArray(artData) ? artData : (artData && Array.isArray(artData.data) ? artData.data : []);
                setArtists(artList);
            } else {
                throw new Error("Artists API offline");
            }

            // 2. Fetch Genres list
            console.log(`Loading genres from ${API_GENRES_URL}`);
            const genRes = await fetch(API_GENRES_URL).catch(() => null);
            if (genRes && genRes.ok) {
                const genData = await genRes.json();
                const genList = Array.isArray(genData) ? genData : (genData && Array.isArray(genData.data) ? genData.data : []);
                setGenres(genList);
            } else {
                throw new Error("Genres API offline");
            }

            // 3. Fetch Songs list
            console.log(`Loading songs from ${API_SONGS_URL}`);
            const songRes = await fetch(API_SONGS_URL);
            if (!songRes.ok) throw new Error("Songs API returned error");
            const songData = await songRes.json();
            const songList = Array.isArray(songData) ? songData : (songData && Array.isArray(songData.data) ? songData.data : []);
            setSongs(songList);

        } catch (error) {
            console.warn("Backend offline. Loading local localStorage backups instead.", error);
            
            // Fallback load artists
            const localArtists = localStorage.getItem('music_admin_artists');
            setArtists(localArtists ? JSON.parse(localArtists) : []);

            // Fallback load genres
            const localGenres = localStorage.getItem('music_admin_genres');
            setGenres(localGenres ? JSON.parse(localGenres) : []);

            // Fallback load songs
            const localSongs = localStorage.getItem('music_admin_songs');
            if (localSongs) {
                try {
                    setSongs(JSON.parse(localSongs));
                } catch(e) {
                    setSongs(SEED_SONGS);
                }
            } else {
                setSongs(SEED_SONGS);
            }
        } finally {
            setIsLoading(false);
        }
    };

    useEffect(() => {
        fetchData();
    }, []);

    // LocalStorage Backup sync
    useEffect(() => {
        if (songs.length > 0) {
            localStorage.setItem('music_admin_songs', JSON.stringify(songs));
        }
    }, [songs]);

    // Handle alert message timeouts
    useEffect(() => {
        if (successMsg) {
            const timer = setTimeout(() => setSuccessMsg(''), 3000);
            return () => clearTimeout(timer);
        }
    }, [successMsg]);

    useEffect(() => {
        if (errorMsg) {
            const timer = setTimeout(() => setErrorMsg(''), 5000);
            return () => clearTimeout(timer);
        }
    }, [errorMsg]);

    const nextId = useMemo(() => {
        return generateSongId(songs);
    }, [songs]);

    // Form Submission: POST /api/songs
    const handleFormSubmit = async (formDataToSend, localBackupData) => {
        setIsLoading(true);
        setErrorMsg('');
        setSuccessMsg('');

        try {
            const res = await fetch(API_SONGS_URL, {
                method: 'POST',
                body: formDataToSend // FormData binary file transmission
            });

            if (!res.ok) {
                const errData = await res.json().catch(() => ({}));
                throw new Error(errData.error || 'Server rejected song upload payload.');
            }

            const result = await res.json();
            setSuccessMsg(result.message || 'Saved successfully to MySQL database!');
            setEditingSong(null);
            
            // Reload updated database lists
            await fetchData();
        } catch (error) {
            console.error("API submission failed. Operating in fallback offline mode.", error);
            setErrorMsg(`Backend connection failed: ${error.message}. Saved locally instead.`);

            // Local fallback backup
            if (editingSong) {
                // Update local state
                setSongs(prev => prev.map(s =>
                    s.id === editingSong.id
                        ? { 
                            ...s, 
                            title: localBackupData.title, 
                            duration: localBackupData.duration,
                            cover_url: localBackupData.cover_url || s.cover_url,
                            file_url: localBackupData.file_url || s.file_url,
                            artists: localBackupData.artists,
                            genres: localBackupData.genres
                          }
                        : s
                ));
                setEditingSong(null);
            } else {
                // Add local state
                const newSong = {
                    id: localBackupData.id,
                    title: localBackupData.title,
                    duration: localBackupData.duration,
                    cover_url: localBackupData.cover_url,
                    file_url: localBackupData.file_url,
                    artists: localBackupData.artists,
                    genres: localBackupData.genres,
                    views: 0,
                    created_at: new Date().toISOString()
                };
                setSongs(prev => [...prev, newSong]);
            }
        } finally {
            setIsLoading(false);
        }
    };

    // Deletion: DELETE /api/songs/:id
    const handleDeleteSong = async (id) => {
        if (!window.confirm(`Are you sure you want to delete song ${id}?`)) return;

        setIsLoading(true);
        setErrorMsg('');
        setSuccessMsg('');

        try {
            const res = await fetch(`${API_SONGS_URL}/${id}`, {
                method: 'DELETE'
            });

            if (!res.ok) {
                const errData = await res.json().catch(() => ({}));
                throw new Error(errData.error || 'Server rejected song deletion request.');
            }

            setSuccessMsg(`Deleted song ${id} successfully.`);
            if (editingSong && editingSong.id === id) {
                setEditingSong(null);
            }
            
            // Refresh
            await fetchData();
        } catch (error) {
            console.error("API deletion failed. Falling back to local delete.", error);
            setErrorMsg(`Backend connection failed. Removed locally instead.`);

            // Local fallback backup
            setSongs(prev => prev.filter(s => s.id !== id));
            if (editingSong && editingSong.id === id) {
                setEditingSong(null);
            }
        } finally {
            setIsLoading(false);
        }
    };

    const handleResetToSeeds = () => {
        if (window.confirm('Reset local database list to default seed data? All custom additions will be lost.')) {
            setSongs(SEED_SONGS);
            setEditingSong(null);
            localStorage.setItem('music_admin_songs', JSON.stringify(SEED_SONGS));
            setSuccessMsg('Reset local registry records to default values.');
        }
    };

    return (
        <div className="space-y-6">
            {/* Header info */}
            <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
                <div>
                    <h1 className="text-2xl font-bold tracking-tight text-foreground sm:text-3xl">Songs Directory</h1>
                    <p className="text-muted-foreground text-sm mt-1">Upload tracks, manage co-authors, link genres, and examine stats.</p>
                </div>
                <Button
                    variant="outline"
                    size="sm"
                    onClick={handleResetToSeeds}
                    className="flex items-center gap-2 border-dashed border-primary/40 hover:border-primary text-xs h-9"
                >
                    <RotateCcw size={14} />
                    Reset Defaults
                </Button>
            </div>

            {/* Notification messages */}
            {successMsg && (
                <div className="p-4 bg-emerald-50 text-emerald-800 dark:bg-emerald-950/30 dark:text-emerald-400 border border-emerald-200 dark:border-emerald-900/50 rounded-2xl text-sm font-medium animate-in fade-in slide-in-from-top-2 duration-300">
                    {successMsg}
                </div>
            )}
            {errorMsg && (
                <div className="p-4 bg-rose-50 text-rose-800 dark:bg-rose-950/30 dark:text-rose-400 border border-rose-200 dark:border-rose-900/50 rounded-2xl text-sm font-medium animate-in fade-in slide-in-from-top-2 duration-300">
                    {errorMsg}
                </div>
            )}

            {/* Layout Grid */}
            <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
                
                {/* Form Card (1 col on lg) */}
                <div className="lg:col-span-1">
                    <SongForm
                        editingSong={editingSong}
                        nextId={nextId}
                        artists={artists}
                        genres={genres}
                        onSubmit={handleFormSubmit}
                        onCancel={() => setEditingSong(null)}
                        isLoading={isLoading}
                    />
                </div>

                {/* Table / List Card (2 cols on lg) */}
                <div className="lg:col-span-2">
                    <SongTable
                        songs={songs}
                        onEdit={(song) => setEditingSong(song)}
                        onDelete={handleDeleteSong}
                        editingId={editingSong ? editingSong.id : null}
                    />
                </div>

            </div>
        </div>
    );
}