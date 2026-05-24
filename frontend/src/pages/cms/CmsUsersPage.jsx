import { useEffect, useState } from "react";
import { Link, useNavigate, useSearchParams } from "react-router-dom";
import api from "../../api/axios.js";
import Pagination from "../../components/Pagination.jsx";

export default function CmsUsersPage() {
    const [korisnici, setKorisnici] = useState([]);
    const [totalPages, setTotalPages] = useState(1);
    const [error, setError] = useState("");
    const [loading, setLoading] = useState(true);

    const [searchParams] = useSearchParams();
    const navigate = useNavigate();

    const page = Number(searchParams.get("page") || 1);

    async function loadKorisnici() {
        try {
            setLoading(true);
            setError("");

            const response = await api.get(`/korisnici?page=${page}`);

            setKorisnici(response.data.podaci || []);
            setTotalPages(response.data.ukupnoStranica || 1);
        } catch (error) {
            const message =
                error.response?.data?.error ||
                error.response?.data ||
                error.message ||
                "Greška pri učitavanju korisnika.";

            setError(String(message));
        } finally {
            setLoading(false);
        }
    }

    useEffect(() => {
        loadKorisnici();
    }, [page]);

    async function handleToggleStatus(id) {
        try {
            await api.post(`/korisnici/${id}/status`);
            await loadKorisnici();
        } catch (error) {
            const message =
                error.response?.data?.error ||
                error.response?.data ||
                error.message ||
                "Greška pri promeni statusa korisnika.";

            setError(String(message));
        }
    }

    return (
        <div className="container">
            <h1>CMS - Korisnici</h1>

            {error && <div className="error">{error}</div>}

            <Link className="btn" to="/cms/korisnici/novi">
                Dodaj korisnika
            </Link>

            {loading && <p>Učitavanje korisnika...</p>}

            {!loading && (
                <>
                    <table className="table" style={{ marginTop: 16 }}>
                        <thead>
                        <tr>
                            <th>Ime i prezime</th>
                            <th>Email</th>
                            <th>Tip</th>
                            <th>Status</th>
                            <th>Akcije</th>
                        </tr>
                        </thead>

                        <tbody>
                        {korisnici.map((korisnik) => (
                            <tr key={korisnik.id}>
                                <td>
                                    {korisnik.ime} {korisnik.prezime}
                                </td>
                                <td>{korisnik.email}</td>
                                <td>{korisnik.tip}</td>
                                <td>{korisnik.status}</td>
                                <td>
                                    <Link className="btn secondary" to={`/cms/korisnici/${korisnik.id}/izmena`}>
                                        Izmeni
                                    </Link>

                                    {korisnik.tip === "STVARALAC_SADRZAJA" && (
                                        <button onClick={() => handleToggleStatus(korisnik.id)}>
                                            {korisnik.status === "AKTIVAN" ? "Deaktiviraj" : "Aktiviraj"}
                                        </button>
                                    )}
                                </td>
                            </tr>
                        ))}

                        {korisnici.length === 0 && (
                            <tr>
                                <td colSpan="5">Nema korisnika.</td>
                            </tr>
                        )}
                        </tbody>
                    </table>

                    <Pagination
                        page={page}
                        totalPages={totalPages}
                        onPageChange={(newPage) => navigate(`/cms/korisnici?page=${newPage}`)}
                    />
                </>
            )}
        </div>
    );
}