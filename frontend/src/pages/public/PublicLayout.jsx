import { Outlet } from "react-router-dom";
import PublicNavbar from "../../components/PublicNavbar.jsx";
import TopReactionsBox from "../../components/TopReactionsBox.jsx";

export default function PublicLayout() {
    return (
        <>
            <PublicNavbar />

            <div className="public-layout">
                <main className="public-main">
                    <Outlet />
                </main>

                <aside className="public-sidebar">
                    <TopReactionsBox />
                </aside>
            </div>
        </>
    );
}