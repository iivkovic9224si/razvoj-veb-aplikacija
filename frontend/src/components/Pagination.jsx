export default function Pagination({ page, totalPages, onPageChange }) {
    if (!totalPages || totalPages <= 1) {
        return null;
    }

    return (
        <div className="pagination">
            <button
                className="secondary"
                disabled={page <= 1}
                onClick={() => onPageChange(page - 1)}
            >
                Prethodna
            </button>

            <span>
        Strana {page} / {totalPages}
      </span>

            <button
                className="secondary"
                disabled={page >= totalPages}
                onClick={() => onPageChange(page + 1)}
            >
                Sledeća
            </button>
        </div>
    );
}