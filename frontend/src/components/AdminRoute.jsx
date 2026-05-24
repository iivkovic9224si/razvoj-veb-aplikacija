import { Navigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext.jsx";

export default function AdminRoute({ children }) {
    const { isLoggedIn, isAdmin, loading } = useAuth();

    if (loading) {
        return <div className="container">Učitavanje...</div>;
    }

    if (!isLoggedIn) {
        return <Navigate to="/login" replace />;
    }

    if (!isAdmin) {
        return <Navigate to="/cms/kategorije" replace />;
    }

    return children;
}