import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext.jsx";

export default function CmsNavbar() {
    const { user, isAdmin, logout } = useAuth();
    const navigate = useNavigate();

    function handleLogout() {
        logout();
        navigate("/login");
    }

    return (
        <nav className="navbar">
            <Link to="/cms/kategorije">Kategorije</Link>
            <Link to="/cms/vesti">Vesti</Link>

            {isAdmin && <Link to="/cms/korisnici">Korisnici</Link>}

            <Link to="/">Javna platforma</Link>

            <span className="spacer" />

            {user && (
                <span>
          {user.ime} {user.prezime} ({user.tip})
        </span>
            )}

            <button onClick={handleLogout}>Logout</button>
        </nav>
    );
}