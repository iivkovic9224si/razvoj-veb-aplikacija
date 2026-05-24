import { Link } from "react-router-dom";

export default function NewsList({ vesti }) {
    if (!vesti || vesti.length === 0) {
        return <p>Nema vesti.</p>;
    }

    return (
        <>
            {vesti.map((vest) => (
                <div className="card" key={vest.id}>
                    <h2>
                        <Link to={`/vest/${vest.id}`}>{vest.naslov}</Link>
                    </h2>

                    <p>
                        <b>Kategorija:</b> {vest.kategorijaIme} |{" "}
                        <b>Datum:</b> {vest.vremeKreiranja}
                    </p>

                    <p>{vest.tekst?.slice(0, 220)}...</p>

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
                </div>
            ))}
        </>
    );
}