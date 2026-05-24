import { useEffect, useState } from "react";
import { Link, useNavigate, useSearchParams } from "react-router-dom";
import api from "../../api/axios.js";
import Pagination from "../../components/Pagination.jsx";

export default function CmsNewsPage() {
    const [vesti, setVesti] = useState([]);
    const [totalPages, setTotalPages] = useState(1);
    const [error, setError] = useState("");
    const [loading, setLoading] = useState(true);
    const [searchInput, setSearchInput] = useState("");

    const [searchParams] = useSearchParams();
    const navigate = useNavigate();

    const page = Number(searchParams.get("page") || 1);
    const kategorija = searchParams.get("kategorija");
    const search = searchParams.get("search") || "";

    useEffect(() => {
        setSearchInput(search);
    }, [search]);

    useEffect(() => {
        let active = true;

        async function loadVesti() {
            try {
                setLoading(true);
                setError("");

                const params = new URLSearchParams();
                params.set("page", String(page));

                if (kategorija) {
                    params.set("kategorija", kategorija);
                }

                if (search.trim()) {
                    params.set("search", search.trim());
                }

                const response = await api.get(`/vesti?${params.toString()}`);

                if (!active) return;

                setVesti(response.data.podaci || []);
                setTotalPages(response.data.ukupnoStranica || 1);
            } catch (error) {
                if (!active) return;

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
    }, [page, kategorija, search]);

    async function handleDelete(id) {
        const confirmed = window.confirm("Da li sigurno želiš da obrišeš vest?");

        if (!confirmed) {
            return;
        }

        try {
            await api.delete(`/vesti/${id}`);

            const params = new URLSearchParams(searchParams);
            navigate(`/cms/vesti?${params.toString()}`, { replace: true });

            setVesti((old) => old.filter((vest) => vest.id !== id));
        } catch (error) {
            const message =
                error.response?.data?.error ||
                error.response?.data ||
                error.message ||
                "Greška pri brisanju vesti.";

            setError(String(message));
        }
    }

    function handleSearchSubmit(e) {
        e.preventDefault();

        const params = new URLSearchParams();

        if (searchInput.trim()) {
            params.set("search", searchInput.trim());
        }

        if (kategorija) {
            params.set("kategorija", kategorija);
        }

        params.set("page", "1");

        navigate(`/cms/vesti?${params.toString()}`);
    }

    function handleClearSearch() {
        const params = new URLSearchParams();

        if (kategorija) {
            params.set("kategorija", kategorija);
        }

        params.set("page", "1");
        setSearchInput("");
        navigate(`/cms/vesti?${params.toString()}`);
    }

    function goToPage(newPage) {
        const params = new URLSearchParams(searchParams);
        params.set("page", String(newPage));
        navigate(`/cms/vesti?${params.toString()}`);
    }

    return (
        <div className="container">
            <h1>CMS - Vesti</h1>

            {error && <div className="error">{error}</div>}

            <Link className="btn" to="/cms/vesti/nova">
                Dodaj vest
            </Link>

            <form className="form" onSubmit={handleSearchSubmit} style={{ marginTop: 16, marginBottom: 16 }}>
                <label>Pretraga vesti</label>
                <input
                    value={searchInput}
                    onChange={(e) => setSearchInput(e.target.value)}
                    placeholder="Pretraži po naslovu ili tekstu"
                />

                <button type="submit">Pretraži</button>
                <button type="button" className="secondary" onClick={handleClearSearch}>
                    Poništi
                </button>
            </form>

            {loading && <p>Učitavanje vesti...</p>}

            {!loading && (
                <>
                    <table className="table">
                        <thead>
                        <tr>
                            <th>Naslov</th>
                            <th>Autor</th>
                            <th>Kategorija</th>
                            <th>Datum objave</th>
                            <th>Akcije</th>
                        </tr>
                        </thead>

                        <tbody>
                        {vesti.map((vest) => (
                            <tr key={vest.id}>
                                <td>
                                    <Link to={`/vest/${vest.id}`}>{vest.naslov}</Link>
                                </td>
                                <td>{vest.autorIme}</td>
                                <td>{vest.kategorijaIme}</td>
                                <td>{vest.vremeKreiranja}</td>
                                <td>
                                    <Link className="btn secondary" to={`/cms/vesti/${vest.id}/izmena`}>
                                        Izmeni
                                    </Link>

                                    <button className="danger" onClick={() => handleDelete(vest.id)}>
                                        Obriši
                                    </button>
                                </td>
                            </tr>
                        ))}

                        {vesti.length === 0 && (
                            <tr>
                                <td colSpan="5">Nema vesti.</td>
                            </tr>
                        )}
                        </tbody>
                    </table>

                    <Pagination
                        page={page}
                        totalPages={totalPages}
                        onPageChange={goToPage}
                    />
                </>
            )}
        </div>
    );
}