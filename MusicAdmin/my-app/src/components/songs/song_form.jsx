import React, { useState, useEffect } from 'react';
import { Button } from "@/components/ui/button.jsx";
import { Input } from "@/components/ui/input.jsx";
import {
    Card,
    CardHeader,
    CardTitle,
    CardDescription,
    CardContent
} from "@/components/ui/card.jsx";
import { Plus, X, Upload, Music, Image as ImageIcon, Loader2 } from "lucide-react";

export default function SongForm({
    editingSong,
    nextId,
    artists, // List of all artists in DB for selector
    genres,  // List of all genres in DB for selector
    onSubmit,
    onCancel,
    isLoading
}) {
    const [title, setTitle] = useState('');
    const [duration, setDuration] = useState(0);
    const [selectedArtists, setSelectedArtists] = useState([]); // [{ artist_id, is_main_artist }]
    const [selectedGenres, setSelectedGenres] = useState([]);    // [genre_id]
    
    const [audioFile, setAudioFile] = useState(null);
    const [audioFileName, setAudioFileName] = useState('');
    const [audioPreview, setAudioPreview] = useState('');

    const [coverFile, setCoverFile] = useState(null);
    const [coverPreview, setCoverPreview] = useState('');

    // Pre-fill form when entering Edit mode
    useEffect(() => {
        if (editingSong) {
            setTitle(editingSong.title || '');
            setDuration(editingSong.duration || 0);
            
            // Map existing artists & genres relation
            const mappedArtists = editingSong.artists 
                ? editingSong.artists.map(a => ({ artist_id: a.artist_id, is_main_artist: a.is_main_artist }))
                : [];
            const mappedGenres = editingSong.genres 
                ? editingSong.genres.map(g => g.genre_id)
                : [];
                
            setSelectedArtists(mappedArtists);
            setSelectedGenres(mappedGenres);
            setAudioFile(null);
            setAudioFileName('');
            setAudioPreview(editingSong.file_url || '');
            setCoverFile(null);
            setCoverPreview(editingSong.cover_url || '');
        } else {
            setTitle('');
            setDuration(0);
            setSelectedArtists([]);
            setSelectedGenres([]);
            setAudioFile(null);
            setAudioFileName('');
            setAudioPreview('');
            setCoverFile(null);
            setCoverPreview('');
        }
    }, [editingSong]);

    // Handle Audio File: Extract duration automatically
    const handleAudioChange = (e) => {
        const file = e.target.files[0];
        if (file) {
            setAudioFile(file);
            setAudioFileName(file.name);
            
            // Audio preview
            const tempUrl = URL.createObjectURL(file);
            setAudioPreview(tempUrl);
            
            // Auto duration loader
            const audioEl = document.createElement('audio');
            audioEl.src = tempUrl;
            audioEl.onloadedmetadata = () => {
                setDuration(Math.round(audioEl.duration));
            };
        }
    };

    // Handle Cover Image: Reader Base64
    const handleCoverChange = (e) => {
        const file = e.target.files[0];
        if (file) {
            setCoverFile(file);
            
            const reader = new FileReader();
            reader.onloadend = () => {
                setCoverPreview(reader.result);
            };
            reader.readAsDataURL(file);
        }
    };

    const handleArtistSelect = (artistId) => {
        setSelectedArtists(prev => {
            const exists = prev.find(a => a.artist_id === artistId);
            if (exists) {
                // remove
                return prev.filter(a => a.artist_id !== artistId);
            } else {
                // add as main by default
                return [...prev, { artist_id: artistId, is_main_artist: true }];
            }
        });
    };

    const toggleMainArtist = (artistId) => {
        setSelectedArtists(prev => prev.map(a =>
            a.artist_id === artistId
                ? { ...a, is_main_artist: !a.is_main_artist }
                : a
        ));
    };

    const handleGenreSelect = (genreId) => {
        setSelectedGenres(prev =>
            prev.includes(genreId)
                ? prev.filter(gId => gId !== genreId)
                : [...prev, genreId]
        );
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        if (!title.trim()) return;

        // Construct FormData for submission
        const data = new FormData();
        const displayId = editingSong ? editingSong.id : nextId;
        
        data.append('id', displayId);
        data.append('title', title.trim());
        data.append('duration', duration);
        data.append('artists', JSON.stringify(selectedArtists)); // Send relations as JSON strings
        data.append('genres', JSON.stringify(selectedGenres));
        
        if (audioFile) {
            data.append('audio', audioFile); // Audio Binary
        } else if (editingSong && editingSong.file_url) {
            data.append('file_url', editingSong.file_url); // Keep old URL if editing
        }

        if (coverFile) {
            data.append('cover', coverFile); // Cover Binary
        } else if (editingSong && editingSong.cover_url) {
            data.append('cover_url', editingSong.cover_url);
        }

        // Build mock preview data for instant UI updates (local fallbacks)
        const mockArtistsData = selectedArtists.map(sa => {
            const original = artists.find(art => art.id === sa.artist_id);
            return {
                artist_id: sa.artist_id,
                name: original ? original.name : sa.artist_id,
                is_main_artist: sa.is_main_artist
            };
        });

        const mockGenresData = selectedGenres.map(sgId => {
            const original = genres.find(g => g.id === sgId);
            return {
                genre_id: sgId,
                name: original ? original.name : sgId
            };
        });

        onSubmit(data, {
            id: displayId,
            title: title.trim(),
            duration: duration,
            cover_url: coverPreview || (editingSong ? editingSong.cover_url : ''),
            file_url: audioPreview || (editingSong ? editingSong.file_url : ''),
            artists: mockArtistsData,
            genres: mockGenresData
        });

        if (!editingSong) {
            setTitle('');
            setDuration(0);
            setSelectedArtists([]);
            setSelectedGenres([]);
            setAudioFile(null);
            setAudioFileName('');
            setAudioPreview('');
            setCoverFile(null);
            setCoverPreview('');
        }
    };

    const formatDuration = (seconds) => {
        const mins = Math.floor(seconds / 60);
        const secs = seconds % 60;
        return `${mins}:${secs.toString().padStart(2, '0')}`;
    };

    const displayId = editingSong ? editingSong.id : nextId;

    return (
        <Card className="shadow-lg border border-border bg-white">
            <CardHeader className="pb-4">
                <CardTitle className="text-lg font-semibold text-foreground">
                    {editingSong ? `Edit Song: ${editingSong.id}` : "Register New Song"}
                </CardTitle>
                <CardDescription>
                    {editingSong ? "Modify track records and relations." : "Add a new song, link authors, select genres, and upload media."}
                </CardDescription>
            </CardHeader>
            <CardContent>
                <form onSubmit={handleSubmit} className="space-y-4">
                    {/* ID & Duration */}
                    <div className="grid grid-cols-2 gap-4">
                        <div className="space-y-1.5">
                            <label className="text-xs font-semibold uppercase tracking-wider text-muted-foreground">
                                Song ID
                            </label>
                            <Input
                                value={displayId}
                                disabled
                                className="bg-muted text-muted-foreground font-mono font-bold tracking-wide"
                            />
                        </div>
                        <div className="space-y-1.5">
                            <label className="text-xs font-semibold uppercase tracking-wider text-muted-foreground">
                                Length
                            </label>
                            <Input
                                value={formatDuration(duration)}
                                disabled
                                className="bg-muted text-muted-foreground font-mono font-bold tracking-wide"
                            />
                        </div>
                    </div>

                    {/* Title */}
                    <div className="space-y-1.5">
                        <label className="text-xs font-semibold uppercase tracking-wider text-muted-foreground flex items-center gap-1">
                            Track Title <span className="text-rose-500">*</span>
                        </label>
                        <Input
                            value={title}
                            onChange={(e) => setTitle(e.target.value)}
                            placeholder="e.g. Lạc Trôi"
                            required
                            disabled={isLoading}
                            className="bg-muted/30 focus-visible:bg-white"
                        />
                    </div>

                    {/* Audio File Picker */}
                    <div className="space-y-1.5">
                        <label className="text-xs font-semibold uppercase tracking-wider text-muted-foreground flex items-center gap-1">
                            Audio Track file <span className="text-rose-500">*</span>
                        </label>
                        <div className="flex items-center gap-3">
                            <input
                                type="file"
                                id="audio-upload"
                                accept="audio/*"
                                onChange={handleAudioChange}
                                className="hidden"
                                disabled={isLoading}
                                required={!editingSong}
                            />
                            <label
                                htmlFor="audio-upload"
                                className={`inline-flex items-center gap-1.5 px-3.5 py-2 border border-border rounded-3xl bg-white hover:bg-muted text-xs font-medium cursor-pointer transition-colors shadow-sm select-none ${
                                    isLoading ? 'pointer-events-none opacity-50' : ''
                                }`}
                            >
                                <Music size={14} className="text-muted-foreground" />
                                Select MP3
                            </label>
                            <p className="text-xs text-muted-foreground truncate max-w-[200px]">
                                {audioFileName || (editingSong && editingSong.file_url ? "Existing track loaded" : "No track selected")}
                            </p>
                        </div>
                    </div>

                    {/* Cover Art Picker */}
                    <div className="space-y-1.5">
                        <label className="text-xs font-semibold uppercase tracking-wider text-muted-foreground">
                            Cover Image Art
                        </label>
                        <div className="flex items-center gap-4 py-1">
                            <div className="shrink-0 w-16 h-16 rounded-xl overflow-hidden border border-dashed border-primary/30 bg-muted/40 flex items-center justify-center">
                                {coverPreview ? (
                                    <img src={coverPreview} alt="Cover Preview" className="w-full h-full object-cover" />
                                ) : (
                                    <ImageIcon className="text-muted-foreground/60 w-5 h-5" />
                                )}
                            </div>
                            <div>
                                <input
                                    type="file"
                                    id="cover-upload"
                                    accept="image/*"
                                    onChange={handleCoverChange}
                                    className="hidden"
                                    disabled={isLoading}
                                />
                                <label
                                    htmlFor="cover-upload"
                                    className={`inline-flex items-center gap-1.5 px-3.5 py-2 border border-border rounded-3xl bg-white hover:bg-muted text-xs font-medium cursor-pointer transition-colors shadow-sm select-none ${
                                        isLoading ? 'pointer-events-none opacity-50' : ''
                                    }`}
                                >
                                    <Upload size={14} className="text-muted-foreground" />
                                    Choose Photo
                                </label>
                                <p className="text-[10px] text-muted-foreground mt-1">PNG, JPG formats accepted.</p>
                            </div>
                        </div>
                    </div>

                    {/* Artists selector (Many-to-Many) */}
                    <div className="space-y-2 pt-1">
                        <label className="text-xs font-semibold uppercase tracking-wider text-muted-foreground block">
                            Link Artists
                        </label>
                        <div className="border border-border/80 rounded-2xl p-3 bg-muted/20 max-h-[140px] overflow-y-auto space-y-2 text-xs">
                            {artists.length === 0 ? (
                                <p className="text-muted-foreground italic">No registered artists. Add artists first.</p>
                            ) : (
                                artists.map(artist => {
                                    const linked = selectedArtists.find(a => a.artist_id === artist.id);
                                    return (
                                        <div key={artist.id} className="flex items-center justify-between border-b border-border/40 pb-1.5 last:border-0 last:pb-0">
                                            <label className="flex items-center gap-2 font-medium cursor-pointer">
                                                <input
                                                    type="checkbox"
                                                    checked={!!linked}
                                                    onChange={() => handleArtistSelect(artist.id)}
                                                    disabled={isLoading}
                                                    className="rounded border-border focus:ring-primary w-3.5 h-3.5"
                                                />
                                                <span>{artist.name}</span>
                                            </label>
                                            
                                            {linked && (
                                                <div className="flex items-center gap-1.5">
                                                    <span className="text-[10px] text-muted-foreground">Main Artist?</span>
                                                    <input
                                                        type="checkbox"
                                                        checked={linked.is_main_artist}
                                                        onChange={() => toggleMainArtist(artist.id)}
                                                        disabled={isLoading}
                                                        className="rounded border-border focus:ring-primary w-3 h-3 text-primary"
                                                    />
                                                </div>
                                            )}
                                        </div>
                                    );
                                })
                            )}
                        </div>
                    </div>

                    {/* Genres selector (Many-to-Many) */}
                    <div className="space-y-2 pt-1">
                        <label className="text-xs font-semibold uppercase tracking-wider text-muted-foreground block">
                            Select Genres
                        </label>
                        <div className="border border-border/80 rounded-2xl p-3 bg-muted/20 max-h-[140px] overflow-y-auto grid grid-cols-2 gap-2 text-xs">
                            {genres.length === 0 ? (
                                <p className="text-muted-foreground italic col-span-2">No registered genres. Add genres first.</p>
                            ) : (
                                genres.map(genre => (
                                    <label key={genre.id} className="flex items-center gap-2 font-medium cursor-pointer py-1">
                                        <input
                                            type="checkbox"
                                            checked={selectedGenres.includes(genre.id)}
                                            onChange={() => handleGenreSelect(genre.id)}
                                            disabled={isLoading}
                                            className="rounded border-border focus:ring-primary w-3.5 h-3.5"
                                        />
                                        <span className="truncate">{genre.name}</span>
                                    </label>
                                ))
                            )}
                        </div>
                    </div>

                    {/* Action Buttons */}
                    <div className="flex gap-2 pt-2">
                        <Button
                            type="submit"
                            disabled={isLoading || !title.trim()}
                            className="flex-1 rounded-3xl h-10 font-semibold gap-1.5 flex items-center justify-center cursor-pointer"
                        >
                            {isLoading ? (
                                <Loader2 size={16} className="animate-spin" />
                            ) : editingSong ? (
                                <Plus size={16} className="rotate-45" />
                            ) : (
                                <Plus size={16} />
                            )}
                            {isLoading ? "Uploading..." : editingSong ? "Save Changes" : "Publish Song"}
                        </Button>

                        {editingSong && !isLoading && (
                            <Button
                                type="button"
                                variant="outline"
                                onClick={onCancel}
                                className="rounded-3xl h-10 font-semibold px-4 border border-border cursor-pointer flex items-center justify-center gap-1.5 text-muted-foreground"
                            >
                                <X size={16} />
                                Cancel
                            </Button>
                        )}
                    </div>
                </form>
            </CardContent>
        </Card>
    );
}
