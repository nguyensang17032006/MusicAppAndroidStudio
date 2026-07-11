import React, { useState, useEffect, useMemo } from 'react';
import { Button } from "@/components/ui/button.jsx";
import { RotateCcw } from "lucide-react";
import ArtistForm from "../components/artists/artist_form.jsx";
import ArtistTable from "../components/artists/artist_table.jsx";

// Backend API URL Base
const API_BASE = import.meta.env.VITE_API_URL || 'http://localhost:3000';
const API_URL = `${API_BASE}/api/artists`;

// Helper to generate next ID
const generateArtistId = (artistsList) => {
    if (!artistsList || !Array.isArray(artistsList) || artistsList.length === 0) return 'A001';
    const ids = artistsList
        .map(a => {
            if (!a || !a.id) return 0;
            const match = String(a.id).match(/\d+/);
            return match ? parseInt(match[0], 10) : 0;
        })
        .filter(n => n > 0);
    const maxId = ids.length > 0 ? Math.max(...ids) : 0;
    const nextNum = maxId + 1;
    return `A${String(nextNum).padStart(3, '0')}`;
};

export default function ArtistsPage() {
    // Coordinate States
    const [artists, setArtists] = useState([]);
    const [editingArtist, setEditingArtist] = useState(null);
    const [isLoading, setIsLoading] = useState(false);
    const [errorMsg, setErrorMsg] = useState('');
    const [successMsg, setSuccessMsg] = useState('');

    // Fetch artists on component mount
    const fetchArtists = async () => {
        setIsLoading(true);
        try {
            console.log(`fetching artists from backend: ${API_URL}`);
            const res = await fetch(API_URL);
            if (!res.ok) throw new Error('API server returned error code');

            const data = await res.json();
            const list = Array.isArray(data) ? data : (data && Array.isArray(data.data) ? data.data : []);
            setArtists(list);
        } catch (error) {
            console.warn("Backend offline or connection failed. Falling back to local storage.", error);
            // Local storage fallback
            const stored = localStorage.getItem('music_admin_artists');
            if (stored) {
                try {
                    setArtists(JSON.parse(stored));
                } catch (e) {
                    setArtists([]);
                }
            } else {
                setArtists([]);
            }
        } finally {
            setIsLoading(false);
        }
    };

    useEffect(() => {
        fetchArtists();
    }, []);

    // Sync to localStorage as a fallback backup whenever artists state changes
    useEffect(() => {
        if (artists.length > 0) {
            localStorage.setItem('music_admin_artists', JSON.stringify(artists));
        }
    }, [artists]);

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

    // Calculate next generation ID for Add Form
    const nextId = useMemo(() => {
        return generateArtistId(artists);
    }, [artists]);

    // Form Submission: POST /api/artists
    const handleFormSubmit = async (formDataToSend, localBackupData) => {
        setIsLoading(true);
        setErrorMsg('');
        setSuccessMsg('');

        try {
            const res = await fetch(API_URL, {
                method: 'POST',
                body: formDataToSend // FormData binary file transmission
            });

            if (!res.ok) {
                const errData = await res.json().catch(() => ({}));
                throw new Error(errData.error || 'Server rejected artist registration payload.');
            }

            const result = await res.json();
            setSuccessMsg(result.message || 'Saved successfully to MySQL database!');
            setEditingArtist(null);

            // Reload updated database lists
            await fetchArtists();
        } catch (error) {
            console.error("API submission failed. Operating in fallback offline mode.", error);
            setErrorMsg(`Backend connection failed: ${error.message}. Saved locally instead.`);

            // Local fallback backup
            const finalAvatarUrl = localBackupData.avatarPreview || (editingArtist ? editingArtist.avatar_url : '');

            if (editingArtist) {
                // Update local state
                setArtists(prev => prev.map(a =>
                    a.id === editingArtist.id
                        ? { ...a, name: localBackupData.name, avatar_url: finalAvatarUrl, bio: localBackupData.bio }
                        : a
                ));
                setEditingArtist(null);
            } else {
                // Add local state
                const newArtist = {
                    id: localBackupData.id,
                    name: localBackupData.name,
                    avatar_url: finalAvatarUrl,
                    bio: localBackupData.bio
                };
                setArtists(prev => [...prev, newArtist]);
            }
        } finally {
            setIsLoading(false);
        }
    };

    // Deletion: DELETE /api/artists/:id
    const handleDeleteArtist = async (id) => {
        if (!window.confirm(`Are you sure you want to delete artist ${id}?`)) return;

        setIsLoading(true);
        setErrorMsg('');
        setSuccessMsg('');

        try {
            const res = await fetch(`${API_URL}/${id}`, {
                method: 'DELETE'
            });

            if (!res.ok) {
                const errData = await res.json().catch(() => ({}));
                throw new Error(errData.error || 'Server rejected artist deletion request.');
            }

            setSuccessMsg(`Deleted artist ${id} successfully.`);
            if (editingArtist && editingArtist.id === id) {
                setEditingArtist(null);
            }

            // Refresh
            await fetchArtists();
        } catch (error) {
            console.error("API deletion failed. Falling back to local delete.", error);
            setErrorMsg(`Backend connection failed. Removed locally instead.`);

            // Local fallback backup
            setArtists(prev => prev.filter(a => a.id !== id));
            if (editingArtist && editingArtist.id === id) {
                setEditingArtist(null);
            }
        } finally {
            setIsLoading(false);
        }
    };

    const handleResetToSeeds = () => {
        if (window.confirm('Reset local database list to default seed data? All custom additions will be lost.')) {
            setArtists([]);
            setEditingArtist(null);
            localStorage.setItem('music_admin_artists', JSON.stringify([]));
            setSuccessMsg('Reset local registry records to default values.');
        }
    };

    return (
        <div className="space-y-6">
            {/* Header info */}
            <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
                <div>
                    <h1 className="text-2xl font-bold tracking-tight text-foreground sm:text-3xl">Artists Directory</h1>
                    <p className="text-muted-foreground text-sm mt-1">Manage music creators, edit bios, upload avatars, and curate profiles.</p>
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
                    <ArtistForm
                        editingArtist={editingArtist}
                        nextId={nextId}
                        onSubmit={handleFormSubmit}
                        onCancel={() => setEditingArtist(null)}
                        isLoading={isLoading}
                    />
                </div>

                {/* Table / List Card (2 cols on lg) */}
                <div className="lg:col-span-2">
                    <ArtistTable
                        artists={artists}
                        onEdit={(artist) => setEditingArtist(artist)}
                        onDelete={handleDeleteArtist}
                        editingId={editingArtist ? editingArtist.id : null}
                    />
                </div>

            </div>
        </div>
    );
}