import { useState } from 'react';
import { ArrowLeft, CheckCircle, Sparkles, Code } from 'lucide-react';
import { Link } from 'react-router-dom';

export default function Majors() {
  const [selectedMajor, setSelectedMajor] = useState(null);
  const [selectedClasses, setSelectedClasses] = useState([]);

  const csClasses = [
    { id: "CSC10", code: "CSC 10", name: "Introduction to Programming Logic", units: 3 },
    { id: "CSC15", code: "CSC 15", name: "Programming Concepts and Methodology I", units: 3 },
    { id: "CSC15P", code: "CSC 15P", name: "Peer-Assisted Learning", units: 1 },
    { id: "CSC20", code: "CSC 20", name: "Programming Concepts and Methodology II", units: 3 },
    { id: "CSC20P", code: "CSC 20P", name: "Peer-Assisted Learning", units: 1 },
    { id: "CSC28", code: "CSC 28", name: "Discrete Structures for Computer Science", units: 3 },
    { id: "CSC35", code: "CSC 35", name: "Introduction to Computer Architecture", units: 3 },
    { id: "CSC60", code: "CSC 60", name: "Introduction to Systems Programming in UNIX", units: 3 },
    { id: "CSC130", code: "CSC 130", name: "Data Structures and Algorithm Analysis", units: 3 },
    { id: "CSC131", code: "CSC 131", name: "Computer Software Engineering", units: 3 },
    { id: "CSC133", code: "CSC 133", name: "Object-Oriented Graphics Programming", units: 3 },
    { id: "CSC134", code: "CSC 134", name: "Database Management Systems", units: 3 },
    { id: "CSC135", code: "CSC 135", name: "Computability and Formal Languages", units: 3 },
    { id: "CSC137", code: "CSC 137", name: "Computer Organization", units: 3 },
    { id: "CSC138", code: "CSC 138", name: "Computer Networking Fundamentals", units: 3 },
    { id: "CSC139", code: "CSC 139", name: "Operating System Principles", units: 3 },
    { id: "CSC140", code: "CSC 140", name: "Advanced Algorithm Design and Analysis", units: 3 },
    { id: "CSC142", code: "CSC 142", name: "Advanced Computer Organization", units: 3 },
    { id: "CSC152", code: "CSC 152", name: "Cryptography", units: 3 },
    { id: "CSC153", code: "CSC 153", name: "Computer Forensics Principles", units: 3 },
    { id: "CSC154", code: "CSC 154", name: "System Attacks and Countermeasures", units: 3 },
    { id: "CSC155", code: "CSC 155", name: "3D Graphics and Shader Programming", units: 3 },
    { id: "CSC159", code: "CSC 159", name: "Operating System Pragmatics", units: 3 },
    { id: "CSC163", code: "CSC 163", name: "Parallel Programming with GPUs", units: 3 },
    { id: "CSC165", code: "CSC 165", name: "Comp Game Arc+Implemntatn", units: 3 },
    { id: "CSC174", code: "CSC 174", name: "Adv Database Mgmt Systems", units: 3 },
    { id: "CSC177", code: "CSC 177", name: "Data Analytics and Mining", units: 3 },
    { id: "CSC180", code: "CSC 180", name: "Intelligent Systems", units: 3 },
    { id: "CSC190", code: "CSC 190", name: "Senior Project: Part I", units: 2 },
    { id: "CSC191", code: "CSC 191", name: "Senior Project: Part II", units: 2 },
    { id: "CSC192", code: "CSC 192", name: "Career Planning", units: 1 },
    { id: "CSC193A", code: "CSC 193A", name: "Web Programming", units: 1 },
    { id: "CSC195", code: "CSC 195", name: "Fieldwork in Computer Science", units: "1-4" },
    { id: "CSC195A", code: "CSC 195A", name: "Professional Practice", units: "1-12" },
    { id: "CSC196A", code: "CSC 196A", name: "Computational Biology", units: 3 },
    { id: "CSC199", code: "CSC 199", name: "Special Problems", units: "1-3" }
  ];

  const toggleClass = (courseId) => {
    if (selectedClasses.includes(courseId)) {
      setSelectedClasses(selectedClasses.filter(c => c !== courseId));
    } else {
      setSelectedClasses([...selectedClasses, courseId]);
    }
  };

  return (
    <div className="min-h-screen bg-[#004e38] text-white p-6 md:p-12 font-sans selection:bg-ss-gold selection:text-ss-black">
      <Link to="/" className="fixed top-10 left-10 text-white/50 hover:text-ss-gold transition z-50">
        <ArrowLeft size={28} />
      </Link>

      <header className="max-w-7xl mx-auto mb-16 pt-12">
        <h1 className="text-6xl font-black uppercase tracking-tighter italic leading-none">
          Academic <span className="text-ss-gold underline decoration-white underline-offset-8">Catalog</span>
        </h1>
        <p className="text-white/70 font-medium mt-4 text-lg italic tracking-tight">Map out your degree requirements in the hive.</p>
      </header>

      <main className="max-w-7xl mx-auto">
        {!selectedMajor ? (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
            <button 
              onClick={() => setSelectedMajor('Computer Science')}
              className="group relative overflow-hidden bg-ss-card border border-white/10 p-12 rounded-[3.5rem] text-left hover:border-ss-gold transition-all duration-300 shadow-2xl"
            >
              <div className="bg-ss-gold/10 w-16 h-16 rounded-2xl flex items-center justify-center mb-8 border border-ss-gold/20 group-hover:scale-110 transition-transform">
                <Code size={32} className="text-ss-gold" />
              </div>
              <h3 className="text-4xl font-black uppercase italic tracking-tighter mb-3">Computer Science</h3>
              <p className="text-white/60 font-medium leading-relaxed">Full Sacramento State curriculum for CS and CompE tracks.</p>
              <div className="mt-8 flex items-center gap-2 text-ss-gold font-bold text-xs uppercase tracking-widest opacity-60 group-hover:opacity-100 transition-opacity">
                View Catalog <ArrowLeft className="rotate-180" size={14} />
              </div>
            </button>
            <div className="bg-ss-card border border-white/5 p-12 rounded-[3.5rem] opacity-20 grayscale cursor-not-allowed">
              <div className="w-16 h-16 rounded-2xl bg-white/5 mb-8 border border-white/5"></div>
              <h3 className="text-4xl font-black uppercase italic tracking-tighter mb-3 text-white/40">Mechanical Eng.</h3>
              <p className="text-white/30 font-medium italic">Pending faculty review.</p>
            </div>
          </div>
        ) : (
          <div className="space-y-12 animate-in fade-in slide-in-from-bottom-4 duration-700">
            <div className="flex flex-col md:flex-row justify-between items-start md:items-center gap-6 bg-ss-card p-10 rounded-[3rem] border border-white/10 shadow-2xl">
              <div>
                <button 
                  onClick={() => { setSelectedMajor(null); setSelectedClasses([]); }}
                  className="text-ss-gold/60 hover:text-ss-gold text-[10px] font-black uppercase tracking-[0.3em] mb-4 flex items-center gap-2 transition-colors"
                >
                  <ArrowLeft size={14} /> Back to Majors
                </button>
                <h2 className="text-5xl font-black uppercase italic tracking-tighter">{selectedMajor}</h2>
                <div className="flex items-center gap-3 mt-2">
                  <p className="text-ss-gold font-black uppercase tracking-widest text-[10px]">
                    {selectedClasses.length} {selectedClasses.length === 1 ? 'class' : 'classes'} selected
                  </p>
                  {selectedClasses.length < 2 && (
                    <>
                      <span className="text-white/20">|</span>
                      <p className="text-white font-black uppercase tracking-widest text-[10px]">
                        Select {2 - selectedClasses.length} more classes
                      </p>
                    </>
                  )}
                </div>
              </div>

              {selectedClasses.length >= 2 && (
                <button className="bg-ss-green text-white px-12 py-5 rounded-2xl font-black uppercase tracking-widest text-xs flex items-center gap-3 animate-pulse shadow-2xl hover:bg-ss-green/80 transition-all">
                  <Sparkles size={18} className="text-ss-gold" /> Generate Schedule
                </button>
              )}
            </div>

            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
              {csClasses.map((course) => (
                <button
                  key={course.id}
                  onClick={() => toggleClass(course.id)}
                  className={`group p-8 rounded-[2.5rem] text-left transition-all duration-300 border h-52 relative flex flex-col justify-between overflow-hidden shadow-xl ${
                    selectedClasses.includes(course.id)
                    ? 'bg-ss-gold text-ss-black border-ss-gold scale-[1.05]'
                    : 'bg-ss-card text-white border-white/10 hover:border-ss-gold/40'
                  }`}
                >
                  <div className="flex justify-between items-start relative z-10">
                    <span className={`text-[10px] font-black uppercase tracking-[0.3em] ${selectedClasses.includes(course.id) ? 'text-ss-black/50' : 'text-ss-gold/80'}`}>
                      {course.code}
                    </span>
                    <div className={`w-6 h-6 rounded-full border-2 flex items-center justify-center transition-all ${
                      selectedClasses.includes(course.id) ? 'border-ss-black/20 bg-ss-black/10' : 'border-white/10'
                    }`}>
                      {selectedClasses.includes(course.id) && <CheckCircle size={14} />}
                    </div>
                  </div>
                  <div className="relative z-10">
                    <h4 className="text-xl font-black leading-tight mb-3 uppercase italic tracking-tighter">{course.name}</h4>
                    <div className={`text-[9px] font-black uppercase tracking-[0.2em] inline-block px-3 py-1 rounded-full border ${
                      selectedClasses.includes(course.id) ? 'border-ss-black/10 bg-ss-black/5 text-ss-black/60' : 'border-white/10 bg-white/5 text-white/40'
                    }`}>Units: {course.units}</div>
                  </div>
                </button>
              ))}
            </div>
          </div>
        )}
      </main>
    </div>
  );
}