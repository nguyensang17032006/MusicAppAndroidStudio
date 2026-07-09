import { useState, useEffect } from 'react';
import { Music2, Menu } from "lucide-react";
import { Input } from "@/components/ui/input.jsx";
import { Button } from "@/components/ui/button.jsx";

export default function Header({ onMenuClick }) {
    const [adminName, setAdminName] = useState('Sáng');

    useEffect(() => {
        const name = localStorage.getItem('music_admin_name');
        if (name) setAdminName(name);

        // Listen for profile name update events
        const handleProfileChange = () => {
            const updatedName = localStorage.getItem('music_admin_name');
            if (updatedName) setAdminName(updatedName);
        };
        window.addEventListener('music_admin_profile_update', handleProfileChange);
        return () => window.removeEventListener('music_admin_profile_update', handleProfileChange);
    }, []);

    return (
        <header className="bg-primary h-16 flex items-center px-4 md:px-6 shadow-md text-white sticky top-0 z-20">
            <div className="flex items-center gap-2 md:gap-3">
                {/* Menu Button for Mobile */}
                <Button
                    variant="ghost"
                    size="icon"
                    className="md:hidden text-white hover:bg-white/10"
                    onClick={onMenuClick}
                    aria-label="Toggle Sidebar"
                >
                    <Menu className="!size-6" />
                </Button>

                <div className="flex items-center gap-2 text-lg md:text-xl font-semibold">
                    <Music2 size={24} className="md:w-7 md:h-7" />
                    <span className="hidden min-[400px]:inline">Music Admin</span>
                </div>
            </div>


        </header>
    );
}
