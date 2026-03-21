import { useEffect, useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { Mail, GraduationCap, TextQuote, Hash, Settings, ArrowLeft, UserPlus, Users } from 'lucide-react';
import { Link, useNavigate } from 'react-router-dom';

export default function ViewProfile() {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [profile, setProfile] = useState(null);
  const [suggestions, setSuggestions] = useState([]); // State for other students
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    async function loadData() {
      if (!user?.id) {
        setError("Missing logged-in user.");
        setLoading(false);
        return;
      }
      
      try {
        // 1. Fetch current user profile
        const profileResp = await fetch(`http://localhost:8080/api/users/${user.id}`);
        if (profileResp.ok) {
          const data = await profileResp.json();
          setProfile(data);
        } else {
          setError("Could not load your profile.");
        }

        // 2. Fetch suggested connections (People with similar interests/major)
        const suggestResp = await fetch(`http://localhost:8080/api/users/suggestions?userId=${user.id}`);
        if (suggestResp.ok) {
          const suggestData = await suggestResp.json();
          setSuggestions(suggestData);
        } else {
          // Fallback mock data if your backend endpoint isn't ready yet
          setSuggestions([
            { id: 101, name: "Sami", major: "Computer Science", bio: "Dutch Bros addict." },
            { id: 102, name: "Alex", major: "Mechanical Engineering", bio: "Looking for study groups." },
            { id: 103, name: "Jordan", major: "Business", bio: "Let's connect!" }
          ]);
        }
      } catch (e) {
        setError("Connection error. Is the Spring Boot server on?");
      } finally {
        setLoading(false);
      }
    }
    loadData();
  }, [user?.id]);

  if (loading) {
    return (
      <div className="min-h-screen bg-ss-black flex items-center justify-center text-ss-gold italic">
        Gathering Hornet data...
      </div>
    );
  }

  if (error && !profile) {
    return (
      <div className="min-h-screen bg-ss-black flex flex-col items-center justify-center gap-4 text-center px-6">
        <p className="text-red-400 font-bold">{error}</p>
        <button
          onClick={() => navigate('/edit-profile')}
          className="px-6 py-3 rounded-xl bg-ss-green text-white font-bold hover:bg-ss-green/80 transition"
        >
          Go to Edit Profile
        </button>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-ss-black text-white p-6 md:p-12 font-sans selection:bg-ss-gold selection:text-ss-black">
      <Link to="/" className="fixed top-10 left-10 text-gray-700 hover:text-ss-gold transition z-50">
        <ArrowLeft size={28} />
      </Link>
      
      <header className="max-w-7xl mx-auto flex flex-col md:flex-row justify-between items-end mb-12 gap-6 pt-12 md:pt-0">
        <div>
          <h1 className="text-6xl font-black uppercase tracking-tighter italic leading-none">Your <span className="text-ss-gold underline decoration-ss-green underline-offset-8">Card</span></h1>
          <p className="text-gray-500 font-medium mt-4">How you appear to the Sac State community</p>
        </div>
        <Link to="/edit-profile" className="p-4 px-8 bg-ss-green rounded-2xl border border-ss-green/20 hover:bg-ss-green/80 transition flex items-center gap-3 font-bold text-sm shadow-lg shadow-ss-green/20">
          <Settings size={18} /> Edit Profile
        </Link>
      </header>

      <main className="max-w-7xl mx-auto grid grid-cols-1 md:grid-cols-3 gap-8">
        
        {/* Profile Header */}
        <div className="md:col-span-3 bg-ss-card p-12 rounded-[3rem] border border-white/10 shadow-2xl flex flex-col md:flex-row items-center gap-12 relative overflow-hidden">
          <div className="absolute top-0 right-0 w-96 h-96 bg-ss-green/5 blur-[120px] -mr-20 -mt-20"></div>
          <div className="w-40 h-40 rounded-full bg-ss-green/20 flex items-center justify-center text-ss-gold border-4 border-ss-green/30 text-7xl font-black shadow-inner">
            {profile?.name?.[0]?.toUpperCase()}
          </div>
          <div className="text-center md:text-left">
            <h2 className="text-7xl font-black tracking-tighter mb-2 uppercase">{profile?.name}</h2>
            <div className="flex items-center gap-2 text-ss-gold font-bold bg-ss-green/10 px-5 py-2 rounded-full inline-flex border border-ss-gold/10 text-sm">
              <Mail size={16} /> {profile?.email}
            </div>
          </div>
        </div>

        {/* Left Column: Stats & Bio */}
        <div className="md:col-span-2 space-y-8">
          <div className="bg-ss-card p-10 rounded-[2.5rem] border border-white/10 shadow-xl group hover:border-ss-gold/20 transition-all">
            <h4 className="flex items-center gap-3 text-ss-gold font-bold uppercase tracking-[0.2em] text-[10px] mb-6"><GraduationCap size={16}/> Current Major</h4>
            <p className="text-4xl font-black tracking-tight">{profile?.major || "Undeclared"}</p>
          </div>
          
          <div className="bg-ss-card p-10 rounded-[2.5rem] border border-white/10 shadow-xl group hover:border-ss-gold/20 transition-all">
            <h4 className="flex items-center gap-3 text-ss-gold font-bold uppercase tracking-[0.2em] text-[10px] mb-6"><TextQuote size={16}/> The Vibe</h4>
            <p className="text-xl text-gray-300 font-medium italic leading-relaxed">
              "{profile?.bio || "This Hornet is mysterious. No bio yet."}"
            </p>
          </div>

          <div className="bg-ss-card p-10 rounded-[2.5rem] border border-white/10 shadow-xl group hover:border-ss-gold/20 transition-all">
            <h4 className="flex items-center gap-3 text-ss-gold font-bold uppercase tracking-[0.2em] text-[10px] mb-8"><Hash size={16}/> Interests</h4>
            <div className="flex flex-wrap gap-3">
              {profile?.interests?.map((tag, i) => (
                <span key={i} className="px-5 py-2 bg-ss-green/5 border border-ss-green/20 rounded-xl text-ss-gold font-bold text-xs uppercase tracking-widest">
                  #{tag}
                </span>
              ))}
            </div>
          </div>
        </div>

        {/* Right Column: Suggestions */}
        <div className="md:col-span-1 space-y-8">
          <div className="bg-white/5 border border-white/10 rounded-[2.5rem] p-10 shadow-2xl">
            <h4 className="flex items-center gap-3 text-ss-gold font-bold uppercase tracking-[0.2em] text-[10px] mb-8">
              <Users size={16}/> Suggested Hornets
            </h4>
            <div className="space-y-6">
              {suggestions.map((suggest) => (
                <div key={suggest.id} className="flex items-center justify-between group p-2 -mx-2 hover:bg-white/5 rounded-2xl transition-all cursor-pointer">
                  <div className="flex items-center gap-4">
                    <div className="w-12 h-12 rounded-xl bg-ss-green/20 border border-ss-green/20 flex items-center justify-center text-ss-gold font-black">
                      {suggest.name[0]}
                    </div>
                    <div>
                      <p className="font-bold text-sm">{suggest.name}</p>
                      <p className="text-[10px] text-gray-500 font-bold uppercase tracking-tighter">{suggest.major}</p>
                    </div>
                  </div>
                  <UserPlus size={18} className="text-gray-700 group-hover:text-ss-gold transition-colors" />
                </div>
              ))}
            </div>
            <button className="w-full mt-10 py-4 border border-white/10 rounded-2xl text-[10px] font-black uppercase tracking-widest text-gray-500 hover:text-ss-gold hover:border-ss-gold transition-all">
              Discover More
            </button>
          </div>

          <div className="bg-ss-green p-10 rounded-[2.5rem] shadow-xl shadow-ss-green/10 flex flex-col justify-center text-center">
            <p className="text-ss-gold font-black text-xs uppercase tracking-widest mb-2">Campus Hub</p>
            <p className="text-lg font-bold text-white leading-tight">Join the Computer Engineering study hall.</p>
          </div>
        </div>

      </main>
    </div>
  );
}
