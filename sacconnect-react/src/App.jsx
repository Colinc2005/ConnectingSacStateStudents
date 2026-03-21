// src/App.jsx
import Verify from './pages/Verify'; // 1. Add the import

function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          <Route path="/login" element={<LoginTemp />} />
          <Route path="/signup" element={<Signup />} />
          <Route path="/verify" element={<Verify />} /> {/* 2. Add this route */}
          <Route path="/" element={
            <ProtectedRoute>
              <Dashboard />
            </ProtectedRoute>
          } />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
}