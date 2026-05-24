import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import api from "../../api/axios.js";

export default function UserFormPage() {
    const [ime, setIme] = useState("");
    const [prezime, setPrezime] = useState("");
    const [email, setEmail] = useState("");
    const [tip, setTip] = useState("STVARALAC_SADRZAJA");
    const [lozinka, setLozinka] = useState("");
    const [lozinkaPotvrda, setLozinkaPotvrda] = useState("");

    const [error, setError] = useState("");
    const [loading, setLoading] = useState(false);
    const [saving, setSaving] = useState(false);

    const { id } = useParams();
    const navigate = useNavigate();

    const isEdit = Boolean(id);

    useEffect(() => {
        let active = true;

        async function loadUser() {
            if (!isEdit) {
                return;
            }

            try {
                setLoading(true);
                setError("");

                const response = await api.get(`/korisnici/${id}`);
                const korisnik = response.data;

                if (!active) return;

                setIme(korisnik.ime || "");
                setPrezime(korisnik.prezime || "");
                setEmail(korisnik.email || "");
                setTip(korisnik.tip || "STVARALAC_SADRZAJA");
            } catch (error) {
                if (!active) return;

                const message =
                    error.response?.data?.error ||
                    error.response?.data ||
                    error.message ||
                    "Greška pri učitavanju korisnika.";

                setError(String(message));
            } finally {
                if (active) {
                    setLoading(false);
                }
            }
        }

        loadUser();

        return () => {
            active = false;
        };
    }, [id, isEdit]);

    async function handleSubmit(e) {
        e.preventDefault();
        setError("");
        setSaving(true);

        const body = {
            ime,
            prezime,
            email,
            tip,
        };

        if (!isEdit) {
            body.lozinka = lozinka;
            body.lozinkaPotvrda = lozinkaPotvrda;
        }

        try {
            if (isEdit) {
                await api.put(`/korisnici/${id}`, body);
            } else {
                await api.post("/korisnici", body);
            }

            navigate("/cms/korisnici");
        } catch (error) {
            const message =
                error.response?.data?.error ||
                error.response?.data ||
                error.message ||
                "Greška pri čuvanju korisnika.";

            setError(String(message));
        } finally {
            setSaving(false);
        }
    }

    if (loading) {
        return (
            <div className="container">
                <p>Učitavanje korisnika...</p>
            </div>
        );
    }

    return (
        <div className="container">
            <h1>{isEdit ? "Izmena korisnika" : "Novi korisnik"}</h1>

            <form className="form" onSubmit={handleSubmit}>
                {error && <div className="error">{error}</div>}

                <label>Ime</label>
                <input value={ime} onChange={(e) => setIme(e.target.value)} />

                <label>Prezime</label>
                <input value={prezime} onChange={(e) => setPrezime(e.target.value)} />

                <label>Email</label>
                <input type="email" value={email} onChange={(e) => setEmail(e.target.value)} />

                <label>Tip korisnika</label>
                <select value={tip} onChange={(e) => setTip(e.target.value)}>
                    <option value="STVARALAC_SADRZAJA">STVARALAC_SADRZAJA</option>
                    <option value="ADMINISTRATOR">ADMINISTRATOR</option>
                </select>

                {!isEdit && (
                    <>
                        <label>Lozinka</label>
                        <input
                            type="password"
                            value={lozinka}
                            onChange={(e) => setLozinka(e.target.value)}
                        />

                        <label>Potvrda lozinke</label>
                        <input
                            type="password"
                            value={lozinkaPotvrda}
                            onChange={(e) => setLozinkaPotvrda(e.target.value)}
                        />
                    </>
                )}

                <button type="submit" disabled={saving}>
                    {saving ? "Čuvanje..." : "Sačuvaj"}
                </button>

                <button type="button" className="secondary" onClick={() => navigate("/cms/korisnici")}>
                    Nazad
                </button>
            </form>
        </div>
    );
}