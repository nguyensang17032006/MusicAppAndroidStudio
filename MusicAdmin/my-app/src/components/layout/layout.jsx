import { Outlet } from 'react-router-dom'
import Header from './Header.jsx'
import Sidebar from './Sidebar.jsx'

export default function Layout() {
    return (
        <div className="grid h-screen grid-cols-[240px_1fr] grid-rows-[64px_1fr]">

            {/* HEADER */}
            <header className="col-span-2 bg-black text-white">
                <Header />
            </header>

            {/* SIDEBAR */}
            <aside className="bg-slate-900 text-white h-full">
                <Sidebar />
            </aside>

            {/* MAIN */}
            <main className="p-6 overflow-auto bg-gray-100 h-full">
                <Outlet />
            </main>

        </div>
    )
}