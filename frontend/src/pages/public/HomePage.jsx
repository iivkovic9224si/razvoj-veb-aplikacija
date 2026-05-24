import { useEffect, useState } from "react";
import { Link, useNavigate, useSearchParams } from "react-router-dom";
import api from "../../api/axios.js";
import Pagination from "../../components/Pagination.jsx";

export default function HomePage() {
    const [vesti, setVesti] = useState([]);
    const [totalPages, setTotalPages] = useState(1);
    const [error, setError] = useState("");
    const [loading, setLoading] = useState(true);

    const [searchParams] = useSearchParams();
    const navigate = useNavigate();

    const page = Number(searchParams.get("page") || 1);

    useEffect(() => {
        let active = true;

        async function loadVesti() {
            try {
                setLoading(true);
                setError("");

                const response = await api.get(`/vesti?page=${page}`);

                if (!active) {
                    return;
                }

                setVesti(response.data.podaci || []);
                setTotalPages(response.data.ukupnoStranica || 1);
            } catch (error) {
                if (!active) {
                    return;
                }

                const message =
                    error.response?.data?.error ||
                    error.response?.data ||
                    error.message ||
                    "Greška pri učitavanju vesti.";

                setError(String(message));
            } finally {
                if (active) {
                    setLoading(false);
                }
            }
        }

        loadVesti();

        return () => {
            active = false;
        };
    }, [page]);

    return (
        <div className="container">
            <h1>RAF Novosti</h1>

            {loading && <p>Učitavanje vesti...</p>}

            {error && <div className="error">{error}</div>}

            {!loading &&
                !error &&
                vesti.map((vest) => (
                    <div className="card" key={vest.id}>
                        <h2>
                            <Link to={`/vest/${vest.id}`}>{vest.naslov}</Link>
                        </h2>

                        <p>
                            <b>Kategorija:</b> {vest.kategorijaIme} |{" "}
                            <b>Datum:</b> {vest.vremeKreiranja}
                        </p>

                        <p>{vest.tekst?.slice(0, 220)}...</p>
                    </div>
                ))}

            {!loading && !error && vesti.length === 0 && <p>Nema vesti.</p>}

            {!loading && !error && (
                <Pagination
                    page={page}
                    totalPages={totalPages}
                    onPageChange={(newPage) => navigate(`/?page=${newPage}`)}
                />
            )}
        </div>
    );
}