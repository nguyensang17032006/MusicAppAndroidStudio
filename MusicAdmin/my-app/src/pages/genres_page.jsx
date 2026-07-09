import React, { useState, useEffect, useMemo } from 'react';
import { Button } from "@/components/ui/button.jsx";
import { RotateCcw } from "lucide-react";
import GenreForm from "../components/genres/genre_form.jsx";
import GenreTable from "../components/genres/genre_table.jsx";

// Fallback Seed Data
const SEED_GENRES = [
    { id: "G001", name: "Pop-Ballad" },
    { id: "G002", name: "Indie-Pop" },
    { id: "G003", name: "Hip-Hop" }
];

// Backend API URL Base
const API_URL = 'http://localhost:3000/api/genres';

// Helper to generate next ID
const generateGenreId = (genresList) => {
    if (!genresList || genresList.length === 0) return 'G001';
    const ids = genresList
        .map(g => {
            const match = g.id.match(/^G(\d+)$/);
            return match ? parseInt(match[1], 10) : 0;
        })
        .filter(n => n > 0);
    const maxId = ids.length > 0 ? Math.max(...ids) : 0;
    const nextNum = maxId + 1;
    return `G${String(nextNum).padStart(3, '0')}`;
};

export default function GenresPage() {
    // Coordinate States
    const [genres, setGenres] = useState([]);
    const [editingGenre, setEditingGenre] = useState(null);
    const [isLoading, setIsLoading] = useState(false);
    const [errorMsg, setErrorMsg] = useState('');
    const [successMsg, setSuccessMsg] = useState('');

    // Fetch genres on component mount
    const fetchGenres = async () => {
        setIsLoading(true);
        try {
            console.log(`fetching genres from backend: ${API_URL}`);
            const res = await fetch(API_URL);
            if (!res.ok) throw new Error('API server returned error code');
            
            const data = await res.json();
            setGenres(data);
        } catch (error) {
            console.warn("Backend offline or connection failed. Falling back to local storage.", error);
            // Local storage fallback
            const stored = localStorage.getItem('music_admin_genres');
            if (stored) {
                try {
                    setGenres(JSON.parse(stored));
                } catch (e) {
                    setGenres(SEED_GENRES);
                }
            } else {
                setGenres(SEED_GENRES);
            }
        } finally {
            setIsLoading(false);
        }
    };

    useEffect(() => {
        fetchGenres();
    }, []);

    // Sync to localStorage as fallback whenever genres state changes
    useEffect(() => {
        if (genres.length > 0) {
            localStorage.setItem('music_admin_genres', JSON.stringify(genres));
        }
    }, [genres]);

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

    // Calculate next generated ID
    const nextId = useMemo(() => {
        return generateGenreId(genres);
    }, [genres]);

    // Form Submission: POST /api/genres
    const handleFormSubmit = async (genreData) => {
        setIsLoading(true);
        setErrorMsg('');
        setSuccessMsg('');

        try {
            const res = await fetch(API_URL, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(genreData) // Send as regular JSON
            });

            if (!res.ok) {
                const errData = await res.json().catch(() => ({}));
                throw new Error(errData.error || 'Server rejected genre registration payload.');
            }

            const result = await res.json();
            setSuccessMsg(result.message || 'Saved successfully to MySQL database!');
            setEditingGenre(null);
            
            // Reload updated database lists
            await fetchGenres();
        } catch (error) {
            console.error("API submission failed. Operating in fallback offline mode.", error);
            setErrorMsg(`Backend connection failed: ${error.message}. Saved locally instead.`);

            // Local fallback backup
            if (editingGenre) {
                // Update local state
                setGenres(prev => prev.map(g =>
                    g.id === editingGenre.id
                        ? { ...g, name: genreData.name }
                        : g
                ));
                setEditingGenre(null);
            } else {
                // Add local state
                const newGenre = {
                    id: genreData.id,
                    name: genreData.name
                };
                setGenres(prev => [...prev, newGenre]);
            }
        } finally {
            setIsLoading(false);
        }
    };

    // Deletion: DELETE /api/genres/:id
    const handleDeleteGenre = async (id) => {
        if (!window.confirm(`Are you sure you want to delete genre ${id}?`)) return;

        setIsLoading(true);
        setErrorMsg('');
        setSuccessMsg('');

        try {
            const res = await fetch(`${API_URL}/${id}`, {
                method: 'DELETE'
            });

            if (!res.ok) {
                const errData = await res.json().catch(() => ({}));
                throw new Error(errData.error || 'Server rejected genre deletion request.');
            }

            setSuccessMsg(`Deleted genre ${id} successfully.`);
            if (editingGenre && editingGenre.id === id) {
                setEditingGenre(null);
            }
            
            // Refresh
            await fetchGenres();
        } catch (error) {
            console.error("API deletion failed. Falling back to local delete.", error);
            setErrorMsg(`Backend connection failed. Removed locally instead.`);

            // Local fallback backup
            setGenres(prev => prev.filter(g => g.id !== id));
            if (editingGenre && editingGenre.id === id) {
                setEditingGenre(null);
            }
        } finally {
            setIsLoading(false);
        }
    };

    const handleResetToSeeds = () => {
        if (window.confirm('Reset local database list to default seed data? All custom additions will be lost.')) {
            setGenres(SEED_GENRES);
            setEditingGenre(null);
            localStorage.setItem('music_admin_genres', JSON.stringify(SEED_GENRES));
            setSuccessMsg('Reset local registry records to default values.');
        }
    };

    return (
        <div className="space-y-6">
            {/* Header info */}
            <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
                <div>
                    <h1 className="text-2xl font-bold tracking-tight text-foreground sm:text-3xl">Genres Directory</h1>
                    <p className="text-muted-foreground text-sm mt-1">Manage musical genres, categorize tracks, and expand definitions.</p>
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
                    <GenreForm
                        editingGenre={editingGenre}
                        nextId={nextId}
                        onSubmit={handleFormSubmit}
                        onCancel={() => setEditingGenre(null)}
                        isLoading={isLoading}
                    />
                </div>

                {/* Table / List Card (2 cols on lg) */}
                <div className="lg:col-span-2">
                    <GenreTable
                        genres={genres}
                        onEdit={(genre) => setEditingGenre(genre)}
                        onDelete={handleDeleteGenre}
                        editingId={editingGenre ? editingGenre.id : null}
                    />
                </div>

            </div>
        </div>
    );
}