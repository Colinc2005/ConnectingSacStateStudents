import { useAuth } from '../context/AuthContext';
import { LayoutDashboard, MessageSquare, User, LogOut, Settings } from 'lucide-react';
import { Link } from 'react-router-dom';

export default function Dashboard() {
  const { user, logout } = useAuth();

  return (
    <div className="min-h-screen bg-ss-black text-white flex font-sans">
      <aside className="w-72 bg-ss-card border-r border-white/5 p-8 flex flex-col hidden lg:flex">
        <h2 className="text-ss-gold font-black text-3xl mb-12 tracking-tighter uppercase italic">Connect</h2>
        <nav className="flex-1 space-y-4">
          <div className="flex items-center gap-4 text-ss-gold font-bold bg-ss-green/10 p-4 rounded-2xl border border-ss-gold/10"><LayoutDashboard size={22}/> Dashboard</div>
          <Link to="/edit-profile" className="flex items-center gap-4 text-gray-500 p-4 hover:text-white transition-all"><Settings size={22}/> Edit Profile</Link>
          <div className="flex items-center gap-4 text-gray-500 p-4 hover:text-white transition-all"><MessageSquare size={22}/> Messages</div>
        </nav>
        <button onClick={logout} className="text-red-400/60 flex items-center gap-4 p-4 hover:bg-red-400/10 hover:text-red-400 rounded-2xl transition-all font-bold"><LogOut size={22}/> Sign Out</button>
      </aside>

      <main className="flex-1 p-8 lg:p-16 overflow-y-auto">
        <header className="mb-12 flex justify-between items-end">
          <div>
            <h1 className="text-5xl font-black tracking-tight mb-2">Sup, <span className="text-ss-gold underline decoration-ss-green underline-offset-8">Hornet?</span></h1>
            <p className="text-gray-500 font-medium text-lg">{user?.email}</p>
          </div>
          <Link to="/edit-profile" className="p-4 bg-white/5 rounded-2xl border border-white/10 hover:border-ss-gold transition group">
            <User className="text-gray-500 group-hover:text-ss-gold" />
          </Link>
        </header>

        {/* Modular Bento Grid */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-8 auto-rows-[240px]">
          <div className="md:col-span-2 row-span-2 bg-ss-card border border-white/10 rounded-[3rem] p-10 shadow-2xl relative overflow-hidden group">
            <div className="absolute top-0 right-0 w-64 h-64 bg-ss-green/5 blur-[100px] group-hover:bg-ss-green/10 transition-all"></div>
            <h3 className="text-ss-gold font-bold text-sm uppercase tracking-[0.2em] mb-8">Live Campus Feed</h3>
            <div className="h-full flex flex-col justify-center items-center text-center opacity-40">
              <MessageSquare size={48} className="mb-4 text-ss-green" />
              <p className="text-lg italic font-medium">Scanning for active threads...</p>
            </div>
          </div>
          
          <div className="md:col-span-1 row-span-1 bg-white/5 border border-white/10 rounded-[2.5rem] p-8 flex flex-col justify-between hover:border-ss-gold transition cursor-default">
            <h4 className="text-ss-gold font-bold uppercase text-xs tracking-widest">Coming Up</h4>
            <p className="text-2xl font-black leading-tight">Spring Finals Prep Chatroom</p>
          </div>

          <div className="md:col-span-1 row-span-1 bg-ss-green/10 border border-ss-green/20 rounded-[2.5rem] p-8 flex flex-col justify-center text-center group">
            <p className="text-ss-gold font-black text-4xl mb-1">0</p>
            <p className="text-xs text-white/40 uppercase tracking-widest font-bold">New Messages</p>
          </div>
        </div>
      </main>
    </div>
  );
}