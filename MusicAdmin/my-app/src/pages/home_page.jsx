import React, { useState, useEffect, useMemo } from 'react';
import { Link } from 'react-router-dom';
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from "@/components/ui/card.jsx";
import { Music, Users, Tags, Play, PlusCircle, ArrowRight, UserPlus, FileMusic } from "lucide-react";

// Mock Fallbacks in case localStorage is empty
const MOCK_SONGS = [
    { id: "S001", title: "Lạc Trôi", views: 120450, artists: [{ name: "Sơn Tùng M-TP" }] },
    { id: "S002", title: "Trốn Tìm", views: 89400, artists: [{ name: "Đen Vâu" }] }
];
const MOCK_ARTISTS = [{ id: "A001" }, { id: "A002" }, { id: "A003" }];
const MOCK_GENRES = [{ id: "G001" }, { id: "G002" }, { id: "G003" }];

export default function Home() {
    // 1. Load Data from localStorage (fallback to initial seed datasets)
    const [stats, setStats] = useState({
        songs: [],
        artists: [],
        genres: []
    });
    const [adminName, setAdminName] = useState('Sáng');

    useEffect(() => {
        const storedSongs = localStorage.getItem('music_admin_songs');
        const storedArtists = localStorage.getItem('music_admin_artists');
        const storedGenres = localStorage.getItem('music_admin_genres');
        const name = localStorage.getItem('music_admin_name');

        if (name) setAdminName(name);

        setStats({
            songs: storedSongs ? JSON.parse(storedSongs) : MOCK_SONGS,
            artists: storedArtists ? JSON.parse(storedArtists) : MOCK_ARTISTS,
            genres: storedGenres ? JSON.parse(storedGenres) : MOCK_GENRES
        });
    }, []);


    // 2. Compute KPI Metrics
    const metrics = useMemo(() => {
        const totalSongs = stats.songs.length;
        const totalArtists = stats.artists.length;
        const totalGenres = stats.genres.length;
        const totalViews = stats.songs.reduce((acc, song) => acc + (parseInt(song.views, 10) || 0), 0);

        // Sort songs by plays views descending for trending tracks
        const trendingSongs = [...stats.songs]
            .sort((a, b) => (b.views || 0) - (a.views || 0))
            .slice(0, 4);

        // Determine max views for relative progress bar calculation
        const maxViews = trendingSongs.length > 0 ? Math.max(...trendingSongs.map(s => s.views || 0)) : 1;

        return {
            totalSongs,
            totalArtists,
            totalGenres,
            totalViews,
            trendingSongs,
            maxViews
        };
    }, [stats]);

    return (
        <div className="space-y-6">
            {/* Page Title & Greeting */}
            <div>
                <h1 className="text-2xl font-bold tracking-tight text-foreground sm:text-3xl">Dashboard Overview</h1>
                <p className="text-muted-foreground text-sm mt-1">
                    Welcome back, <span className="font-semibold text-foreground">Admin • Sáng</span>. Here is a summary of your platform records.
                </p>
            </div>

            {/* 4 Stat KPIs Grid */}
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
                
                {/* Songs KPI */}
                <Card className="shadow border border-border/80 hover:border-primary/20 transition-colors bg-white">
                    <CardHeader className="flex flex-row items-center justify-between pb-2 space-y-0">
                        <span className="text-xs font-semibold uppercase tracking-wider text-muted-foreground">Total Tracks</span>
                        <div className="p-1.5 bg-blue-50 text-blue-600 rounded-xl">
                            <Music size={18} />
                        </div>
                    </CardHeader>
                    <CardContent>
                        <div className="text-3xl font-bold font-mono tracking-tight text-foreground">
                            {metrics.totalSongs}
                        </div>
                        <p className="text-[10px] text-muted-foreground mt-1">Audio files linked in system</p>
                    </CardContent>
                </Card>

                {/* Artists KPI */}
                <Card className="shadow border border-border/80 hover:border-primary/20 transition-colors bg-white">
                    <CardHeader className="flex flex-row items-center justify-between pb-2 space-y-0">
                        <span className="text-xs font-semibold uppercase tracking-wider text-muted-foreground">Creators</span>
                        <div className="p-1.5 bg-violet-50 text-violet-600 rounded-xl">
                            <Users size={18} />
                        </div>
                    </CardHeader>
                    <CardContent>
                        <div className="text-3xl font-bold font-mono tracking-tight text-foreground">
                            {metrics.totalArtists}
                        </div>
                        <p className="text-[10px] text-muted-foreground mt-1">Registered authors and bands</p>
                    </CardContent>
                </Card>

                {/* Genres KPI */}
                <Card className="shadow border border-border/80 hover:border-primary/20 transition-colors bg-white">
                    <CardHeader className="flex flex-row items-center justify-between pb-2 space-y-0">
                        <span className="text-xs font-semibold uppercase tracking-wider text-muted-foreground">Genres</span>
                        <div className="p-1.5 bg-emerald-50 text-emerald-600 rounded-xl">
                            <Tags size={18} />
                        </div>
                    </CardHeader>
                    <CardContent>
                        <div className="text-3xl font-bold font-mono tracking-tight text-foreground">
                            {metrics.totalGenres}
                        </div>
                        <p className="text-[10px] text-muted-foreground mt-1">Classification directories</p>
                    </CardContent>
                </Card>

                {/* Play views KPI */}
                <Card className="shadow border border-border/80 hover:border-primary/20 transition-colors bg-white">
                    <CardHeader className="flex flex-row items-center justify-between pb-2 space-y-0">
                        <span className="text-xs font-semibold uppercase tracking-wider text-muted-foreground">Total Plays</span>
                        <div className="p-1.5 bg-rose-50 text-rose-600 rounded-xl">
                            <Play size={16} className="fill-rose-600 ml-0.5" />
                        </div>
                    </CardHeader>
                    <CardContent>
                        <div className="text-3xl font-bold font-mono tracking-tight text-foreground truncate">
                            {metrics.totalViews.toLocaleString()}
                        </div>
                        <p className="text-[10px] text-muted-foreground mt-1">Cumulative views across songs</p>
                    </CardContent>
                </Card>

            </div>

            {/* Analytics Grid */}
            <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
                
                {/* Left: Top Tracks */}
                <Card className="shadow border border-border bg-white flex flex-col justify-between">
                    <CardHeader className="pb-3 border-b border-border/40">
                        <CardTitle className="text-base font-semibold text-foreground">Trending Tracks</CardTitle>
                        <CardDescription>Most played tracks cataloged in database</CardDescription>
                    </CardHeader>
                    
                    <CardContent className="py-4 flex-1 space-y-4">
                        {metrics.trendingSongs.length === 0 ? (
                            <p className="text-muted-foreground text-xs italic text-center py-6">No songs loaded in catalog.</p>
                        ) : (
                            metrics.trendingSongs.map((song) => {
                                const artistName = song.artists && song.artists.length > 0 ? song.artists[0].name : "Unknown Artist";
                                const percentage = Math.max(5, Math.round(((song.views || 0) / metrics.maxViews) * 100));
                                
                                return (
                                    <div key={song.id} className="space-y-1.5">
                                        <div className="flex justify-between items-center text-xs">
                                            <div>
                                                <span className="font-semibold text-foreground block">{song.title}</span>
                                                <span className="text-[10px] text-muted-foreground block mt-0.5">{artistName}</span>
                                            </div>
                                            <span className="font-mono text-muted-foreground font-semibold">
                                                {song.views.toLocaleString()} plays
                                            </span>
                                        </div>
                                        <div className="w-full bg-muted/65 h-1.5 rounded-full overflow-hidden">
                                            <div 
                                                className="bg-primary h-full rounded-full transition-all duration-500 ease-out" 
                                                style={{ width: `${percentage}%` }}
                                            />
                                        </div>
                                    </div>
                                );
                            })
                        )}
                    </CardContent>
                </Card>

                {/* Right: Quick Action Grid */}
                <Card className="shadow border border-border bg-white">
                    <CardHeader className="pb-3 border-b border-border/40">
                        <CardTitle className="text-base font-semibold text-foreground">Quick Administration Shortcuts</CardTitle>
                        <CardDescription>Rapidly trigger registry forms and classification curators</CardDescription>
                    </CardHeader>
                    
                    <CardContent className="py-4">
                        <div className="grid grid-cols-2 gap-3">
                            
                            {/* Add Song shortcut */}
                            <Link 
                                to="/songs"
                                className="group p-4 border border-border/80 rounded-2xl hover:border-primary/30 hover:bg-muted/15 transition-all text-left flex flex-col justify-between h-[110px]"
                            >
                                <div className="p-2 bg-blue-50 text-blue-600 rounded-xl w-fit">
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
                                className="group p-4 border border-border/80 rounded-2xl hover:border-primary/30 hover:bg-muted/15 transition-all text-left flex flex-col justify-between h-[110px]"
                            >
                                <div className="p-2 bg-violet-50 text-violet-600 rounded-xl w-fit">
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
                                className="group p-4 border border-border/80 rounded-2xl hover:border-primary/30 hover:bg-muted/15 transition-all text-left flex flex-col justify-between h-[110px]"
                            >
                                <div className="p-2 bg-emerald-50 text-emerald-600 rounded-xl w-fit">
                                    <PlusCircle size={18} />
                                </div>
                                <div className="flex items-center justify-between text-xs font-semibold text-foreground mt-2">
                                    <span>Curate Genre</span>
                                    <ArrowRight size={14} className="opacity-0 group-hover:opacity-100 group-hover:translate-x-1 transition-all" />
                                </div>
                            </Link>

                            {/* Settings shortcut */}
                            <Link 
                                to="/settings"
                                className="group p-4 border border-border/80 rounded-2xl hover:border-primary/30 hover:bg-muted/15 transition-all text-left flex flex-col justify-between h-[110px]"
                            >
                                <div className="p-2 bg-slate-50 text-slate-600 rounded-xl w-fit">
                                    <PlusCircle size={18} className="rotate-45" />
                                </div>
                                <div className="flex items-center justify-between text-xs font-semibold text-foreground mt-2">
                                    <span>System Settings</span>
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
