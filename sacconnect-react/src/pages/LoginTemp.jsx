import { useActionState } from 'react';
import { useAuth } from '../context/AuthContext';
import { GraduationCap } from 'lucide-react';
import { Link } from 'react-router-dom';

export default function LoginTemp() {
  const { login } = useAuth();

  const [error, submitAction, isPending] = useActionState(async (prev, formData) => {
    const email = formData.get('email');
    const password = formData.get('password');

    if (!email.endsWith('@csus.edu')) return "Must use a @csus.edu email.";

    try {
      const resp = await fetch('http://localhost:8080/api/users/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email, password })
      });

      if (!resp.ok) return "Invalid credentials.";
      
      const data = await resp.json();
      login(data);
      window.location.href = '/';
    } catch (e) {
      return "Backend is offline (Check Port 8080).";
    }
  }, null);

  return (
    <div className="min-h-screen flex items-center justify-center bg-ss-black p-4">
      <div className="w-full max-w-md bg-ss-card p-10 rounded-3xl border border-white/10 shadow-2xl">
        <div className="flex flex-col items-center mb-8">
          <div className="bg-ss-green p-4 rounded-2xl mb-4">
            <GraduationCap className="text-ss-gold" size={40} />
          </div>
          <h1 className="text-2xl font-black text-white uppercase tracking-tighter">Student <span className="text-ss-gold">Connect</span></h1>
        </div>

        <form action={submitAction} className="space-y-4">
          <input name="email" type="email" placeholder="herky@csus.edu" required className="w-full bg-white/5 border border-white/10 rounded-xl px-4 py-3 text-white focus:border-ss-gold outline-none transition-all" />
          <input name="password" type="password" placeholder="Password" required className="w-full bg-white/5 border border-white/10 rounded-xl px-4 py-3 text-white focus:border-ss-gold outline-none transition-all" />
          {error && <p className="text-red-500 text-xs text-center font-bold">{error}</p>}
          <button disabled={isPending} className="w-full bg-ss-green hover:bg-ss-green/80 text-white font-bold py-4 rounded-xl transition-all shadow-lg shadow-ss-green/20">
            {isPending ? 'Signing in...' : 'Sign In'}
          </button>
        </form>

        <p className="mt-8 text-center text-sm text-gray-500">
          New here? <Link to="/signup" className="text-ss-gold hover:underline font-bold">Create an account</Link>
        </p>
      </div>
    </div>
  );
}