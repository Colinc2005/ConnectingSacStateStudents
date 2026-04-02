import { useState } from 'react';
import { BookOpen, GraduationCap, Award, CheckCircle, ArrowLeft, Target, Briefcase, Clock } from 'lucide-react';
import { Link } from 'react-router-dom';

export default function Mentorship() {
  const [selectedSlot, setSelectedSlot] = useState(null);
  const [booked, setBooked] = useState(false);
  const mentor = { name: "Ben Chong", role: "Graduate Mentor", dept: "Computer Science", coaching: ["CSC 130", "CSC 138", "CSC 134"], skills: ["Planning", "Career Paths", "Interviewing"], slots: ["Mon 2:00 PM", "Mon 3:30 PM", "Wed 10:00 AM", "Thu 4:00 PM", "Fri 1:00 PM"] };

  if (booked) {
    return (
      <div className="min-h-screen bg-[#004e38] flex items-center justify-center p-6 text-center font-sans">
        <div className="bg-ss-card border border-ss-green/30 p-12 rounded-[3rem] shadow-2xl max-w-lg">
          <CheckCircle className="text-ss-green mx-auto mb-6" size={80} />
          <h2 className="text-3xl font-black uppercase mb-2 tracking-tighter italic">Requested!</h2>
          <p className="text-white/70 mb-8 italic">Your request for <span className="text-ss-gold font-bold">{selectedSlot}</span> has been sent.</p>
          <button onClick={() => setBooked(false)} className="w-full bg-ss-green text-white py-4 rounded-2xl font-bold hover:bg-ss-green/80 transition">Return</button>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-[#004e38] text-white p-6 md:p-12 font-sans selection:bg-ss-gold selection:text-ss-black">
      <Link to="/" className="fixed top-10 left-10 text-white/50 hover:text-ss-gold transition z-50"><ArrowLeft size={28} /></Link>
      <header className="max-w-7xl mx-auto mb-16 pt-12">
        <h1 className="text-6xl font-black uppercase tracking-tighter italic">Hornet <span className="text-ss-gold underline decoration-white underline-offset-8">Mentorship</span></h1>
        <p className="text-white/70 font-medium mt-4 text-lg italic tracking-tight">1-1 Coaching to master your classes and your career.</p>
      </header>
      <main className="max-w-7xl mx-auto grid grid-cols-1 lg:grid-cols-3 gap-8">
        <div className="lg:col-span-2 space-y-8">
          <div className="bg-ss-card p-12 rounded-[3rem] border border-white/10 shadow-2xl relative overflow-hidden flex flex-col md:flex-row items-center gap-10">
            <div className="w-48 h-48 rounded-[2.5rem] overflow-hidden border-2 border-ss-gold/30 shadow-2xl relative z-10"><img src="https://cdn.discordapp.com/attachments/1484618688098205811/1485020741715099719/1759810233240.jpeg?ex=69c058d7&is=69bf0757&hm=3dbb39feaea42177edbcab9fb0ca0d76b5b2f2fd0003d0c22779f7c38aa4b508&" alt="Ben Chong" className="w-full h-full object-cover" /></div>
            <div className="flex-1 text-center md:text-left relative z-10"><h2 className="text-5xl font-black tracking-tighter mb-2 uppercase italic">{mentor.name}</h2><p className="text-ss-gold font-bold flex items-center gap-2 justify-center md:justify-start text-lg italic"><GraduationCap size={20}/> {mentor.dept} Graduate</p></div>
          </div>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
            <div className="bg-ss-card p-10 rounded-[2.5rem] border border-white/10 shadow-xl"><h4 className="flex items-center gap-3 text-ss-gold font-bold uppercase tracking-[0.2em] text-[10px] mb-8 italic"><Target size={18}/> Coaching</h4><div className="space-y-4">{mentor.coaching.map((course, i) => (<div key={i} className="flex items-center gap-4 bg-white/5 p-4 rounded-2xl border border-white/5"><BookOpen size={18} className="text-ss-green" /><span className="font-bold text-sm tracking-tight">{course}</span></div>))}</div></div>
            <div className="bg-ss-card p-10 rounded-[2.5rem] border border-white/10 shadow-xl"><h4 className="flex items-center gap-3 text-ss-gold font-bold uppercase tracking-[0.2em] text-[10px] mb-8 italic"><Briefcase size={18}/> Strategy</h4><div className="space-y-4">{mentor.skills.map((skill, i) => (<div key={i} className="flex items-center gap-4 bg-white/5 p-4 rounded-2xl border border-white/5"><Award size={18} className="text-ss-gold" /><span className="font-bold text-sm tracking-tight italic">{skill}</span></div>))}</div></div>
          </div>
        </div>
        <div className="lg:col-span-1 bg-white/5 border border-white/10 rounded-[3rem] p-10 shadow-2xl flex flex-col h-full"><h4 className="flex items-center gap-3 text-ss-gold font-bold uppercase tracking-[0.2em] text-[10px] mb-8 italic"><Clock size={18}/> Slots</h4><div className="grid grid-cols-1 gap-4 flex-1">{mentor.slots.map((slot) => (<button key={slot} onClick={() => setSelectedSlot(slot)} className={`py-5 rounded-2xl font-black text-xs uppercase tracking-widest transition-all border ${selectedSlot === slot ? 'bg-ss-gold text-ss-black border-ss-gold shadow-2xl' : 'bg-ss-card text-gray-500 border-white/5 hover:border-ss-gold/40'}`}>{slot}</button>))}</div><button disabled={!selectedSlot} onClick={() => setBooked(true)} className="w-full mt-10 bg-ss-green text-white font-black py-5 rounded-2xl disabled:opacity-20 shadow-xl uppercase tracking-widest text-xs">Request Session</button></div>
      </main>
    </div>
  );
}