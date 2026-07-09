import { useState } from 'react'
import { Outlet } from 'react-router-dom'
import Header from './header.jsx'
import Sidebar from './sidebar.jsx'

export default function Layout() {
    const [isSidebarOpen, setIsSidebarOpen] = useState(false);

    return (
        <div className="grid h-screen grid-cols-1 md:grid-cols-[240px_1fr] grid-rows-[64px_1fr] overflow-hidden">

            {/* HEADER */}
            <header className="col-span-1 md:col-span-2 bg-black text-white z-20">
                <Header onMenuClick={() => setIsSidebarOpen(true)} />
            </header>

            {/* SIDEBAR ON DESKTOP */}
            <Sidebar className="hidden md:block border-r border-border w-60" />

            {/* MOBILE SIDEBAR DRAWER */}
            {/* Backdrop */}
            <div
                className={`fixed inset-0 bg-black/50 z-30 transition-opacity duration-300 md:hidden ${
                    isSidebarOpen ? 'opacity-100 pointer-events-auto' : 'opacity-0 pointer-events-none'
                }`}
                onClick={() => setIsSidebarOpen(false)}
            />

            {/* Sidebar drawer content */}
            <div
                className={`fixed top-0 left-0 bottom-0 w-60 bg-white z-40 shadow-xl flex flex-col transform transition-transform duration-300 ease-in-out md:hidden ${
                    isSidebarOpen ? 'translate-x-0' : '-translate-x-full'
                }`}
            >
                {/* Header inside mobile sidebar with close button */}
                <div className="h-16 flex items-center justify-between px-6 border-b border-border bg-primary text-white shrink-0">
                    <span className="font-semibold text-lg">Menu</span>
                    <button
                        onClick={() => setIsSidebarOpen(false)}
                        className="text-white hover:text-white/80 focus:outline-none"
                    >
                        <svg className="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                        </svg>
                    </button>
                </div>
                
                <Sidebar className="w-full flex-1" onItemClick={() => setIsSidebarOpen(false)} />
            </div>

            {/* MAIN */}
            <main className="p-4 md:p-6 overflow-auto bg-gray-100 h-full">
                <Outlet />
            </main>

        </div>
    )
}