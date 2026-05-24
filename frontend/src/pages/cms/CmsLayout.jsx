import { Outlet } from "react-router-dom";
import CmsNavbar from "../../components/CmsNavbar.jsx";

export default function CmsLayout() {
    return (
        <>
            <CmsNavbar />
            <Outlet />
        </>
    );
}