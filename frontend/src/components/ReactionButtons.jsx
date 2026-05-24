export default function ReactionButtons({
                                            likes,
                                            dislikes,
                                            onLike,
                                            onDislike,
                                            disabled = false,
                                        }) {
    return (
        <div style={{ marginTop: 10, marginBottom: 10 }}>
            <button type="button" onClick={onLike} disabled={disabled}>
                👍 Like ({likes || 0})
            </button>

            <button type="button" className="secondary" onClick={onDislike} disabled={disabled}>
                👎 Dislike ({dislikes || 0})
            </button>
        </div>
    );
}