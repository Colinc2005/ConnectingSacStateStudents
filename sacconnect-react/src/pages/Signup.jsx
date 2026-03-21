import { useActionState } from 'react';
import { GraduationCap, ArrowLeft } from 'lucide-react';
import { Link, useNavigate } from 'react-router-dom';

export default function Signup() {
  const navigate = useNavigate();

  const [error, submitAction, isPending] = useActionState(async (prev, formData) => {
    const name = formData.get('username');
    const email = formData.get('email');
    const password = formData.get('password');
    const confirm = formData.get('confirmPassword'); // Added from old logic

    // Validations
    if (!email.endsWith('@csus.edu')) return "Must use @csus.edu email.";
    if (password !== confirm) return "Passwords do not match."; // Added

    try {
      const resp = await fetch('http://localhost:8080/api/users/register', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        // Expanded body to match your original backend schema
        body: JSON.stringify({ 
          name, 
          email, 
          password,
          age: null,
          major: null,
          bio: null,
          interests: [],
          tags: []
        })
      });

      if (resp.ok) {
        navigate(`/verify?email=${encodeURIComponent(email)}`); 
        return null;
      }
      
      const errText = await resp.text();
      return errText || "User already exists or server error.";
    } catch (e) {
      return "Backend connection failed. Is port 8080 open?";
    }
  }, null);

  return (
    <div className="min-h-screen flex items-center justify-center bg-ss-black p-4">
      <div className="w-full max-w-md bg-ss-card p-10 rounded-3xl border border-white/10 shadow-2xl relative">
        <Link to="/login" className="absolute top-10 left-10 text-gray-500 hover:text-ss-gold transition">
          <ArrowLeft size={20} />
        </Link>
        
        <div className="flex flex-col items-center mb-8">
          <div className="bg-ss-green p-4 rounded-2xl mb-4">
            <GraduationCap className="text-ss-gold" size={40} />
          </div>
          <h1 className="text-2xl font-black text-white uppercase tracking-tighter">Join <span className="text-ss-gold">Connect</span></h1>
        </div>

        <form action={submitAction} className="space-y-4">
          <input name="username" placeholder="Username" required className="w-full bg-white/5 border border-white/10 rounded-xl px-4 py-3 text-white focus:border-ss-gold outline-none transition-all" />
          
          <input name="email" type="email" placeholder="herky@csus.edu" required className="w-full bg-white/5 border border-white/10 rounded-xl px-4 py-3 text-white focus:border-ss-gold outline-none transition-all" />
          
          {/* Grid for passwords to keep the form compact */}
          <div className="grid grid-cols-2 gap-4">
            <input name="password" type="password" placeholder="Password" required className="w-full bg-white/5 border border-white/10 rounded-xl px-4 py-3 text-white focus:border-ss-gold outline-none transition-all" />
            <input name="confirmPassword" type="password" placeholder="Confirm" required className="w-full bg-white/5 border border-white/10 rounded-xl px-4 py-3 text-white focus:border-ss-gold outline-none transition-all" />
          </div>

          {error && <p className="text-red-500 text-xs text-center font-bold bg-red-500/10 py-2 rounded-lg">{error}</p>}
          
          <button disabled={isPending} className="w-full bg-ss-green hover:bg-ss-green/80 text-white font-bold py-4 rounded-xl shadow-lg shadow-ss-green/20 transition-all">
            {isPending ? 'Creating Account...' : 'Register'}
          </button>
        </form>
      </div>
    </div>
  );
}