import { BrowserRouter as Router, Routes, Route, Navigate } from "react-router-dom";
import Login from "./components/Login.tsx";
import AdminDashboard from "./components/dashboards/AdminDashboard.tsx";
import UserDashboard from "./components/dashboards/UserDashboard.tsx";
import ForgotPassword from "./components/ForgotPassword.tsx";
import VerifyCode from "./components/VerifyCode.tsx";
import ResetPassword from "./components/ResetPassword.tsx";

function App() {
    return (
        <Router>
            <Routes>
                {/* Login Route */}
                <Route path="/login" element={<Login />} />
                {/* Forgot Password Route */}
                <Route path="/forgot-password" element={<ForgotPassword />} />
                <Route path="/verify-code" element={<VerifyCode />} />
                <Route path="/reset-password" element={<ResetPassword />} />

                {/* Separate Routes for each role */}
                <Route path="/admin" element={<AdminDashboard />} />
                <Route path="/student-dashboard" element={<UserDashboard />} />
                <Route path="/instructor-dashboard" element={<UserDashboard />} />

                {/* Fallback route for unmatched paths */}
                <Route path="*" element={<Navigate to="/login" />} />
            </Routes>
        </Router>
    );
}

export default App;