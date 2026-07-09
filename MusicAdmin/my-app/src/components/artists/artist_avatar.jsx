import React, { useState, useEffect } from 'react';

export default function ArtistAvatar({ src, name }) {
    const [hasError, setHasError] = useState(false);
    const initials = name ? name.split(' ').map(n => n[0]).join('').slice(0, 2).toUpperCase() : 'A';

    useEffect(() => {
        setHasError(false);
    }, [src]);

    if (hasError || !src) {
        return (
            <div className="w-10 h-10 rounded-full bg-primary/10 text-primary border border-primary/20 flex items-center justify-center font-semibold text-xs shrink-0 select-none">
                {initials}
            </div>
        );
    }

    return (
        <img
            src={src}
            alt={name}
            onError={() => setHasError(true)}
            className="w-10 h-10 rounded-full object-cover shrink-0 border border-border"
        />
    );
}
