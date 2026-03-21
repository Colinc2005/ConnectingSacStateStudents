import { useAuth } from '../context/AuthContext';
import { LayoutDashboard, MessageSquare, User, LogOut } from 'lucide-react';

export default function Dashboard() {
  const { user, logout } = useAuth();

  return (
    <div className="min-h-screen bg-ss-black text-white flex">
      <aside className="w-64 bg-ss-card border-r border-white/5 p-6 flex flex-col hidden md:flex">
        <h2 className="text-ss-gold font-black text-2xl mb-10 tracking-tighter uppercase">Connect</h2>
        <nav className="flex-1 space-y-4">
          <div className="flex items-center gap-4 text-ss-gold font-bold bg-ss-green/10 p-3 rounded-xl"><LayoutDashboard size={20}/> Dashboard</div>
          <div className="flex items-center gap-4 text-gray-500 p-3 hover:text-white transition cursor-pointer"><User size={20}/> Profile</div>
        </nav>
        <button onClick={logout} className="text-red-400 flex items-center gap-4 p-3 hover:bg-red-400/10 rounded-xl transition font-bold"><LogOut size={20}/> Logout</button>
      </aside>

      <main className="flex-1 p-6 lg:p-10">
        <header className="mb-10">
          <h1 className="text-4xl font-black">Welcome back, <span className="text-ss-gold">Hornet!</span></h1>
          <p className="text-gray-500 font-medium">{user?.email}</p>
        </header>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-6 auto-rows-[200px]">
          <div className="md:col-span-2 row-span-2 bg-ss-card border border-white/10 rounded-3xl p-8 shadow-xl relative overflow-hidden">
            <div className="absolute top-0 right-0 w-32 h-32 bg-ss-green/10 blur-3xl -mr-10 -mt-10"></div>
            <h3 className="text-ss-gold font-bold text-xs uppercase tracking-widest mb-6">Live Campus Feed</h3>
            <div className="bg-white/5 border border-white/5 p-10 rounded-2xl text-gray-500 italic text-center">No activity yet. Start the conversation!</div>
          </div>
          
          <div className="bg-ss-green rounded-3xl p-8 flex flex-col justify-end shadow-xl shadow-ss-green/10">
            <User className="text-ss-gold mb-2" size={32} />
            <h4 className="text-xl font-bold">Stinger Status</h4>
            <p className="text-white/60 text-sm font-medium">Profile Verified ✅</p>
          </div>

          <div className="bg-white/5 border border-white/10 rounded-3xl p-8 flex flex-col justify-center text-center group cursor-pointer hover:border-ss-gold transition">
            <p className="text-ss-gold font-bold uppercase text-xs tracking-widest mb-1">Coming Soon</p>
            <p className="text-lg font-bold">Private Messages</p>
          </div>
        </div>
      </main>
    </div>
  );
}