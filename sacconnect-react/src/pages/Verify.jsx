import { useActionState } from 'react';
import { useSearchParams, useNavigate, Link } from 'react-router-dom';
import { ShieldCheck, ArrowLeft } from 'lucide-react';

export default function Verify() {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const email = searchParams.get('email');
  const [error, submitAction, isPending] = useActionState(async (prev, formData) => {
    const code = formData.get('code');
    if (!code || code.length !== 6) return "Please enter 6 digits.";
    try {
      const resp = await fetch('http://localhost:8080/api/users/verify', { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ email, code }) });
      if (resp.ok) { navigate('/login?verified=true'); return null; }
      return "Invalid or expired code.";
    } catch (e) { return "Connection failed."; }
  }, null);

  return (
    <div className="min-h-screen flex items-center justify-center bg-[#004e38] p-4 font-sans">
      <div className="w-full max-w-md bg-ss-card p-10 rounded-3xl border border-white/10 shadow-2xl relative text-center">
        <Link to="/signup" className="absolute top-10 left-10 text-gray-500 hover:text-ss-gold transition"><ArrowLeft size={20} /></Link>
        <div className="flex flex-col items-center mb-8">
          <div className="bg-ss-green p-4 rounded-2xl mb-4 text-ss-gold"><ShieldCheck size={40} /></div>
          <h1 className="text-2xl font-black text-white uppercase tracking-tighter italic">Verify <span className="text-ss-gold">Email</span></h1>
          <p className="text-white/70 italic text-sm mt-2">Code sent to <span className="text-ss-gold font-bold">{email}</span></p>
        </div>
        <form action={submitAction} className="space-y-6">
          <input name="code" type="text" maxLength="6" required className="w-full bg-white/5 border border-white/10 rounded-xl px-4 py-4 text-center text-2xl font-black tracking-[1em] text-ss-gold focus:border-ss-gold outline-none transition-all" placeholder="000000" />
          {error && <p className="text-red-500 text-xs font-bold">{error}</p>}
          <button disabled={isPending} className="w-full bg-ss-green hover:bg-ss-green/80 text-white font-bold py-4 rounded-xl shadow-lg">{isPending ? 'Verifying...' : 'Verify Account'}</button>
        </form>
      </div>
    </div>
  );
}