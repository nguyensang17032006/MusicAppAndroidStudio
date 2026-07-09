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
import { Plus, X, Loader2 } from "lucide-react";

export default function GenreForm({
    editingGenre,
    nextId,
    onSubmit,
    onCancel,
    isLoading
}) {
    const [name, setName] = useState('');

    // Pre-fill form when entering Edit mode
    useEffect(() => {
        if (editingGenre) {
            setName(editingGenre.name || '');
        } else {
            setName('');
        }
    }, [editingGenre]);

    const handleSubmit = (e) => {
        e.preventDefault();
        if (!name.trim()) return;

        const displayId = editingGenre ? editingGenre.id : nextId;
        onSubmit({
            id: displayId,
            name: name.trim()
        });

        // Reset if adding new
        if (!editingGenre) {
            setName('');
        }
    };

    const displayId = editingGenre ? editingGenre.id : nextId;

    return (
        <Card className="shadow-lg border border-border sticky top-20 bg-white">
            <CardHeader className="pb-4">
                <CardTitle className="text-lg font-semibold text-foreground">
                    {editingGenre ? `Edit Genre: ${editingGenre.id}` : "Create New Genre"}
                </CardTitle>
                <CardDescription>
                    {editingGenre ? "Modify genre registry details." : "Add a new musical genre to the system database."}
                </CardDescription>
            </CardHeader>
            <CardContent>
                <form onSubmit={handleSubmit} className="space-y-4">
                    {/* ID Field (Read-only) */}
                    <div className="space-y-1.5">
                        <label className="text-xs font-semibold uppercase tracking-wider text-muted-foreground">
                            Genre ID
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
                            Genre Name <span className="text-rose-500">*</span>
                        </label>
                        <Input
                            value={name}
                            onChange={(e) => setName(e.target.value)}
                            placeholder="e.g. Pop-Ballad"
                            maxLength={100}
                            className="bg-muted/30 focus-visible:bg-white"
                            required
                            disabled={isLoading}
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
                            ) : editingGenre ? (
                                <Plus size={16} className="rotate-45" />
                            ) : (
                                <Plus size={16} />
                            )}
                            {isLoading ? "Saving..." : editingGenre ? "Save Changes" : "Add Genre"}
                        </Button>

                        {editingGenre && !isLoading && (
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
