import { BrowserRouter as Router, Routes, Route, Navigate } from "react-router-dom";
import Login from "./components/Login.tsx";
import AdminPanel from "./components/AdminPanel.tsx";
import UserDashboard from "./components/UserDashboard/UserDashboard.tsx";

function App() {
    return (
        <Router>
            <Routes>
                {/* Login Route */}
                <Route path="/login" element={<Login />} />

                {/* Separate Routes for each role */}
                <Route path="/admin" element={<AdminPanel />} />
                <Route path="/student-dashboard" element={<UserDashboard />} />
                <Route path="/instructor-dashboard" element={<UserDashboard />} />

                {/* Fallback route for unmatched paths */}
                <Route path="*" element={<Navigate to="/login" />} />
            </Routes>
        </Router>
    );
}

export default App;