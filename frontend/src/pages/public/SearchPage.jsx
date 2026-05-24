import { useEffect, useState } from "react";
import { useNavigate, useSearchParams } from "react-router-dom";
import api from "../../api/axios.js";
import Pagination from "../../components/Pagination.jsx";
import NewsList from "../../components/NewsList.jsx";

export default function SearchPage() {
    const [vesti, setVesti] = useState([]);
    const [totalPages, setTotalPages] = useState(1);
    const [error, setError] = useState("");
    const [loading, setLoading] = useState(true);

    const [searchParams] = useSearchParams();
    const navigate = useNavigate();

    const search = searchParams.get("search") || "";
    const page = Number(searchParams.get("page") || 1);

    useEffect(() => {
        let active = true;

        async function loadVesti() {
            if (!search.trim()) {
                setVesti([]);
                setTotalPages(1);
                setLoading(false);
                return;
            }

            try {
                setLoading(true);
                setError("");

                const response = await api.get(
                    `/vesti?search=${encodeURIComponent(search)}&page=${page}`
                );

                if (!active) return;

                setVesti(response.data.podaci || []);
                setTotalPages(response.data.ukupnoStranica || 1);
            } catch (error) {
                if (!active) return;

                const message =
                    error.response?.data?.error ||
                    error.response?.data ||
                    error.message ||
                    "Greška pri pretrazi vesti.";

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
    }, [search, page]);

    return (
        <div className="container">
            <h1>Pretraga vesti</h1>

            <p>
                Rezultati za: <b>{search}</b>
            </p>

            {loading && <p>Učitavanje...</p>}
            {error && <div className="error">{error}</div>}

            {!loading && !error && <NewsList vesti={vesti} />}

            {!loading && !error && (
                <Pagination
                    page={page}
                    totalPages={totalPages}
                    onPageChange={(newPage) =>
                        navigate(`/pretraga?search=${encodeURIComponent(search)}&page=${newPage}`)
                    }
                />
            )}
        </div>
    );
}