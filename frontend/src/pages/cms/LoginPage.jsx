import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../../context/AuthContext.jsx";

export default function LoginPage() {
    const [email, setEmail] = useState("admin@raf.rs");
    const [lozinka, setLozinka] = useState("admin123");
    const [error, setError] = useState("");
    const [loading, setLoading] = useState(false);

    const { login } = useAuth();
    const navigate = useNavigate();

    async function handleSubmit(e) {
        e.preventDefault();
        setError("");
        setLoading(true);

        try {
            const user = await login(email, lozinka);
            console.log("LOGIN USPEŠAN:", user);
            navigate("/cms/kategorije");
        } catch (error) {
            console.log("LOGIN GREŠKA:", error);

            const message =
                error.response?.data?.error ||
                error.response?.data ||
                error.message ||
                "Greška pri prijavi.";

            setError(String(message));
        } finally {
            setLoading(false);
        }
    }

    return (
        <div className="container">
            <h1>CMS Login</h1>

            <form className="form" onSubmit={handleSubmit}>
                {error && <div className="error">{error}</div>}

                <label>Email</label>
                <input
                    type="email"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                />

                <label>Lozinka</label>
                <input
                    type="password"
                    value={lozinka}
                    onChange={(e) => setLozinka(e.target.value)}
                />

                <button type="submit" disabled={loading}>
                    {loading ? "Prijava..." : "Prijavi se"}
                </button>
            </form>
        </div>
    );
}