import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import api from "../api/axios.js";

export default function TopReactionsBox() {
    const [vesti, setVesti] = useState([]);
    const [error, setError] = useState("");
    const [loading, setLoading] = useState(true);

    async function loadTopReactions() {
        try {
            setLoading(true);
            setError("");

            const response = await api.get("/vesti/top-reakcije", {
                params: {
                    t: Date.now(),
                },
            });

            setVesti(response.data || []);
        } catch (error) {
            setError("Greška pri učitavanju najreaktivnijih vesti.");
        } finally {
            setLoading(false);
        }
    }

    useEffect(() => {
        loadTopReactions();

        function handleReactionsChanged() {
            loadTopReactions();
        }

        window.addEventListener("reactionsChanged", handleReactionsChanged);

        return () => {
            window.removeEventListener("reactionsChanged", handleReactionsChanged);
        };
    }, []);

    return (
        <aside className="card">
            <h3>Najviše reakcija</h3>

            {loading && <p>Učitavanje...</p>}

            {error && <div className="error">{error}</div>}

            {!loading && vesti.length === 0 && !error && <p>Nema reakcija.</p>}

            {!loading &&
                !error &&
                vesti.map((vest) => (
                    <div key={vest.id} style={{ marginBottom: 10 }}>
                        <Link to={`/vest/${vest.id}`}>
                            {vest.naslov}
                        </Link>

                        <div style={{ fontSize: 13, color: "#555" }}>
                            Reakcije: {(vest.brojLajkova || 0) + (vest.brojDislajkova || 0)}
                        </div>
                    </div>
                ))}
        </aside>
    );
}