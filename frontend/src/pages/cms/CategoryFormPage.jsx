import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import api from "../../api/axios.js";

export default function CategoryFormPage() {
    const [ime, setIme] = useState("");
    const [opis, setOpis] = useState("");
    const [error, setError] = useState("");

    const { id } = useParams();
    const navigate = useNavigate();

    const isEdit = Boolean(id);

    useEffect(() => {
        async function loadCategoryForEdit() {
            if (!isEdit) {
                return;
            }

            try {
                const response = await api.get("/kategorije?page=1");
                const kategorija = response.data.podaci.find((k) => String(k.id) === String(id));

                if (!kategorija) {
                    setError("Kategorija nije pronađena na prvoj strani. Ako je nema, izmeni je direktno iz tabele.");
                    return;
                }

                setIme(kategorija.ime);
                setOpis(kategorija.opis);
            } catch (error) {
                setError("Greška pri učitavanju kategorije.");
            }
        }

        loadCategoryForEdit();
    }, [id, isEdit]);

    async function handleSubmit(e) {
        e.preventDefault();
        setError("");

        const body = {
            ime,
            opis,
        };

        try {
            if (isEdit) {
                await api.put(`/kategorije/${id}`, body);
            } else {
                await api.post("/kategorije", body);
            }

            navigate("/cms/kategorije");
        } catch (error) {
            const message = error.response?.data?.error || error.response?.data || "Greška pri čuvanju kategorije.";
            setError(String(message));
        }
    }

    return (
        <div className="container">
            <h1>{isEdit ? "Izmena kategorije" : "Nova kategorija"}</h1>

            <form className="form" onSubmit={handleSubmit}>
                {error && <div className="error">{error}</div>}

                <label>Ime kategorije</label>
                <input value={ime} onChange={(e) => setIme(e.target.value)} />

                <label>Opis</label>
                <textarea value={opis} onChange={(e) => setOpis(e.target.value)} />

                <button type="submit">Sačuvaj</button>
                <button type="button" className="secondary" onClick={() => navigate("/cms/kategorije")}>
                    Nazad
                </button>
            </form>
        </div>
    );
}