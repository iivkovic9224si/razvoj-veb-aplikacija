import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import api from "../../api/axios.js";

export default function NewsFormPage() {
    const [naslov, setNaslov] = useState("");
    const [tekst, setTekst] = useState("");
    const [kategorijaId, setKategorijaId] = useState("");
    const [tagInput, setTagInput] = useState("");
    const [kategorije, setKategorije] = useState([]);

    const [error, setError] = useState("");
    const [loading, setLoading] = useState(true);
    const [saving, setSaving] = useState(false);

    const { id } = useParams();
    const navigate = useNavigate();

    const isEdit = Boolean(id);

    useEffect(() => {
        let active = true;

        async function loadData() {
            try {
                setLoading(true);
                setError("");

                const kategorijeResponse = await api.get("/kategorije?page=1");

                if (!active) return;

                const loadedKategorije = kategorijeResponse.data.podaci || [];
                setKategorije(loadedKategorije);

                if (!isEdit && loadedKategorije.length > 0) {
                    setKategorijaId(String(loadedKategorije[0].id));
                }

                if (isEdit) {
                    const vestResponse = await api.get(`/vesti/${id}`);
                    const vest = vestResponse.data;

                    if (!active) return;

                    setNaslov(vest.naslov || "");
                    setTekst(vest.tekst || "");
                    setTagInput((vest.tagovi || []).join(", "));

                    const foundCategory = loadedKategorije.find(
                        (kategorija) => kategorija.ime === vest.kategorijaIme
                    );

                    if (foundCategory) {
                        setKategorijaId(String(foundCategory.id));
                    } else if (loadedKategorije.length > 0) {
                        setKategorijaId(String(loadedKategorije[0].id));
                    }
                }
            } catch (error) {
                if (!active) return;

                const message =
                    error.response?.data?.error ||
                    error.response?.data ||
                    error.message ||
                    "Greška pri učitavanju forme.";

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
    }, [id, isEdit]);

    function parseTagovi() {
        return tagInput
            .split(",")
            .map((tag) => tag.trim())
            .filter(Boolean);
    }

    async function handleSubmit(e) {
        e.preventDefault();
        setError("");
        setSaving(true);

        const body = {
            naslov,
            tekst,
            kategorijaId: Number(kategorijaId),
            tagovi: parseTagovi(),
        };

        try {
            if (isEdit) {
                await api.put(`/vesti/${id}`, body);
            } else {
                await api.post("/vesti", body);
            }

            navigate("/cms/vesti");
        } catch (error) {
            const message =
                error.response?.data?.error ||
                error.response?.data ||
                error.message ||
                "Greška pri čuvanju vesti.";

            setError(String(message));
        } finally {
            setSaving(false);
        }
    }

    if (loading) {
        return (
            <div className="container">
                <p>Učitavanje forme...</p>
            </div>
        );
    }

    return (
        <div className="container">
            <h1>{isEdit ? "Izmena vesti" : "Nova vest"}</h1>

            <form className="form" onSubmit={handleSubmit}>
                {error && <div className="error">{error}</div>}

                <label>Naslov</label>
                <input
                    value={naslov}
                    onChange={(e) => setNaslov(e.target.value)}
                    placeholder="Naslov vesti"
                />

                <label>Kategorija</label>
                <select
                    value={kategorijaId}
                    onChange={(e) => setKategorijaId(e.target.value)}
                >
                    {kategorije.map((kategorija) => (
                        <option key={kategorija.id} value={kategorija.id}>
                            {kategorija.ime}
                        </option>
                    ))}
                </select>

                <label>Tekst</label>
                <textarea
                    value={tekst}
                    onChange={(e) => setTekst(e.target.value)}
                    placeholder="Tekst vesti"
                />

                <label>Tagovi</label>
                <input
                    value={tagInput}
                    onChange={(e) => setTagInput(e.target.value)}
                    placeholder="Java, RAF, Sport"
                />

                <button type="submit" disabled={saving}>
                    {saving ? "Čuvanje..." : "Sačuvaj"}
                </button>

                <button type="button" className="secondary" onClick={() => navigate("/cms/vesti")}>
                    Nazad
                </button>
            </form>
        </div>
    );
}