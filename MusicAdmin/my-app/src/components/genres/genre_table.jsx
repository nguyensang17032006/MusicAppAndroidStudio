import React, { useState, useMemo } from 'react';
import { Card, CardHeader, CardTitle, CardDescription, CardContent } from "@/components/ui/card.jsx";
import { Input } from "@/components/ui/input.jsx";
import { Button } from "@/components/ui/button.jsx";
import { Search, X, Tags, Edit2, Trash2 } from "lucide-react";

export default function GenreTable({
    genres,
    onEdit,
    onDelete,
    editingId
}) {
    const [searchTerm, setSearchTerm] = useState('');

    const filteredGenres = useMemo(() => {
        return genres.filter(genre =>
            genre.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
            genre.id.toLowerCase().includes(searchTerm.toLowerCase())
        );
    }, [genres, searchTerm]);

    return (
        <Card className="shadow-lg border border-border bg-white h-full flex flex-col">
            <CardHeader className="pb-3 flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4 shrink-0">
                <div>
                    <CardTitle className="text-lg font-semibold text-foreground">Registry Records</CardTitle>
                    <CardDescription>All registered genres ({filteredGenres.length} showing)</CardDescription>
                </div>
                
                {/* Search bar */}
                <div className="relative w-full sm:w-60">
                    <Search className="absolute left-3 top-1/2 -translate-y-1/2 text-muted-foreground w-4 h-4" />
                    <Input
                        placeholder="Search by ID or name..."
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
                    {filteredGenres.length === 0 ? (
                        <div className="py-16 text-center">
                            <Tags className="mx-auto h-12 w-12 text-muted-foreground/45 bg-muted rounded-full p-2.5 mb-3" />
                            <h3 className="text-base font-semibold text-foreground">No genres found</h3>
                            <p className="text-sm text-muted-foreground mt-1 px-4">
                                {searchTerm 
                                    ? "Try adjusting your search keywords to locate the genre record."
                                    : "Start by filling out the creation form to register a genre."
                                }
                            </p>
                        </div>
                    ) : (
                        <table className="w-full text-left border-collapse text-sm">
                            <thead>
                                <tr className="bg-muted/40 border-b border-border/80 text-muted-foreground text-xs uppercase tracking-wider font-semibold">
                                    <th className="py-3 px-4 font-semibold hidden sm:table-cell">ID</th>
                                    <th className="py-3 px-4 font-semibold">Genre Name</th>
                                    <th className="py-3 px-4 font-semibold text-right">Actions</th>
                                </tr>
                            </thead>
                            <tbody className="divide-y divide-border/60">
                                {filteredGenres.map((genre) => (
                                    <tr 
                                        key={genre.id} 
                                        className={`hover:bg-muted/20 transition-colors ${
                                            editingId === genre.id ? 'bg-primary/5 hover:bg-primary/5' : ''
                                        }`}
                                    >
                                        {/* ID (Desktop/Mobile) */}
                                        <td className="py-3.5 px-4 font-mono font-semibold text-muted-foreground">
                                            {genre.id}
                                        </td>

                                        {/* Genre Name */}
                                        <td className="py-3.5 px-4 font-semibold text-foreground">
                                            {genre.name}
                                        </td>

                                        {/* Action Buttons */}
                                        <td className="py-3.5 px-4 text-right">
                                            <div className="flex items-center justify-end gap-1">
                                                <Button
                                                    type="button"
                                                    variant="ghost"
                                                    size="icon"
                                                    onClick={() => onEdit(genre)}
                                                    className={`size-8 rounded-full cursor-pointer ${
                                                        editingId === genre.id 
                                                            ? 'text-primary bg-primary/10'
                                                            : 'text-muted-foreground hover:text-foreground'
                                                    }`}
                                                    title="Edit Genre"
                                                >
                                                    <Edit2 size={14} />
                                                </Button>
                                                <Button
                                                    type="button"
                                                    variant="ghost"
                                                    size="icon"
                                                    onClick={() => onDelete(genre.id)}
                                                    className="size-8 rounded-full text-muted-foreground hover:text-rose-600 hover:bg-rose-50 dark:hover:bg-rose-950/20 cursor-pointer"
                                                    title="Delete Genre"
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
