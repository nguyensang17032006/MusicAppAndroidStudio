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
import { Plus, X, Upload, User, Loader2 } from "lucide-react";

export default function ArtistForm({
    editingArtist,
    nextId,
    onSubmit,
    onCancel,
    isLoading
}) {
    const [name, setName] = useState('');
    const [bio, setBio] = useState('');
    const [avatarFile, setAvatarFile] = useState(null);
    const [avatarPreview, setAvatarPreview] = useState('');

    // Pre-fill form when entering Edit mode
    useEffect(() => {
        if (editingArtist) {
            setName(editingArtist.name || '');
            setBio(editingArtist.bio || '');
            setAvatarFile(null);
            setAvatarPreview('');
        } else {
            setName('');
            setBio('');
            setAvatarFile(null);
            setAvatarPreview('');
        }
    }, [editingArtist]);

    const handleFileChange = (e) => {
        const file = e.target.files[0];
        if (file) {
            setAvatarFile(file);
            
            // Read file as Base64 for local preview
            const reader = new FileReader();
            reader.onloadend = () => {
                setAvatarPreview(reader.result);
            };
            reader.readAsDataURL(file);
        }
    };

    const handleRemoveSelectedFile = () => {
        setAvatarFile(null);
        setAvatarPreview('');
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        if (!name.trim()) return;

        // Build FormData
        const data = new FormData();
        const displayId = editingArtist ? editingArtist.id : nextId;
        
        data.append('id', displayId);
        data.append('name', name.trim());
        data.append('bio', bio.trim());
        
        if (avatarFile) {
            data.append('avatar', avatarFile); // Uploading new file binary
        } else if (editingArtist && editingArtist.avatar_url) {
            data.append('avatar_url', editingArtist.avatar_url); // Keep old URL if editing and no new upload
        } else {
            data.append('avatar_url', '');
        }

        onSubmit(data, {
            id: displayId,
            name: name.trim(),
            bio: bio.trim(),
            avatarPreview: avatarPreview // pass preview back for instant mock update
        });

        // If not editing, clear fields. If editing, parent will handle closing/resetting.
        if (!editingArtist) {
            setName('');
            setBio('');
            setAvatarFile(null);
            setAvatarPreview('');
        }
    };

    const displayId = editingArtist ? editingArtist.id : nextId;

    return (
        <Card className="shadow-lg border border-border sticky top-20 bg-white">
            <CardHeader className="pb-4">
                <CardTitle className="text-lg font-semibold text-foreground">
                    {editingArtist ? `Edit Artist: ${editingArtist.id}` : "Create New Artist"}
                </CardTitle>
                <CardDescription>
                    {editingArtist ? "Modify artist profile details." : "Add a new artist to the platform directory."}
                </CardDescription>
            </CardHeader>
            <CardContent>
                <form onSubmit={handleSubmit} className="space-y-4">
                    {/* ID Field (Read-only) */}
                    <div className="space-y-1.5">
                        <label className="text-xs font-semibold uppercase tracking-wider text-muted-foreground">
                            Artist ID
                        </label>
                        <Input
                            value={displayId}
                            disabled
                            className="bg-muted text-muted-foreground select-all font-mono font-bold tracking-wide"
                        />
                    </div>

                    {/* Name Field */}
                    <div className="space-y-1.5">
                        <label className="text-xs font-semibold uppercase tracking-wider text-muted-foreground flex items-center gap-1">
                            Name <span className="text-rose-500">*</span>
                        </label>
                        <Input
                            value={name}
                            onChange={(e) => setName(e.target.value)}
                            placeholder="e.g. Sơn Tùng M-TP"
                            maxLength={100}
                            className="bg-muted/30 focus-visible:bg-white"
                            required
                            disabled={isLoading}
                        />
                    </div>

                    {/* Avatar Upload Field */}
                    <div className="space-y-1.5">
                        <label className="text-xs font-semibold uppercase tracking-wider text-muted-foreground block">
                            Avatar Image
                        </label>
                        
                        <div className="flex items-center gap-4 py-1.5">
                            {/* Avatar Preview circle */}
                            <div className="relative group shrink-0 w-16 h-16 rounded-full overflow-hidden border border-dashed border-primary/30 bg-muted/40 flex items-center justify-center">
                                {avatarPreview || (editingArtist && editingArtist.avatar_url) ? (
                                    <img
                                        src={avatarPreview || (editingArtist && editingArtist.avatar_url)}
                                        alt="Preview"
                                        className="w-full h-full object-cover"
                                    />
                                ) : (
                                    <User className="text-muted-foreground/60 w-6 h-6" />
                                )}
                            </div>

                            <div className="flex-1">
                                <input
                                    type="file"
                                    id="avatar-file"
                                    accept="image/*"
                                    onChange={handleFileChange}
                                    className="hidden"
                                    disabled={isLoading}
                                />
                                <label
                                    htmlFor="avatar-file"
                                    className={`inline-flex items-center gap-1.5 px-3.5 py-2 border border-border rounded-3xl bg-white hover:bg-muted text-xs font-medium cursor-pointer transition-colors shadow-sm select-none ${
                                        isLoading ? 'pointer-events-none opacity-50' : ''
                                    }`}
                                >
                                    <Upload size={14} className="text-muted-foreground" />
                                    Choose Image File
                                </label>
                                
                                <p className="text-xs text-muted-foreground mt-1.5 truncate max-w-[180px]">
                                    {avatarFile ? avatarFile.name : (editingArtist && editingArtist.avatar_url ? "Existing image loaded" : "No file selected")}
                                </p>
                                
                                {avatarFile && !isLoading && (
                                    <button
                                        type="button"
                                        onClick={handleRemoveSelectedFile}
                                        className="text-xs text-rose-500 hover:underline mt-0.5 block cursor-pointer"
                                    >
                                        Remove selected file
                                    </button>
                                )}
                            </div>
                        </div>
                    </div>

                    {/* Bio Field */}
                    <div className="space-y-1.5">
                        <label className="text-xs font-semibold uppercase tracking-wider text-muted-foreground">
                            Biography
                        </label>
                        <textarea
                            value={bio}
                            onChange={(e) => setBio(e.target.value)}
                            placeholder="Describe the artist's history, style, and achievements..."
                            rows={4}
                            maxLength={1000}
                            disabled={isLoading}
                            className="w-full bg-muted/30 focus-visible:bg-white rounded-3xl border border-transparent p-3 text-sm focus:outline-none focus:border-ring focus:ring-3 focus:ring-ring/30 transition-all min-h-[100px] resize-none"
                        />
                    </div>

                    {/* Action Buttons */}
                    <div className="flex gap-2 pt-2">
                        <Button
                            type="submit"
                            disabled={isLoading || !name.trim()}
                            className="flex-1 rounded-3xl h-10 font-semibold gap-1.5 flex items-center justify-center cursor-pointer"
                        >
                            {isLoading ? (
                                <Loader2 size={16} className="animate-spin" />
                            ) : editingArtist ? (
                                <Plus size={16} className="rotate-45" />
                            ) : (
                                <Plus size={16} />
                            )}
                            {isLoading ? "Saving..." : editingArtist ? "Save Changes" : "Add Artist"}
                        </Button>

                        {editingArtist && !isLoading && (
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
