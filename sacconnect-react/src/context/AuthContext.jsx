import { createContext, useContext, useState } from 'react';

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(() => {
    const id = localStorage.getItem('userId');
    const email = localStorage.getItem('userEmail');
    return id ? { id, email } : null;
  });

  const login = (userData) => {
    localStorage.setItem('userId', userData.id);
    localStorage.setItem('userEmail', userData.email);
    setUser(userData);
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