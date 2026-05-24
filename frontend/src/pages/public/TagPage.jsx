import { useEffect, useState } from "react";
import { useNavigate, useParams, useSearchParams } from "react-router-dom";
import api from "../../api/axios.js";
import Pagination from "../../components/Pagination.jsx";
import NewsList from "../../components/NewsList.jsx";

export default function TagPage() {
    const [vesti, setVesti] = useState([]);
    const [totalPages, setTotalPages] = useState(1);
    const [error, setError] = useState("");
    const [loading, setLoading] = useState(true);

    const { tag } = useParams();
    const [searchParams] = useSearchParams();
    const navigate = useNavigate();

    const page = Number(searchParams.get("page") || 1);
    const decodedTag = decodeURIComponent(tag);

    useEffect(() => {
        let active = true;

        async function loadVesti() {
            try {
                setLoading(true);
                setError("");

                const response = await api.get(
                    `/vesti?tag=${encodeURIComponent(decodedTag)}&page=${page}`
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
                    "Greška pri učitavanju vesti po tagu.";

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
    }, [decodedTag, page]);

    return (
        <div className="container">
            <h1>Vesti za tag: #{decodedTag}</h1>

            {loading && <p>Učitavanje...</p>}
            {error && <div className="error">{error}</div>}

            {!loading && !error && <NewsList vesti={vesti} />}

            {!loading && !error && (
                <Pagination
                    page={page}
                    totalPages={totalPages}
                    onPageChange={(newPage) =>
                        navigate(`/tag/${encodeURIComponent(decodedTag)}?page=${newPage}`)
                    }
                />
            )}
        </div>
    );
}