import { useActionState } from 'react';
import { useSearchParams, useNavigate, Link } from 'react-router-dom';
import { ShieldCheck, ArrowLeft } from 'lucide-react';

export default function Verify() {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const email = searchParams.get('email'); // Pulls email from URL like your old version

  const [error, submitAction, isPending] = useActionState(async (prev, formData) => {
    const code = formData.get('code');

    if (!code || code.length !== 6) return "Please enter the 6-digit code.";

    try {
      const resp = await fetch('http://localhost:8080/api/users/verify', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email, code }) // Matches your existing Backend DTO
      });

      if (resp.ok) {
        navigate('/login?verified=true'); // Redirect back to login on success
        return null;
      } else {
        const errText = await resp.text();
        return errText || "Invalid or expired verification code.";
      }
    } catch (e) {
      return "Connection failed. Ensure your Spring Boot server is running.";
    }
  }, null);

  return (
    <div className="min-h-screen flex items-center justify-center bg-ss-black p-4">
      <div className="w-full max-w-md bg-ss-card p-10 rounded-3xl border border-white/10 shadow-2xl relative text-center">
        
        <Link to="/signup" className="absolute top-10 left-10 text-gray-500 hover:text-ss-gold transition">
          <ArrowLeft size={20} />
        </Link>

        <div className="flex flex-col items-center mb-8">
          <div className="bg-ss-green p-4 rounded-2xl mb-4">
            <ShieldCheck className="text-ss-gold" size={40} />
          </div>
          <h1 className="text-2xl font-black text-white uppercase tracking-tighter">Verify <span className="text-ss-gold">Email</span></h1>
          <p className="text-gray-500 text-sm mt-2">
            We sent a code to <span className="text-ss-gold font-bold">{email || 'your email'}</span>
          </p>
        </div>

        <form action={submitAction} className="space-y-6">
          <div className="space-y-2">
            <label className="text-xs font-bold text-ss-gold uppercase tracking-widest">6-Digit Code</label>
            <input 
              name="code" 
              type="text" 
              maxLength="6"
              required 
              className="w-full bg-white/5 border border-white/10 rounded-xl px-4 py-4 text-center text-2xl font-black tracking-[1em] text-ss-gold focus:border-ss-gold outline-none transition-all" 
              placeholder="000000"
            />
          </div>

          {error && <p className="text-red-500 text-xs font-bold bg-red-500/10 py-2 rounded-lg">{error}</p>}

          <button 
            disabled={isPending} 
            className="w-full bg-ss-green hover:bg-ss-green/80 text-white font-bold py-4 rounded-xl transition-all shadow-lg shadow-ss-green/20 disabled:opacity-50"
          >
            {isPending ? 'Verifying...' : 'Verify Account'}
          </button>
        </form>

        <div className="mt-8 text-xs text-gray-600">
          Didn't get a code? <button className="text-ss-gold hover:underline font-bold">Resend Code</button>
        </div>
      </div>
    </div>
  );
}