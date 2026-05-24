import { useEffect, useState } from "react";
import { Link, useNavigate, useSearchParams } from "react-router-dom";
import api from "../../api/axios.js";
import Pagination from "../../components/Pagination.jsx";

export default function CmsCategoriesPage() {
    const [kategorije, setKategorije] = useState([]);
    const [totalPages, setTotalPages] = useState(1);
    const [error, setError] = useState("");

    const [searchParams] = useSearchParams();
    const navigate = useNavigate();

    const page = Number(searchParams.get("page") || 1);

    async function loadKategorije() {
        try {
            setError("");
            const response = await api.get(`/kategorije?page=${page}`);
            setKategorije(response.data.podaci || []);
            setTotalPages(response.data.ukupnoStranica || 1);
        } catch (error) {
            const message = error.response?.data?.error || error.response?.data || "Greška pri učitavanju kategorija.";
            setError(String(message));
        }
    }

    useEffect(() => {
        loadKategorije();
    }, [page]);

    async function handleDelete(id) {
        const confirmed = window.confirm("Da li sigurno želiš da obrišeš kategoriju?");

        if (!confirmed) {
            return;
        }

        try {
            await api.delete(`/kategorije/${id}`);
            await loadKategorije();
        } catch (error) {
            const message = error.response?.data?.error || error.response?.data || "Greška pri brisanju kategorije.";
            setError(String(message));
        }
    }

    return (
        <div className="container">
            <h1>CMS - Kategorije</h1>

            {error && <div className="error">{error}</div>}

            <Link className="btn" to="/cms/kategorije/nova">
                Dodaj kategoriju
            </Link>

            <table className="table" style={{ marginTop: 16 }}>
                <thead>
                <tr>
                    <th>Ime</th>
                    <th>Opis</th>
                    <th>Akcije</th>
                </tr>
                </thead>

                <tbody>
                {kategorije.map((kategorija) => (
                    <tr key={kategorija.id}>
                        <td>
                            <Link to={`/cms/vesti?kategorija=${kategorija.id}`}>
                                {kategorija.ime}
                            </Link>
                        </td>
                        <td>{kategorija.opis}</td>
                        <td>
                            <Link className="btn secondary" to={`/cms/kategorije/${kategorija.id}/izmena`}>
                                Izmeni
                            </Link>

                            <button className="danger" onClick={() => handleDelete(kategorija.id)}>
                                Obriši
                            </button>
                        </td>
                    </tr>
                ))}

                {kategorije.length === 0 && (
                    <tr>
                        <td colSpan="3">Nema kategorija.</td>
                    </tr>
                )}
                </tbody>
            </table>

            <Pagination
                page={page}
                totalPages={totalPages}
                onPageChange={(newPage) => navigate(`/cms/kategorije?page=${newPage}`)}
            />
        </div>
    );
}