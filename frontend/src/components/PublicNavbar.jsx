import { Link, useNavigate } from "react-router-dom";
import { useEffect, useState } from "react";
import api from "../api/axios.js";
import { useAuth } from "../context/AuthContext.jsx";

export default function PublicNavbar() {
    const [kategorije, setKategorije] = useState([]);
    const [search, setSearch] = useState("");
    const { isLoggedIn } = useAuth();
    const navigate = useNavigate();

    useEffect(() => {
        async function loadKategorije() {
            try {
                const response = await api.get("/kategorije?page=1");
                setKategorije(response.data.podaci || []);
            } catch (error) {
                console.error(error);
            }
        }

        loadKategorije();
    }, []);

    function handleSubmit(e) {
        e.preventDefault();

        const trimmed = search.trim();

        if (trimmed.length > 0) {
            navigate(`/pretraga?search=${encodeURIComponent(trimmed)}`);
            setSearch("");
        }
    }

    return (
        <nav className="navbar">
            <Link to="/">Početna</Link>
            <Link to="/najcitanije">Najčitanije</Link>

            {kategorije.map((kategorija) => (
                <Link key={kategorija.id} to={`/kategorija/${kategorija.id}`}>
                    {kategorija.ime}
                </Link>
            ))}

            <form onSubmit={handleSubmit}>
                <input
                    placeholder="Pretraga vesti..."
                    value={search}
                    onChange={(e) => setSearch(e.target.value)}
                    style={{ padding: "7px" }}
                />
                <button type="submit">Traži</button>
            </form>

            <span className="spacer" />

            {isLoggedIn ? (
                <Link to="/cms/kategorije">CMS</Link>
            ) : (
                <Link to="/login">Login</Link>
            )}
        </nav>
    );
}