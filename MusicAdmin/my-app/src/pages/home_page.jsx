import React, { useState, useEffect, useMemo } from 'react';
import { Link } from 'react-router-dom';
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from "@/components/ui/card.jsx";
import {
    Music,
    Users,
    Tags,
    Play,
    PlusCircle,
    ArrowRight,
    UserPlus,
    FileMusic,
    ListMusic,
    Heart,
    Activity,
    Sparkles
} from "lucide-react";

export default function Home() {
    const [stats, setStats] = useState({
        counts: {
            songs: 0,
            artists: 0,
            genres: 0,
            playlists: 0,
            users: 0,
            views: 0,
            interactions: 0,
            likes: 0
        },
        trendingSongs: [],
        popularGenres: [],
        recentUsers: []
    });

    const [isFallback, setIsFallback] = useState(false);
    const [isLoading, setIsLoading] = useState(true);
    const [adminName, setAdminName] = useState('Sáng');

    useEffect(() => {
        const storedName = localStorage.getItem('music_admin_name');
        if (storedName) setAdminName(storedName);

        const fetchDashboardData = async () => {
            setIsLoading(true);
            try {
                const API_BASE = import.meta.env.VITE_API_URL || 'http://localhost:3000';
                const response = await fetch(`${API_BASE}/api/stats/dashboard`);
                if (!response.ok) {
                    throw new Error('Backend returned error status');
                }
                const resJson = await response.json();
                if (resJson.success && resJson.data) {
                    setStats(resJson.data);
                    setIsFallback(false);
                } else {
                    throw new Error('API format incorrect');
                }
            } catch (err) {
                console.warn("Backend statistics API offline. Falling back to cache/mock values.", err);
                setIsFallback(true);

                // Mock Fallbacks in case backend is offline & localStorage is empty
                const MOCK_SONGS = [
                    {
                        id: "S001",
                        title: "Lạc Trôi",
                        views: 120450,
                        cover_url: "https://upload.wikimedia.org/wikipedia/vi/a/a2/Lactroi.jpg",
                        artists: [{ name: "Sơn Tùng M-TP", is_main_artist: true }]
                    },
                    {
                        id: "S002",
                        title: "Trốn Tìm",
                        views: 89400,
                        cover_url: "https://upload.wikimedia.org/wikipedia/vi/c/c8/Tr%E1%BB%91n_T%C3%ACm_-_Rapper_%C4%90en_V%C3%A2u.png",
                        artists: [{ name: "Đen Vâu", is_main_artist: true }]
                    }
                ];

                const MOCK_GENRES = [
                    { id: "G001", name: "Pop-Ballad", song_count: 12 },
                    { id: "G002", name: "Indie-Pop", song_count: 8 },
                    { id: "G003", name: "Hip-Hop", song_count: 5 }
                ];

                const MOCK_USERS = [
                    { id: "U001", email: "sangsang17@student.hcmute.edu.vn", gender: "Male", created_at: new Date().toISOString() },
                    { id: "U002", email: "nguyensang@gmail.com", gender: "Male", created_at: new Date().toISOString() },
                    { id: "U003", email: "musicfan@yahoo.com", gender: "Female", created_at: new Date().toISOString() }
                ];

                // Fallback calculations using localStorage/seed databases
                const storedSongs = localStorage.getItem('music_admin_songs');
                const storedArtists = localStorage.getItem('music_admin_artists');
                const storedGenres = localStorage.getItem('music_admin_genres');

                const loadedSongs = storedSongs ? JSON.parse(storedSongs) : MOCK_SONGS;
                const loadedArtists = storedArtists ? JSON.parse(storedArtists) : [{ id: "A001" }, { id: "A002" }];
                const loadedGenres = storedGenres ? JSON.parse(storedGenres) : MOCK_GENRES;

                const totalViews = loadedSongs.reduce((acc, s) => acc + (parseInt(s.views, 10) || 0), 0);

                setStats({
                    counts: {
                        songs: loadedSongs.length,
                        artists: loadedArtists.length,
                        genres: loadedGenres.length,
                        playlists: 3,
                        users: MOCK_USERS.length,
                        views: totalViews,
                        interactions: 154,
                        likes: 42
                    },
                    trendingSongs: loadedSongs.slice(0, 5),
                    popularGenres: loadedGenres,
                    recentUsers: MOCK_USERS
                });
            } finally {
                setIsLoading(false);
            }
        };

        fetchDashboardData();
    }, []);

    // Determine max views for relative progress bar calculation
    const maxViews = useMemo(() => {
        if (!stats.trendingSongs || stats.trendingSongs.length === 0) return 1;
        return Math.max(...stats.trendingSongs.map(s => s.views || 0));
    }, [stats.trendingSongs]);

    return (
        <div className="space-y-6 animate-[fadeIn_0.5s_ease-out]">
            {/* Header section */}
            <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
                <div>
                    <h1 className="text-3xl font-extrabold tracking-tight bg-gradient-to-r from-gray-900 via-gray-800 to-indigo-900 bg-clip-text text-transparent">
                        Dashboard Overview
                    </h1>
                    <p className="text-muted-foreground text-sm mt-1 flex items-center gap-1.5">
                        Welcome back, <span className="font-semibold text-gray-900">Admin • {adminName}</span>
                        <span className="w-1.5 h-1.5 rounded-full bg-emerald-500 inline-block animate-pulse" title="System Online" />
                    </p>
                </div>
            </div>

            {/* Offline Fallback Warning Notice */}
            {isFallback && (
                <div className="flex items-center gap-3 bg-amber-500/10 border border-amber-500/20 text-amber-700 dark:text-amber-400 text-xs px-4 py-3.5 rounded-3xl animate-pulse">
                    <div className="p-1 bg-amber-500/20 rounded-full shrink-0">
                        <Activity size={14} className="text-amber-600 animate-spin" />
                    </div>
                    <span>
                        <strong>Backend API Offline:</strong> Operating in local fallback mode. Displaying cached records and simulated AI interaction activities.
                    </span>
                </div>
            )}

            {/* 6 Stats Card Grid */}
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-6 gap-4">

                {/* Total Tracks */}
                <Card className="shadow-sm border border-border/80 hover:border-indigo-500/30 hover:shadow transition-all duration-300 bg-white group rounded-3xl relative overflow-hidden">
                    <div className="absolute top-0 right-0 w-24 h-24 bg-indigo-500/5 rounded-full -mr-8 -mt-8 group-hover:scale-125 transition-transform duration-500" />
                    <CardHeader className="flex flex-row items-center justify-between pb-2 space-y-0 relative">
                        <span className="text-[10px] font-bold uppercase tracking-wider text-muted-foreground">Total Tracks</span>
                        <div className="p-2 bg-indigo-50 text-indigo-600 rounded-2xl group-hover:bg-indigo-600 group-hover:text-white transition-colors duration-300">
                            <Music size={16} />
                        </div>
                    </CardHeader>
                    <CardContent className="relative">
                        <div className="text-3xl font-extrabold tracking-tight text-foreground font-mono">
                            {isLoading ? "..." : stats.counts.songs}
                        </div>
                        <p className="text-[10px] text-muted-foreground mt-1">Audio files registered</p>
                    </CardContent>
                </Card>

                {/* Total Creators */}
                <Card className="shadow-sm border border-border/80 hover:border-violet-500/30 hover:shadow transition-all duration-300 bg-white group rounded-3xl relative overflow-hidden">
                    <div className="absolute top-0 right-0 w-24 h-24 bg-violet-500/5 rounded-full -mr-8 -mt-8 group-hover:scale-125 transition-transform duration-500" />
                    <CardHeader className="flex flex-row items-center justify-between pb-2 space-y-0 relative">
                        <span className="text-[10px] font-bold uppercase tracking-wider text-muted-foreground">Creators</span>
                        <div className="p-2 bg-violet-50 text-violet-600 rounded-2xl group-hover:bg-violet-600 group-hover:text-white transition-colors duration-300">
                            <Users size={16} />
                        </div>
                    </CardHeader>
                    <CardContent className="relative">
                        <div className="text-3xl font-extrabold tracking-tight text-foreground font-mono">
                            {isLoading ? "..." : stats.counts.artists}
                        </div>
                        <p className="text-[10px] text-muted-foreground mt-1">Artists & bands catalogs</p>
                    </CardContent>
                </Card>

                {/* Total Genres */}
                <Card className="shadow-sm border border-border/80 hover:border-emerald-500/30 hover:shadow transition-all duration-300 bg-white group rounded-3xl relative overflow-hidden">
                    <div className="absolute top-0 right-0 w-24 h-24 bg-emerald-500/5 rounded-full -mr-8 -mt-8 group-hover:scale-125 transition-transform duration-500" />
                    <CardHeader className="flex flex-row items-center justify-between pb-2 space-y-0 relative">
                        <span className="text-[10px] font-bold uppercase tracking-wider text-muted-foreground">Genres</span>
                        <div className="p-2 bg-emerald-50 text-emerald-600 rounded-2xl group-hover:bg-emerald-600 group-hover:text-white transition-colors duration-300">
                            <Tags size={16} />
                        </div>
                    </CardHeader>
                    <CardContent className="relative">
                        <div className="text-3xl font-extrabold tracking-tight text-foreground font-mono">
                            {isLoading ? "..." : stats.counts.genres}
                        </div>
                        <p className="text-[10px] text-muted-foreground mt-1">Classification taxonomy</p>
                    </CardContent>
                </Card>

                {/* Active Users */}
                <Card className="shadow-sm border border-border/80 hover:border-rose-500/30 hover:shadow transition-all duration-300 bg-white group rounded-3xl relative overflow-hidden">
                    <div className="absolute top-0 right-0 w-24 h-24 bg-rose-500/5 rounded-full -mr-8 -mt-8 group-hover:scale-125 transition-transform duration-500" />
                    <CardHeader className="flex flex-row items-center justify-between pb-2 space-y-0 relative">
                        <span className="text-[10px] font-bold uppercase tracking-wider text-muted-foreground">Active Users</span>
                        <div className="p-2 bg-rose-50 text-rose-600 rounded-2xl group-hover:bg-rose-600 group-hover:text-white transition-colors duration-300">
                            <UserPlus size={16} />
                        </div>
                    </CardHeader>
                    <CardContent className="relative">
                        <div className="text-3xl font-extrabold tracking-tight text-foreground font-mono">
                            {isLoading ? "..." : stats.counts.users}
                        </div>
                        <p className="text-[10px] text-muted-foreground mt-1">Supabase authenticated</p>
                    </CardContent>
                </Card>

                {/* Playlists */}
                <Card className="shadow-sm border border-border/80 hover:border-blue-500/30 hover:shadow transition-all duration-300 bg-white group rounded-3xl relative overflow-hidden">
                    <div className="absolute top-0 right-0 w-24 h-24 bg-blue-500/5 rounded-full -mr-8 -mt-8 group-hover:scale-125 transition-transform duration-500" />
                    <CardHeader className="flex flex-row items-center justify-between pb-2 space-y-0 relative">
                        <span className="text-[10px] font-bold uppercase tracking-wider text-muted-foreground">Playlists</span>
                        <div className="p-2 bg-blue-50 text-blue-600 rounded-2xl group-hover:bg-blue-600 group-hover:text-white transition-colors duration-300">
                            <ListMusic size={16} />
                        </div>
                    </CardHeader>
                    <CardContent className="relative">
                        <div className="text-3xl font-extrabold tracking-tight text-foreground font-mono">
                            {isLoading ? "..." : stats.counts.playlists}
                        </div>
                        <p className="text-[10px] text-muted-foreground mt-1">User customized curation</p>
                    </CardContent>
                </Card>

                {/* Cumulative Plays */}
                <Card className="shadow-sm border border-border/80 hover:border-amber-500/30 hover:shadow transition-all duration-300 bg-white group rounded-3xl relative overflow-hidden">
                    <div className="absolute top-0 right-0 w-24 h-24 bg-amber-500/5 rounded-full -mr-8 -mt-8 group-hover:scale-125 transition-transform duration-500" />
                    <CardHeader className="flex flex-row items-center justify-between pb-2 space-y-0 relative">
                        <span className="text-[10px] font-bold uppercase tracking-wider text-muted-foreground">Total Plays</span>
                        <div className="p-2 bg-amber-50 text-amber-600 rounded-2xl group-hover:bg-amber-600 group-hover:text-white transition-colors duration-300">
                            <Play size={16} className="fill-current ml-0.5" />
                        </div>
                    </CardHeader>
                    <CardContent className="relative">
                        <div className="text-3xl font-extrabold tracking-tight text-foreground font-mono truncate">
                            {isLoading ? "..." : stats.counts.views.toLocaleString()}
                        </div>
                        <p className="text-[10px] text-muted-foreground mt-1">Accumulated streams</p>
                    </CardContent>
                </Card>

            </div>

            {/* AI Recommendation & Liked Metrics Summary Panel */}
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div className="flex items-center gap-4 p-4 border border-border bg-white rounded-3xl shadow-sm hover:shadow-md transition-shadow">
                    <div className="p-3 bg-red-50 text-red-500 rounded-2xl shrink-0">
                        <Heart size={20} className="fill-red-500 animate-pulse" />
                    </div>
                    <div>
                        <span className="text-xs text-muted-foreground block">AI Interaction Logs Captured</span>
                        <span className="text-lg font-bold font-mono text-foreground">
                            {isLoading ? "..." : stats.counts.interactions} Events
                        </span>
                    </div>
                </div>
                <div className="flex items-center gap-4 p-4 border border-border bg-white rounded-3xl shadow-sm hover:shadow-md transition-shadow">
                    <div className="p-3 bg-indigo-50 text-indigo-500 rounded-2xl shrink-0">
                        <Sparkles size={20} className="animate-pulse" />
                    </div>
                    <div>
                        <span className="text-xs text-muted-foreground block">User Liked Tracks Record</span>
                        <span className="text-lg font-bold font-mono text-foreground">
                            {isLoading ? "..." : stats.counts.likes} Favorites
                        </span>
                    </div>
                </div>
            </div>

            {/* Dashboard Analytics & Tables */}
            <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">

                {/* 1. Trending Tracks (Left) */}
                <Card className="shadow-sm border border-border bg-white rounded-3xl flex flex-col justify-between lg:col-span-2">
                    <CardHeader className="pb-3 border-b border-border/40">
                        <CardTitle className="text-lg font-bold text-foreground">Trending Tracks</CardTitle>
                        <CardDescription>Top played audio tracks stored in MySQL database</CardDescription>
                    </CardHeader>

                    <CardContent className="py-4 flex-1 space-y-4">
                        {isLoading ? (
                            <div className="py-12 text-center text-xs text-muted-foreground">Loading trending list...</div>
                        ) : stats.trendingSongs.length === 0 ? (
                            <p className="text-muted-foreground text-xs italic text-center py-6">No song tracks loaded.</p>
                        ) : (
                            stats.trendingSongs.map((song) => {
                                const artistNames = song.artists && song.artists.length > 0
                                    ? song.artists.map(a => a.name).join(', ')
                                    : "Unknown Artist";
                                const percentage = Math.max(5, Math.round(((song.views || 0) / maxViews) * 100));

                                return (
                                    <div key={song.id} className="flex items-center gap-4 group">
                                        {/* Image wrapper */}
                                        <div className="w-12 h-12 rounded-2xl overflow-hidden shrink-0 border border-border bg-muted flex items-center justify-center relative">
                                            {song.cover_url ? (
                                                <img src={song.cover_url} alt="" className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-300" />
                                            ) : (
                                                <Music size={16} className="text-muted-foreground" />
                                            )}
                                        </div>
                                        {/* Progress and names */}
                                        <div className="flex-1 min-w-0">
                                            <div className="flex justify-between items-center text-xs mb-1">
                                                <span className="font-semibold text-foreground truncate max-w-[250px]">{song.title}</span>
                                                <span className="font-mono font-semibold text-indigo-600">
                                                    {song.views.toLocaleString()} plays
                                                </span>
                                            </div>
                                            <div className="text-[10px] text-muted-foreground truncate mb-2">{artistNames}</div>
                                            <div className="w-full bg-muted/65 h-1.5 rounded-full overflow-hidden">
                                                <div
                                                    className="bg-indigo-600 h-full rounded-full transition-all duration-500 ease-out"
                                                    style={{ width: `${percentage}%` }}
                                                />
                                            </div>
                                        </div>
                                    </div>
                                );
                            })
                        )}
                    </CardContent>
                </Card>

                {/* 2. Top Genres & Quick Actions (Right) */}
                <div className="space-y-6 flex flex-col">

                    {/* Top Genres Progress Bars */}
                    <Card className="shadow-sm border border-border bg-white rounded-3xl flex-1">
                        <CardHeader className="pb-3 border-b border-border/40">
                            <CardTitle className="text-base font-bold text-foreground">Popular Genres</CardTitle>
                            <CardDescription>Song count directory distribution</CardDescription>
                        </CardHeader>
                        <CardContent className="py-4 space-y-4">
                            {isLoading ? (
                                <div className="py-6 text-center text-xs text-muted-foreground">Loading genres stats...</div>
                            ) : stats.popularGenres.length === 0 ? (
                                <p className="text-muted-foreground text-xs italic text-center py-4">No genres available.</p>
                            ) : (
                                stats.popularGenres.map((genre, idx) => {
                                    const maxCount = stats.popularGenres[0]?.song_count || 1;
                                    const pct = Math.max(5, Math.round((genre.song_count / maxCount) * 100));

                                    // Custom colors
                                    const barColors = [
                                        "bg-gradient-to-r from-indigo-500 to-indigo-600",
                                        "bg-gradient-to-r from-violet-500 to-violet-600",
                                        "bg-gradient-to-r from-emerald-500 to-emerald-600",
                                        "bg-gradient-to-r from-rose-500 to-rose-600",
                                        "bg-gradient-to-r from-amber-500 to-amber-600"
                                    ];
                                    const colorClass = barColors[idx % barColors.length];

                                    return (
                                        <div key={genre.id} className="space-y-1.5">
                                            <div className="flex justify-between items-center text-xs">
                                                <span className="font-semibold text-foreground">{genre.name}</span>
                                                <span className="text-[10px] text-muted-foreground font-semibold font-mono">
                                                    {genre.song_count} songs
                                                </span>
                                            </div>
                                            <div className="w-full bg-muted/65 h-2 rounded-full overflow-hidden">
                                                <div
                                                    className={`${colorClass} h-full rounded-full transition-all duration-500 ease-out`}
                                                    style={{ width: `${pct}%` }}
                                                />
                                            </div>
                                        </div>
                                    );
                                })
                            )}
                        </CardContent>
                    </Card>

                </div>
            </div>

            {/* Bottom Row: Recent User Registrations & Shortcuts */}
            <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">

                {/* Recent Registrations (Col Span 2) */}
                <Card className="shadow-sm border border-border bg-white rounded-3xl lg:col-span-2">
                    <CardHeader className="pb-3 border-b border-border/40">
                        <CardTitle className="text-base font-bold text-foreground">Recent User Signups</CardTitle>
                        <CardDescription>Latest Supabase Auth synchronized profiles</CardDescription>
                    </CardHeader>
                    <CardContent className="py-4 space-y-3">
                        {isLoading ? (
                            <div className="py-8 text-center text-xs text-muted-foreground">Loading recent users...</div>
                        ) : stats.recentUsers.length === 0 ? (
                            <p className="text-muted-foreground text-xs italic text-center py-4">No registered users found.</p>
                        ) : (
                            stats.recentUsers.map((user) => (
                                <div key={user.id} className="flex items-center justify-between p-3 border border-border/40 rounded-2xl hover:bg-muted/10 transition-colors">
                                    <div className="flex items-center gap-3">
                                        <div className="w-9 h-9 rounded-full bg-indigo-50 border border-indigo-200/50 flex items-center justify-center text-indigo-700 font-semibold text-sm shrink-0 overflow-hidden">
                                            {user.avatar_url ? (
                                                <img src={user.avatar_url} alt="" className="w-full h-full object-cover" />
                                            ) : (
                                                (user.email ? user.email[0].toUpperCase() : 'U')
                                            )}
                                        </div>
                                        <div className="min-w-0">
                                            <span className="font-semibold text-xs text-foreground block truncate max-w-[220px]">{user.email}</span>
                                            <span className="text-[10px] text-muted-foreground capitalize block">{user.gender || "Unspecified"}</span>
                                        </div>
                                    </div>
                                    <span className="text-[10px] text-muted-foreground font-mono">
                                        {new Date(user.created_at).toLocaleDateString()}
                                    </span>
                                </div>
                            ))
                        )}
                    </CardContent>
                </Card>

                {/* Quick Administration Shortcuts */}
                <Card className="shadow-sm border border-border bg-white rounded-3xl">
                    <CardHeader className="pb-3 border-b border-border/40">
                        <CardTitle className="text-base font-bold text-foreground">Administration Shortcuts</CardTitle>
                        <CardDescription>Rapidly trigger registry forms and directory settings</CardDescription>
                    </CardHeader>

                    <CardContent className="py-4">
                        <div className="grid grid-cols-2 gap-3">

                            {/* Upload Track shortcut */}
                            <Link
                                to="/songs"
                                className="group p-4 border border-border/80 rounded-2xl hover:border-indigo-500/40 hover:bg-indigo-500/[0.02] transition-all text-left flex flex-col justify-between h-[110px]"
                            >
                                <div className="p-2 bg-indigo-50 text-indigo-600 rounded-xl w-fit group-hover:bg-indigo-600 group-hover:text-white transition-colors">
                                    <FileMusic size={18} />
                                </div>
                                <div className="flex items-center justify-between text-xs font-semibold text-foreground mt-2">
                                    <span>Upload Track</span>
                                    <ArrowRight size={14} className="opacity-0 group-hover:opacity-100 group-hover:translate-x-1 transition-all" />
                                </div>
                            </Link>

                            {/* Add Artist shortcut */}
                            <Link
                                to="/artists"
                                className="group p-4 border border-border/80 rounded-2xl hover:border-violet-500/40 hover:bg-violet-500/[0.02] transition-all text-left flex flex-col justify-between h-[110px]"
                            >
                                <div className="p-2 bg-violet-50 text-violet-600 rounded-xl w-fit group-hover:bg-violet-600 group-hover:text-white transition-colors">
                                    <UserPlus size={18} />
                                </div>
                                <div className="flex items-center justify-between text-xs font-semibold text-foreground mt-2">
                                    <span>Add Artist</span>
                                    <ArrowRight size={14} className="opacity-0 group-hover:opacity-100 group-hover:translate-x-1 transition-all" />
                                </div>
                            </Link>

                            {/* Add Genre shortcut */}
                            <Link
                                to="/genres"
                                className="group p-4 border border-border/80 rounded-2xl hover:border-emerald-500/40 hover:bg-emerald-500/[0.02] transition-all text-left flex flex-col justify-between h-[110px]"
                            >
                                <div className="p-2 bg-emerald-50 text-emerald-600 rounded-xl w-fit group-hover:bg-emerald-600 group-hover:text-white transition-colors">
                                    <PlusCircle size={18} />
                                </div>
                                <div className="flex items-center justify-between text-xs font-semibold text-foreground mt-2">
                                    <span>Curate Genre</span>
                                    <ArrowRight size={14} className="opacity-0 group-hover:opacity-100 group-hover:translate-x-1 transition-all" />
                                </div>
                            </Link>

                            {/* System Settings shortcut */}
                            <Link
                                to="/settings"
                                className="group p-4 border border-border/80 rounded-2xl hover:border-slate-500/40 hover:bg-slate-500/[0.02] transition-all text-left flex flex-col justify-between h-[110px]"
                            >
                                <div className="p-2 bg-slate-50 text-slate-600 rounded-xl w-fit group-hover:bg-slate-600 group-hover:text-white transition-colors">
                                    <PlusCircle size={18} className="rotate-45" />
                                </div>
                                <div className="flex items-center justify-between text-xs font-semibold text-foreground mt-2">
                                    <span>Settings</span>
                                    <ArrowRight size={14} className="opacity-0 group-hover:opacity-100 group-hover:translate-x-1 transition-all" />
                                </div>
                            </Link>

                        </div>
                    </CardContent>
                </Card>

            </div>
        </div>
    );
}
