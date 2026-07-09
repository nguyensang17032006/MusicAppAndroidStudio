import { BookOpen, PlusCircle, Tags, User, ShoppingCart, Settings } from "lucide-react";
import { NavLink } from "react-router-dom";
import { cn } from "@/lib/utils";

const navItems = [
    { to: "/", label: "Home", icon: BookOpen },
    { to: "/artists", label: "Artists", icon: PlusCircle },
    { to: "/genres", label: "Genres", icon: Tags },
    { to: "/songs", label: "Songs", icon: User },
    { to: "/settings", label: "Settings", icon: Settings },
];

export default function Sidebar({ className, onItemClick }) {
    return (
        <aside className={cn("w-full bg-white h-full overflow-y-auto", className)}>
            <nav className="py-6">
                <ul>
                    {navItems.map((item) => (
                        <li key={item.to}>
                            <NavLink
                                to={item.to}
                                onClick={onItemClick}
                                className={({ isActive }) =>
                                    cn(
                                        "flex items-center gap-3 px-6 py-3 text-sm transition-colors border-l-4",
                                        isActive
                                            ? "border-primary bg-accent text-primary font-medium"
                                            : "border-transparent text-muted-foreground hover:bg-accent hover:text-accent-foreground"
                                    )
                                }
                            >
                                <item.icon size={18} />
                                {item.label}
                            </NavLink>
                        </li>
                    ))}
                </ul>
            </nav>
        </aside>
    );
}