import { useAuth } from '../context/AuthContext';
import { LayoutDashboard, MessageSquare, User, LogOut, GraduationCap, BookOpen, ExternalLink, Newspaper } from 'lucide-react';
import { Link } from 'react-router-dom';

export default function Dashboard() {
  const { user, logout } = useAuth();

  const newsItems = [
    {
      id: 1,
      title: "New documentary featuring Sac State Men's Basketball team premieres May 1",
      date: "March 13, 2026",
      tag: "Athletics",
      summary: "Overtime and Omaha Productions will premiere a six-episode docuseries on The Roku Channel going behind the scenes.",
      url: "https://www.csus.edu/news/newsroom/stories/2026/3/mens-basketball-documentary.html"
    },
    {
      id: 2,
      title: "Student innovators build robotic arm and web app to teach American Sign Language",
      date: "March 13, 2026",
      tag: "Innovation",
      summary: "Sac State students are on six of eight teams competing in Kings Capitalize this year, including creators of Helping Hand.",
      url: "https://www.csus.edu/news/newsroom/stories/2026/3/asl-innovation.html"
    },
    {
      id: 3,
      title: "President Wood projects optimistic future, emphasizes arts in ‘26 Spring Address",
      date: "January 23, 2026",
      tag: "Leadership",
      summary: "Sacramento State made positive strides toward President Luke Wood’s goals for 2025-26 with growing enrollment.",
      url: "https://www.csus.edu/news/newsroom/stories/2026/1/spring-address-2026.html"
    }
  ];

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

        <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
          {/* Full Width News Feed */}
          <div className="md:col-span-3 bg-ss-card border border-white/10 rounded-[3rem] p-10 shadow-2xl relative overflow-hidden group flex flex-col min-h-[500px]">
            <div className="flex justify-between items-center mb-8">
              <h3 className="text-ss-gold font-bold text-[10px] uppercase tracking-[0.3em]">Live Campus Feed</h3>
              <a href="https://www.csus.edu/news/" target="_blank" rel="noreferrer" className="text-white/20 hover:text-ss-gold transition-colors">
                <ExternalLink size={14} />
              </a>
            </div>
            
            <div className="flex-1 space-y-8 overflow-y-auto pr-2 custom-scrollbar">
              {newsItems.map((news) => (
                <div key={news.id} className="group/item border-b border-white/5 pb-8 last:border-0">
                  <div className="flex justify-between items-start gap-4">
                    <div className="flex-1">
                      <div className="flex items-center gap-3 mb-2">
                        <span className="text-[9px] font-black text-ss-green bg-ss-green/20 px-2 py-0.5 rounded uppercase tracking-widest">
                          {news.tag}
                        </span>
                        <span className="text-[9px] text-white/30 font-bold uppercase tracking-widest">{news.date}</span>
                      </div>
                      <a href={news.url} target="_blank" rel="noopener noreferrer" className="block text-2xl font-black leading-tight uppercase italic hover:text-ss-gold transition-colors">
                        {news.title}
                      </a>
                      <p className="text-sm text-white/50 font-medium leading-relaxed italic mt-3">{news.summary}</p>
                    </div>
                    <Newspaper size={24} className="text-white/10 group-hover/item:text-ss-gold transition-colors shrink-0" />
                  </div>
                </div>
              ))}
            </div>
          </div>
          
          {/* Secondary Info Moved Below */}
          <Link to="/majors" className="md:col-span-2 bg-white/5 border border-white/10 rounded-[2.5rem] p-8 flex flex-col justify-between hover:border-ss-gold transition cursor-pointer min-h-[160px]">
            <h4 className="text-ss-gold font-bold uppercase text-[10px] tracking-widest">Featured Session</h4>
            <p className="text-2xl font-black leading-tight italic uppercase">Ben Chong: Scheduling Workshop</p>
          </Link>

          <div className="md:col-span-1 bg-ss-green/10 border border-ss-green/20 rounded-[2.5rem] p-8 flex flex-col justify-center text-center group min-h-[160px]">
            <p className="text-ss-gold font-black text-5xl mb-1 italic">0</p>
            <p className="text-[10px] text-white/40 uppercase tracking-[0.3em] font-black">Unread Notifications</p>
          </div>
        </div>
      </main>
    </div>
  );
}