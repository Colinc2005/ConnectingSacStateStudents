import { useActionState } from 'react';
import { useAuth } from '../context/AuthContext';
import { UserCircle, Save, ArrowLeft } from 'lucide-react';
import { useNavigate, Link } from 'react-router-dom';

export default function EditProfile() {
  const { user } = useAuth();
  const navigate = useNavigate();

  const [error, submitAction, isPending] = useActionState(async (prev, formData) => {
    const profileData = {
      id: user.id,
      email: user.email,
      major: formData.get('major'),
      bio: formData.get('bio'),
      age: parseInt(formData.get('age')) || null,
      interests: formData.get('interests').split(',').map(i => i.trim()),
    };

    try {
      const resp = await fetch(`http://localhost:8080/api/users/update`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(profileData)
      });

      if (resp.ok) {
        navigate('/'); // Send them to the dashboard once done
        return null;
      }
      return "Failed to update profile.";
    } catch (e) {
      return "Backend connection error.";
    }
  }, null);

  return (
    <div className="min-h-screen bg-ss-black flex items-center justify-center p-6">
      <div className="w-full max-w-2xl bg-ss-card border border-white/10 rounded-[2.5rem] p-10 shadow-2xl relative">
        <Link to="/" className="absolute top-10 left-10 text-gray-500 hover:text-ss-gold transition">
          <ArrowLeft size={24} />
        </Link>

        <div className="text-center mb-10">
          <div className="inline-block bg-ss-green/20 p-4 rounded-3xl mb-4 text-ss-gold">
            <UserCircle size={48} />
          </div>
          <h1 className="text-3xl font-black uppercase tracking-tighter">Complete Your <span className="text-ss-gold">Profile</span></h1>
          <p className="text-gray-500">Tell other Hornets a bit about yourself</p>
        </div>

        <form action={submitAction} className="grid grid-cols-1 md:grid-cols-2 gap-6">
          <div className="space-y-2">
            <label className="text-xs font-bold text-ss-gold uppercase ml-1">Academic Major</label>
            <input name="major" placeholder="Computer Engineering" required className="w-full bg-white/5 border border-white/10 rounded-2xl px-5 py-4 text-white focus:border-ss-gold outline-none transition-all" />
          </div>

          <div className="space-y-2">
            <label className="text-xs font-bold text-ss-gold uppercase ml-1">Age</label>
            <input name="age" type="number" placeholder="21" className="w-full bg-white/5 border border-white/10 rounded-2xl px-5 py-4 text-white focus:border-ss-gold outline-none transition-all" />
          </div>

          <div className="md:col-span-2 space-y-2">
            <label className="text-xs font-bold text-ss-gold uppercase ml-1">Short Bio</label>
            <textarea name="bio" rows="3" placeholder="I love building React apps and drinking Dutch Bros..." className="w-full bg-white/5 border border-white/10 rounded-2xl px-5 py-4 text-white focus:border-ss-gold outline-none transition-all resize-none"></textarea>
          </div>

          <div className="md:col-span-2 space-y-2">
            <label className="text-xs font-bold text-ss-gold uppercase ml-1">Interests (Comma separated)</label>
            <input name="interests" placeholder="Coding, Gym, Overwatch, Sac State" className="w-full bg-white/5 border border-white/10 rounded-2xl px-5 py-4 text-white focus:border-ss-gold outline-none transition-all" />
          </div>

          {error && <p className="md:col-span-2 text-red-500 text-sm font-bold text-center">{error}</p>}

          <button disabled={isPending} className="md:col-span-2 bg-ss-green hover:bg-ss-green/80 text-white font-black py-5 rounded-2xl flex items-center justify-center gap-3 transition-all shadow-xl shadow-ss-green/20">
            <Save size={20} />
            {isPending ? 'Updating...' : 'Save Hornet Profile'}
          </button>
        </form>
      </div>
    </div>
  );
}