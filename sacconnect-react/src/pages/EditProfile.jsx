import { useActionState, useEffect, useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { UserCircle, Save, ArrowLeft } from 'lucide-react';
import { useNavigate, Link } from 'react-router-dom';

export default function EditProfile() {
  const { user, login } = useAuth();
  const navigate = useNavigate();
  const [profile, setProfile] = useState(null);
  const [loadingProfile, setLoadingProfile] = useState(true);

  const [error, submitAction, isPending] = useActionState(async (prev, formData) => {
    if (!user?.id) return "Missing session.";
    const profileData = {
      id: user.id,
      email: user.email,
      name: formData.get('name'),
      major: formData.get('major'),
      bio: formData.get('bio'),
      age: parseInt(formData.get('age')) || null,
      interests: (formData.get('interests') || '').split(',').map(i => i.trim()).filter(Boolean),
    };
    try {
      const resp = await fetch(`http://localhost:8080/api/users/update-profile`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(profileData)
      });
      if (resp.ok) {
        login(profileData);
        navigate('/profile');
        return null;
      }
      return "Failed to update profile.";
    } catch (e) {
      return "Backend connection error.";
    }
  }, null);

  useEffect(() => {
    async function loadProfile() {
      if (!user?.id) return;
      try {
        const resp = await fetch(`http://localhost:8080/api/users/${user.id}`);
        if (resp.ok) {
          const data = await resp.json();
          setProfile(data);
        }
      } catch (e) { console.error(e); } finally { setLoadingProfile(false); }
    }
    loadProfile();
  }, [user?.id]);

  const inputStyles = "w-full bg-white/5 border border-white/10 rounded-2xl px-5 py-4 text-white focus:border-ss-gold outline-none transition-all font-bold placeholder:text-transparent not-placeholder-shown:bg-white not-placeholder-shown:text-ss-black";

  return (
    <div className="min-h-screen bg-[#004e38] flex items-center justify-center p-6 font-sans">
      <div className="w-full max-w-2xl bg-ss-card border border-white/10 rounded-[2.5rem] p-10 shadow-2xl relative">
        <Link to="/profile" className="absolute top-10 left-10 text-gray-500 hover:text-ss-gold transition"><ArrowLeft size={24} /></Link>
        <div className="text-center mb-10">
          <div className="inline-block bg-ss-green/20 p-4 rounded-3xl mb-4 text-ss-gold"><UserCircle size={48} /></div>
          <h1 className="text-3xl font-black uppercase tracking-tighter italic text-white">Edit <span className="text-ss-gold">Identity</span></h1>
        </div>
        <form action={submitAction} className="grid grid-cols-1 md:grid-cols-2 gap-6" autoComplete="off">
          <div className="md:col-span-2 space-y-2">
            <label className="text-xs font-bold text-ss-gold uppercase ml-1">Username</label>
            <input name="name" defaultValue={profile?.name || user?.name || ''} placeholder=" " required autoComplete="off" className={inputStyles} />
          </div>
          <div className="space-y-2">
            <label className="text-xs font-bold text-ss-gold uppercase ml-1">Academic Major</label>
            <input name="major" defaultValue={profile?.major || ''} placeholder=" " autoComplete="off" required className={inputStyles} />
          </div>
          <div className="space-y-2">
            <label className="text-xs font-bold text-ss-gold uppercase ml-1">Age</label>
            <input name="age" type="text" inputMode="numeric" pattern="[0-9]*" defaultValue={profile?.age ?? ''} placeholder=" " autoComplete="off" className={inputStyles} />
          </div>
          <div className="md:col-span-2 space-y-2">
            <label className="text-xs font-bold text-ss-gold uppercase ml-1">Short Bio</label>
            <textarea name="bio" rows="3" defaultValue={profile?.bio || ''} placeholder=" " className={inputStyles + " resize-none"}></textarea>
          </div>
          <div className="md:col-span-2 space-y-2">
            <label className="text-xs font-bold text-ss-gold uppercase ml-1">Interests</label>
            <input name="interests" defaultValue={profile?.interests?.join(', ') || ''} placeholder=" " autoComplete="off" className={inputStyles} />
          </div>
          {error && <p className="md:col-span-2 text-red-500 text-sm font-bold text-center">{error}</p>}
          <button disabled={isPending} className="md:col-span-2 bg-ss-green hover:bg-ss-green/80 text-white font-black py-5 rounded-2xl flex items-center justify-center gap-3 transition-all shadow-xl">
            <Save size={20} /> {isPending ? 'Updating...' : 'Save Hornet Profile'}
          </button>
        </form>
      </div>
    </div>
  );
}