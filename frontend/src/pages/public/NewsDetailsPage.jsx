import { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import api from "../../api/axios.js";
import ReactionButtons from "../../components/ReactionButtons.jsx";
import Pagination from "../../components/Pagination.jsx";

export default function NewsDetailsPage() {
    const [vest, setVest] = useState(null);
    const [komentari, setKomentari] = useState([]);
    const [povezaneVesti, setPovezaneVesti] = useState([]);
    const [commentsPage, setCommentsPage] = useState(1);
    const [commentsTotalPages, setCommentsTotalPages] = useState(1);

    const [autorKomentara, setAutorKomentara] = useState("");
    const [tekstKomentara, setTekstKomentara] = useState("");

    const [error, setError] = useState("");
    const [commentError, setCommentError] = useState("");
    const [loading, setLoading] = useState(true);
    const [reactionLoading, setReactionLoading] = useState(false);

    const { id } = useParams();

    async function loadVest() {
        const response = await api.get(`/vesti/${id}`);
        setVest(response.data);
    }

    async function loadKomentari(page = commentsPage) {
        const response = await api.get(`/vesti/${id}/komentari?page=${page}`);
        setKomentari(response.data.podaci || []);
        setCommentsTotalPages(response.data.ukupnoStranica || 1);
    }

    useEffect(() => {
        let active = true;

        async function loadData() {
            try {
                setLoading(true);
                setError("");

                const vestResponse = await api.get(`/vesti/${id}`);
                const komentariResponse = await api.get(`/vesti/${id}/komentari?page=1`);
                const povezaneResponse = await api.get(`/vesti/${id}/povezane`);

                if (!active) return;

                setVest(vestResponse.data);
                setKomentari(komentariResponse.data.podaci || []);
                setCommentsPage(1);
                setCommentsTotalPages(komentariResponse.data.ukupnoStranica || 1);
                setPovezaneVesti(povezaneResponse.data || []);
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

        loadData();

        return () => {
            active = false;
        };
    }, [id]);

    async function handleNewsLike() {
        try {
            setReactionLoading(true);

            await api.post(`/vesti/${id}/like`);
            await loadVest();

            window.dispatchEvent(new Event("reactionsChanged"));
        } catch (error) {
            alert(error.response?.data?.error || error.response?.data || "Greška pri lajkovanju vesti.");
        } finally {
            setReactionLoading(false);
        }
    }

    async function handleNewsDislike() {
        try {
            setReactionLoading(true);

            await api.post(`/vesti/${id}/dislike`);
            await loadVest();

            window.dispatchEvent(new Event("reactionsChanged"));
        } catch (error) {
            alert(error.response?.data?.error || error.response?.data || "Greška pri dislajkovanju vesti.");
        } finally {
            setReactionLoading(false);
        }
    }

    async function handleCommentSubmit(e) {
        e.preventDefault();
        setCommentError("");

        try {
            await api.post("/komentari", {
                vestId: Number(id),
                autor: autorKomentara,
                tekst: tekstKomentara,
            });

            setAutorKomentara("");
            setTekstKomentara("");

            await loadKomentari(1);
            setCommentsPage(1);
        } catch (error) {
            const message =
                error.response?.data?.error ||
                error.response?.data ||
                error.message ||
                "Greška pri dodavanju komentara.";

            setCommentError(String(message));
        }
    }

    async function handleCommentLike(commentId) {
        try {
            await api.post(`/komentari/${commentId}/like`);
            await loadKomentari(commentsPage);
        } catch (error) {
            alert(error.response?.data?.error || error.response?.data || "Greška pri lajkovanju komentara.");
        }
    }

    async function handleCommentDislike(commentId) {
        try {
            await api.post(`/komentari/${commentId}/dislike`);
            await loadKomentari(commentsPage);
        } catch (error) {
            alert(error.response?.data?.error || error.response?.data || "Greška pri dislajkovanju komentara.");
        }
    }

    async function handleCommentsPageChange(newPage) {
        setCommentsPage(newPage);
        await loadKomentari(newPage);
    }

    if (loading) {
        return (
            <div className="container">
                <p>Učitavanje vesti...</p>
            </div>
        );
    }

    if (error) {
        return (
            <div className="container">
                <div className="error">{error}</div>
            </div>
        );
    }

    if (!vest) {
        return (
            <div className="container">
                <p>Vest nije pronađena.</p>
            </div>
        );
    }

    return (
        <div className="container">
            <div className="card">
                <h1>{vest.naslov}</h1>

                <p>
                    <b>Autor:</b> {vest.autorIme} |{" "}
                    <b>Kategorija:</b> {vest.kategorijaIme} |{" "}
                    <b>Datum:</b> {vest.vremeKreiranja}
                </p>

                <p>
                    <b>Broj poseta:</b> {vest.brojPoseta}
                </p>

                <ReactionButtons
                    likes={vest.brojLajkova}
                    dislikes={vest.brojDislajkova}
                    onLike={handleNewsLike}
                    onDislike={handleNewsDislike}
                    disabled={reactionLoading}
                />

                <hr />

                <p style={{ whiteSpace: "pre-wrap" }}>{vest.tekst}</p>

                {vest.tagovi && vest.tagovi.length > 0 && (
                    <p>
                        <b>Tagovi:</b>{" "}
                        {vest.tagovi.map((tag) => (
                            <Link key={tag} to={`/tag/${encodeURIComponent(tag)}`} style={{ marginRight: 8 }}>
                                #{tag}
                            </Link>
                        ))}
                    </p>
                )}

                {povezaneVesti.length > 0 && (
                    <>
                        <hr />

                        <h3>Pročitaj još...</h3>

                        {povezaneVesti.map((povezanaVest) => (
                            <div key={povezanaVest.id} style={{ marginBottom: 8 }}>
                                <Link to={`/vest/${povezanaVest.id}`}>
                                    {povezanaVest.naslov}
                                </Link>

                                <div style={{ fontSize: 13, color: "#555" }}>
                                    {povezanaVest.kategorijaIme} | {povezanaVest.vremeKreiranja}
                                </div>
                            </div>
                        ))}
                    </>
                )}
            </div>

            <div className="card">
                <h2>Dodaj komentar</h2>

                {commentError && <div className="error">{commentError}</div>}

                <form className="form" onSubmit={handleCommentSubmit}>
                    <label>Ime autora</label>
                    <input
                        value={autorKomentara}
                        onChange={(e) => setAutorKomentara(e.target.value)}
                        placeholder="Unesi ime"
                    />

                    <label>Komentar</label>
                    <textarea
                        value={tekstKomentara}
                        onChange={(e) => setTekstKomentara(e.target.value)}
                        placeholder="Unesi komentar"
                    />

                    <button type="submit">Objavi komentar</button>
                </form>
            </div>

            <div className="card">
                <h2>Komentari</h2>

                {komentari.length === 0 && <p>Nema komentara.</p>}

                {komentari.map((komentar) => (
                    <div key={komentar.id} className="card">
                        <p>
                            <b>{komentar.autor}</b> | {komentar.vremeKreiranja || komentar.datumKreiranja}
                        </p>

                        <p>{komentar.tekst}</p>

                        <ReactionButtons
                            likes={komentar.brojLajkova}
                            dislikes={komentar.brojDislajkova}
                            onLike={() => handleCommentLike(komentar.id)}
                            onDislike={() => handleCommentDislike(komentar.id)}
                        />
                    </div>
                ))}

                <Pagination
                    page={commentsPage}
                    totalPages={commentsTotalPages}
                    onPageChange={handleCommentsPageChange}
                />
            </div>
        </div>
    );
}