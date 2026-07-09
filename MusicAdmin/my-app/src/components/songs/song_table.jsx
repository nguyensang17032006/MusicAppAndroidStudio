import React, { useState, useMemo } from 'react';
import { Card, CardHeader, CardTitle, CardDescription, CardContent } from "@/components/ui/card.jsx";
import { Input } from "@/components/ui/input.jsx";
import { Button } from "@/components/ui/button.jsx";
import { Badge } from "@/components/ui/badge.jsx";
import { Search, X, Music, Play, Pause, Edit2, Trash2 } from "lucide-react";

// Fallback Cover image art helper
const CoverPreview = ({ url, title }) => {
    const [hasError, setHasError] = useState(false);

    return hasError || !url ? (
        <div className="w-12 h-12 rounded-xl bg-primary/10 border border-primary/20 flex items-center justify-center text-primary select-none shrink-0">
            <Music size={18} />
        </div>
    ) : (
        <img
            src={url}
            alt={title}
            onError={() => setHasError(true)}
            className="w-12 h-12 rounded-xl object-cover border border-border shrink-0"
        />
    );
};

export default function SongTable({
    songs,
    onEdit,
    onDelete,
    editingId
}) {
    const [searchTerm, setSearchTerm] = useState('');
    const [playingSongId, setPlayingSongId] = useState(null);
    const [audioElement, setAudioElement] = useState(null);

    // Filter list
    const filteredSongs = useMemo(() => {
        return songs.filter(song =>
            song.title.toLowerCase().includes(searchTerm.toLowerCase()) ||
            song.id.toLowerCase().includes(searchTerm.toLowerCase()) ||
            (song.artists && song.artists.some(a => a.name.toLowerCase().includes(searchTerm.toLowerCase()))) ||
            (song.genres && song.genres.some(g => g.name.toLowerCase().includes(searchTerm.toLowerCase())))
        );
    }, [songs, searchTerm]);

    // Handle Inline Audio Previews
    const togglePlayAudio = (song) => {
        if (!song.file_url) return;

        if (playingSongId === song.id) {
            // Pause
            if (audioElement) {
                audioElement.pause();
            }
            setPlayingSongId(null);
        } else {
            // Stop old audio if playing
            if (audioElement) {
                audioElement.pause();
            }

            const audio = new Audio(song.file_url);
            audio.play().catch(e => console.warn("Failed to play audio:", e));
            audio.onended = () => setPlayingSongId(null);

            setAudioElement(audio);
            setPlayingSongId(song.id);
        }
    };

    // Clean up audio when component unmounts
    React.useEffect(() => {
        return () => {
            if (audioElement) {
                audioElement.pause();
            }
        };
    }, [audioElement]);

    const formatDuration = (seconds) => {
        const mins = Math.floor(seconds / 60);
        const secs = seconds % 60;
        return `${mins}:${secs.toString().padStart(2, '0')}`;
    };

    return (
        <Card className="shadow-lg border border-border bg-white h-full flex flex-col">
            <CardHeader className="pb-3 flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4 shrink-0">
                <div>
                    <CardTitle className="text-lg font-semibold text-foreground">Registry Records</CardTitle>
                    <CardDescription>All registered tracks ({filteredSongs.length} showing)</CardDescription>
                </div>
                
                {/* Search bar */}
                <div className="relative w-full sm:w-60">
                    <Search className="absolute left-3 top-1/2 -translate-y-1/2 text-muted-foreground w-4 h-4" />
                    <Input
                        placeholder="Search by title, artist, genre..."
                        value={searchTerm}
                        onChange={(e) => setSearchTerm(e.target.value)}
                        className="pl-9 bg-muted/40 text-sm h-9 border border-border rounded-3xl focus-visible:bg-white"
                    />
                    {searchTerm && (
                        <button 
                            onClick={() => setSearchTerm('')}
                            className="absolute right-3 top-1/2 -translate-y-1/2 text-muted-foreground hover:text-foreground"
                        >
                            <X size={14} />
                        </button>
                    )}
                </div>
            </CardHeader>
            
            <CardContent className="flex-1 overflow-hidden p-0">
                <div className="overflow-x-auto w-full max-h-[600px]">
                    {filteredSongs.length === 0 ? (
                        <div className="py-16 text-center">
                            <Music className="mx-auto h-12 w-12 text-muted-foreground/45 bg-muted rounded-full p-2.5 mb-3" />
                            <h3 className="text-base font-semibold text-foreground">No songs found</h3>
                            <p className="text-sm text-muted-foreground mt-1 px-4">
                                {searchTerm 
                                    ? "Try adjusting your search query parameters."
                                    : "Start by filling out the creation form to register a track."
                                }
                            </p>
                        </div>
                    ) : (
                        <table className="w-full text-left border-collapse text-sm min-w-[700px] md:min-w-0">
                            <thead>
                                <tr className="bg-muted/40 border-b border-border/80 text-muted-foreground text-xs uppercase tracking-wider font-semibold">
                                    <th className="py-3 px-4 font-semibold w-12">Play</th>
                                    <th className="py-3 px-4 font-semibold">Track Details</th>
                                    <th className="py-3 px-4 font-semibold">Artists</th>
                                    <th className="py-3 px-4 font-semibold">Genres</th>
                                    <th className="py-3 px-4 font-semibold hidden md:table-cell w-20">Stats</th>
                                    <th className="py-3 px-4 font-semibold text-right">Actions</th>
                                </tr>
                            </thead>
                            <tbody className="divide-y divide-border/60">
                                {filteredSongs.map((song) => (
                                    <tr 
                                        key={song.id} 
                                        className={`hover:bg-muted/20 transition-colors ${
                                            editingId === song.id ? 'bg-primary/5 hover:bg-primary/5' : ''
                                        }`}
                                    >
                                        {/* Audio Play Trigger */}
                                        <td className="py-3.5 px-4">
                                            <Button
                                                type="button"
                                                variant="outline"
                                                size="icon"
                                                onClick={() => togglePlayAudio(song)}
                                                className={`size-8 rounded-full border border-border shadow-sm cursor-pointer ${
                                                    playingSongId === song.id 
                                                        ? 'bg-rose-50 border-rose-200 text-rose-500 hover:bg-rose-100 hover:text-rose-600'
                                                        : 'hover:bg-muted'
                                                }`}
                                                title={playingSongId === song.id ? "Pause Preview" : "Play Preview"}
                                            >
                                                {playingSongId === song.id ? <Pause size={14} /> : <Play size={14} className="ml-0.5" />}
                                            </Button>
                                        </td>

                                        {/* Cover art + Title + Length */}
                                        <td className="py-3.5 px-4">
                                            <div className="flex items-center gap-3">
                                                <CoverPreview url={song.cover_url} title={song.title} />
                                                <div className="max-w-[150px] sm:max-w-[200px]">
                                                    <span className="font-semibold text-foreground block truncate">{song.title}</span>
                                                    <span className="text-[10px] text-muted-foreground font-mono font-medium block mt-0.5">
                                                        {song.id} • {formatDuration(song.duration)}
                                                    </span>
                                                </div>
                                            </div>
                                        </td>

                                        {/* Artists names list */}
                                        <td className="py-3.5 px-4 text-xs font-medium text-foreground/80 max-w-[140px] truncate">
                                            {song.artists && song.artists.length > 0 ? (
                                                <div className="space-y-0.5">
                                                    {song.artists.map((art, idx) => (
                                                        <span key={art.artist_id || idx} className="block truncate">
                                                            {art.name}
                                                            {!art.is_main_artist && (
                                                                <span className="text-[9px] text-muted-foreground font-normal ml-1 bg-muted/65 px-1 py-0.5 rounded">ft.</span>
                                                            )}
                                                        </span>
                                                    ))}
                                                </div>
                                            ) : (
                                                <span className="text-muted-foreground italic">Unknown Artist</span>
                                            )}
                                        </td>

                                        {/* Genres list */}
                                        <td className="py-3.5 px-4">
                                            <div className="flex flex-wrap gap-1 max-w-[150px]">
                                                {song.genres && song.genres.length > 0 ? (
                                                    song.genres.map((g, idx) => (
                                                        <Badge 
                                                            key={g.genre_id || idx} 
                                                            variant="secondary"
                                                            className="text-[9px] font-semibold px-2 py-0.5 scale-95 border-none rounded-full bg-slate-100 text-slate-700"
                                                        >
                                                            {g.name}
                                                        </Badge>
                                                    ))
                                                ) : (
                                                    <span className="text-muted-foreground text-xs italic">Unassigned</span>
                                                )}
                                            </div>
                                        </td>

                                        {/* Statistics */}
                                        <td className="py-3.5 px-4 font-mono text-xs text-muted-foreground hidden md:table-cell">
                                            <span className="block">{song.views.toLocaleString()} views</span>
                                        </td>

                                        {/* Action Buttons */}
                                        <td className="py-3.5 px-4 text-right">
                                            <div className="flex items-center justify-end gap-1">
                                                <Button
                                                    type="button"
                                                    variant="ghost"
                                                    size="icon"
                                                    onClick={() => onEdit(song)}
                                                    className={`size-8 rounded-full cursor-pointer ${
                                                        editingId === song.id 
                                                            ? 'text-primary bg-primary/10'
                                                            : 'text-muted-foreground hover:text-foreground'
                                                    }`}
                                                    title="Edit Track"
                                                >
                                                    <Edit2 size={14} />
                                                </Button>
                                                <Button
                                                    type="button"
                                                    variant="ghost"
                                                    size="icon"
                                                    onClick={() => onDelete(song.id)}
                                                    className="size-8 rounded-full text-muted-foreground hover:text-rose-600 hover:bg-rose-50 dark:hover:bg-rose-950/20 cursor-pointer"
                                                    title="Delete Track"
                                                >
                                                    <Trash2 size={14} />
                                                </Button>
                                            </div>
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    )}
                </div>
            </CardContent>
        </Card>
    );
}
