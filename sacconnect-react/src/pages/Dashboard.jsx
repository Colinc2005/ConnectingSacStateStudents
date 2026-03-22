import { useAuth } from '../context/AuthContext';
import { LayoutDashboard, MessageSquare, User, LogOut, GraduationCap, BookOpen } from 'lucide-react';
import { Link } from 'react-router-dom';

export default function Dashboard() {
  const { user, logout } = useAuth();

  return (
    <div className="min-h-screen bg-[#004e38] text-white flex font-sans">
      <aside className="w-72 bg-ss-card border-r border-white/5 p-8 flex flex-col hidden lg:flex">
        <h2 className="text-ss-gold font-black text-3xl mb-12 tracking-tighter uppercase italic">Connect</h2>
        <nav className="flex-1 space-y-4">
          <Link to="/" className="flex items-center gap-4 text-ss-gold font-bold bg-ss-green/10 p-4 rounded-2xl border border-ss-gold/10 shadow-lg shadow-ss-green/5">
            <LayoutDashboard size={22}/> Dashboard
          </Link>
          <Link to="/majors" className="flex items-center gap-4 text-gray-400 p-4 hover:text-white transition-all group">
            <BookOpen size={22} className="group-hover:text-ss-gold transition-colors"/> Majors
          </Link>
          <Link to="/mentorship" className="flex items-center gap-4 text-gray-400 p-4 hover:text-white transition-all group">
            <GraduationCap size={22} className="group-hover:text-ss-gold transition-colors"/> Mentorship
          </Link>
        </nav>
        <button onClick={logout} className="text-red-400/60 flex items-center gap-4 p-4 hover:bg-red-400/10 hover:text-red-400 rounded-2xl transition-all font-bold">
          <LogOut size={22}/> Sign Out
        </button>
      </aside>

      <main className="flex-1 p-8 lg:p-16 overflow-y-auto">
        <header className="mb-12 flex justify-between items-end gap-6">
          <div>
            <h1 className="text-5xl font-black tracking-tight mb-2 uppercase italic">Welcome back, <span className="text-ss-gold underline decoration-white underline-offset-8">Hornet!</span></h1>
            <p className="text-white/70 font-medium text-lg tracking-tight italic">{user?.email}</p>
          </div>
          <Link to="/profile" className="p-4 px-6 bg-white/5 rounded-2xl border border-white/10 hover:border-ss-gold transition group flex items-center gap-3">
            <User className="text-gray-400 group-hover:text-ss-gold" />
            <span className="text-sm font-bold text-gray-400 group-hover:text-ss-gold hidden md:block uppercase tracking-widest">My Profile</span>
          </Link>
        </header>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-8 auto-rows-[240px]">
          <div className="md:col-span-2 row-span-2 bg-ss-card border border-white/10 rounded-[3rem] p-10 shadow-2xl relative overflow-hidden group">
            <div className="absolute top-0 right-0 w-64 h-64 bg-ss-green/5 blur-[100px] group-hover:bg-ss-green/10 transition-all"></div>
            <h3 className="text-ss-gold font-bold text-[10px] uppercase tracking-[0.3em] mb-8">Live Campus Feed</h3>
            <div className="h-full flex flex-col justify-center items-center text-center opacity-40">
              <MessageSquare size={48} className="mb-4 text-ss-green" />
              <p className="text-lg italic font-medium">Scanning for active threads...</p>
            </div>
          </div>
          
          <Link to="/majors" className="md:col-span-1 row-span-1 bg-white/5 border border-white/10 rounded-[2.5rem] p-8 flex flex-col justify-between hover:border-ss-gold transition cursor-pointer">
            <h4 className="text-ss-gold font-bold uppercase text-[10px] tracking-widest">Featured Session</h4>
            <p className="text-2xl font-black leading-tight italic uppercase">Ben Chong: <br/>Scheduling Workshop</p>
          </Link>

          <div className="md:col-span-1 row-span-1 bg-ss-green/10 border border-ss-green/20 rounded-[2.5rem] p-8 flex flex-col justify-center text-center group">
            <p className="text-ss-gold font-black text-5xl mb-1 italic">0</p>
            <p className="text-[10px] text-white/40 uppercase tracking-[0.3em] font-black">Unread Notifications</p>
          </div>
        </div>
      </main>
    </div>
  );
}