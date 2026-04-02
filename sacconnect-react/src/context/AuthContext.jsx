import { createContext, useContext, useState } from 'react';

const AuthContext = createContext();

function normalizeUser(raw) {
  if (!raw) return null;
  const id = raw.id || raw.userId;
  const email = raw.email;
  const name = raw.name;

  if (id === null || id === undefined) return null;
  const idStr = String(id).trim();
  if (!idStr || idStr === 'undefined' || idStr === 'null' || idStr === '[object Object]') return null;
  if (!email) return null;

  return { id: idStr, email: String(email), name: name ? String(name) : null };
}

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(() => {
    const id = localStorage.getItem('userId');
    const email = localStorage.getItem('userEmail');
    const name = localStorage.getItem('userName');
    const normalized = normalizeUser({ id, email, name });
    if (!normalized) {
      localStorage.clear();
      return null;
    }
    return normalized;
  });

  const login = (userData) => {
    const normalized = normalizeUser(userData);
    if (!normalized) {
      localStorage.clear();
      setUser(null);
      return false;
    }
    localStorage.setItem('userId', normalized.id);
    localStorage.setItem('userEmail', normalized.email);
    if (normalized.name) localStorage.setItem('userName', normalized.name);
    setUser(normalized);
    return true;
  };

  const logout = () => {
    localStorage.clear();
    setUser(null);
  };

  return (
    <AuthContext.Provider value={{ user, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) throw new Error("useAuth must be used within an AuthProvider");
  return context;
};