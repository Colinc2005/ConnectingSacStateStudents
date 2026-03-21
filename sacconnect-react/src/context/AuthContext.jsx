import { createContext, useContext, useState } from 'react';

const AuthContext = createContext();

function normalizeUser(raw) {
  if (!raw) return null;
  const id = raw.id;
  const email = raw.email;

  if (id === null || id === undefined) return null;
  const idStr = String(id).trim();
  if (!idStr || idStr === 'undefined' || idStr === 'null' || Number.isNaN(Number(idStr))) {
    return null;
  }
  if (!email) return null;

  return { ...raw, id: idStr, email: String(email) };
}

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(() => {
    // Check local storage so the user stays logged in on refresh
    const id = localStorage.getItem('userId');
    const email = localStorage.getItem('userEmail');
    const normalized = normalizeUser({ id, email });
    if (!normalized) {
      localStorage.removeItem('userId');
      localStorage.removeItem('userEmail');
      return null;
    }
    return normalized;
  });

  const login = (userData) => {
    const normalized = normalizeUser(userData);
    if (!normalized) {
      localStorage.removeItem('userId');
      localStorage.removeItem('userEmail');
      setUser(null);
      return false;
    }
    localStorage.setItem('userId', normalized.id);
    localStorage.setItem('userEmail', normalized.email);
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

export const useAuth = () => useContext(AuthContext);
