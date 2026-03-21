import { useState } from 'react';
import { Calendar, Clock, BookOpen, GraduationCap, Award, CheckCircle, ArrowLeft, Target, Briefcase } from 'lucide-react';
import { Link } from 'react-router-dom';

export default function Mentorship() {
  const [selectedSlot, setSelectedSlot] = useState(null);
  const [booked, setBooked] = useState(false);

  const mentor = {
    name: "Ben Chong",
    role: "Graduate Mentor & Career Strategist",
    dept: "Computer Science",
    bio: "I specialize in helping CS students navigate the 'graduation gauntlet.' Whether you're struggling with core theory or trying to align your electives with high-paying career paths, I'm here to ensure you finish strong and job-ready.",
    coaching: ["CSC 130 (Algorithms)", "CSC 138 (Networking)", "CSC 134 (Databases)"],
    skills: ["Strategic Graduation Planning", "Industry-Relevant Career Paths", "Interview Preparation"],
    slots: ["Mon 2:00 PM", "Mon 3:30 PM", "Wed 10:00 AM", "Thu 4:00 PM", "Fri 1:00 PM"]
  };

  const handleBook = () => {
    if (selectedSlot) {
      // In a real app, this would hit your /api/appointments endpoint
      setBooked(true);
    }
  };

  if (booked) {
    return (
      <div className="min-h-screen bg-ss-black flex items-center justify-center p-6 text-center">
        <div className="bg-ss-card border border-ss-green/30 p-12 rounded-[3rem] shadow-2xl max-w-lg">
          <CheckCircle className="text-ss-green mx-auto mb-6" size={80} />
          <h2 className="text-3xl font-black uppercase mb-2 tracking-tighter">Session Requested!</h2>
          <p className="text-gray-400 mb-8 leading-relaxed">
            Your request for <span className="text-ss-gold font-bold">{selectedSlot}</span> has been sent to {mentor.name}. 
            Check your Hornet email for the confirmation link.
          </p>
          <button onClick={() => setBooked(false)} className="w-full bg-ss-green text-white py-4 rounded-2xl font-bold hover:bg-ss-green/80 transition shadow-lg shadow-ss-green/20">
            Return to Mentors
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-ss-black text-white p-6 md:p-12 font-sans selection:bg-ss-gold selection:text-ss-black">
      <Link to="/" className="fixed top-10 left-10 text-gray-700 hover:text-ss-gold transition z-50">
        <ArrowLeft size={28} />
      </Link>
      
      <header className="max-w-7xl mx-auto mb-16 pt-12">
        <h1 className="text-6xl font-black uppercase tracking-tighter italic">Hornet <span className="text-ss-gold underline decoration-ss-green underline-offset-8">Mentorship</span></h1>
        <p className="text-gray-500 font-medium mt-4 text-lg">1-1 Coaching to master your classes and your career.</p>
      </header>

      <main className="max-w-7xl mx-auto grid grid-cols-1 lg:grid-cols-3 gap-8">
        
        {/* Mentor Spotlight Card */}
        <div className="lg:col-span-2 space-y-8">
          <div className="bg-ss-card p-12 rounded-[3rem] border border-white/10 shadow-2xl relative overflow-hidden flex flex-col md:flex-row items-center gap-10">
            <div className="absolute top-0 right-0 w-96 h-96 bg-ss-green/5 blur-[120px] -mr-20 -mt-20"></div>
           <div className="w-48 h-48 rounded-[2.5rem] overflow-hidden border-2 border-ss-gold/30 shadow-2xl">
  <img 
    src="https://cdn.discordapp.com/attachments/1484618688098205811/1485020741715099719/1759810233240.jpeg?ex=69c058d7&is=69bf0757&hm=3dbb39feaea42177edbcab9fb0ca0d76b5b2f2fd0003d0c22779f7c38aa4b508&" 
    alt="Ben Chong" 
    className="w-full h-full object-cover"
  />
</div>
            <div className="flex-1 text-center md:text-left">
              <div className="flex flex-wrap items-center gap-3 mb-6 justify-center md:justify-start">
                <span className="bg-ss-gold/10 text-ss-gold text-[10px] font-black px-4 py-1.5 rounded-full border border-ss-gold/20 uppercase tracking-[0.2em]">Top Rated Mentor</span>
                <span className="bg-ss-green/10 text-ss-green text-[10px] font-black px-4 py-1.5 rounded-full border border-ss-green/20 uppercase tracking-[0.2em]">CSC Expert</span>
              </div>
              <h2 className="text-5xl font-black tracking-tighter mb-2 uppercase italic">{mentor.name}</h2>
              <p className="text-ss-gold font-bold flex items-center gap-2 justify-center md:justify-start text-lg italic">
                <GraduationCap size={20}/> {mentor.dept} Graduate
              </p>
            </div>
          </div>

          {/* Expert Areas Bento Blocks */}
          <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
            <div className="bg-ss-card p-10 rounded-[2.5rem] border border-white/10 group hover:border-ss-gold/30 transition-all">
              <h4 className="flex items-center gap-3 text-ss-gold font-bold uppercase tracking-[0.2em] text-[10px] mb-8">
                <Target size={18}/> Course Coaching
              </h4>
              <div className="space-y-4">
                {mentor.coaching.map((course, i) => (
                  <div key={i} className="flex items-center gap-4 bg-white/5 p-4 rounded-2xl border border-white/5 group-hover:bg-ss-green/5 transition-all">
                    <BookOpen size={18} className="text-ss-green" />
                    <span className="font-bold text-sm tracking-tight">{course}</span>
                  </div>
                ))}
              </div>
            </div>

            <div className="bg-ss-card p-10 rounded-[2.5rem] border border-white/10 group hover:border-ss-gold/30 transition-all">
              <h4 className="flex items-center gap-3 text-ss-gold font-bold uppercase tracking-[0.2em] text-[10px] mb-8">
                <Briefcase size={18}/> Career Strategy
              </h4>
              <div className="space-y-4">
                {mentor.skills.map((skill, i) => (
                  <div key={i} className="flex items-center gap-4 bg-white/5 p-4 rounded-2xl border border-white/5 group-hover:bg-ss-gold/5 transition-all">
                    <Award size={18} className="text-ss-gold" />
                    <span className="font-bold text-sm tracking-tight">{skill}</span>
                  </div>
                ))}
              </div>
            </div>
          </div>
        </div>

        {/* Scheduling Sidebar */}
        <div className="lg:col-span-1 bg-white/5 border border-white/10 rounded-[3rem] p-10 shadow-2xl flex flex-col h-full relative">
          <div className="absolute inset-0 bg-ss-green/5 blur-3xl rounded-full opacity-20 pointer-events-none"></div>
          <h4 className="flex items-center gap-3 text-ss-gold font-bold uppercase tracking-[0.2em] text-[10px] mb-8 relative">
            <Clock size={18}/> Available Slots
          </h4>
          <div className="grid grid-cols-1 gap-4 flex-1 relative">
            {mentor.slots.map((slot) => (
              <button 
                key={slot}
                onClick={() => setSelectedSlot(slot)}
                className={`py-5 rounded-2xl font-black text-xs uppercase tracking-widest transition-all border ${
                  selectedSlot === slot 
                  ? 'bg-ss-gold text-ss-black border-ss-gold shadow-2xl shadow-ss-gold/30 scale-[1.02]' 
                  : 'bg-ss-card text-gray-500 border-white/5 hover:border-ss-gold/40'
                }`}
              >
                {slot}
              </button>
            ))}
          </div>
          <button 
            disabled={!selectedSlot}
            onClick={handleBook}
            className="w-full mt-10 bg-ss-green text-white font-black py-5 rounded-2xl disabled:opacity-20 disabled:grayscale transition-all shadow-xl shadow-ss-green/30 uppercase tracking-widest text-xs relative overflow-hidden group"
          >
            <span className="relative z-10">{selectedSlot ? 'Request 1-1 Session' : 'Select a Time Slot'}</span>
            <div className="absolute inset-0 bg-white/10 translate-y-full group-hover:translate-y-0 transition-transform duration-300"></div>
          </button>
        </div>

      </main>
    </div>
  );
}