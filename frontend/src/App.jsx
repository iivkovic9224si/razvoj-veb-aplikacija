import { Navigate, Route, Routes } from "react-router-dom";

import ProtectedRoute from "./components/ProtectedRoute.jsx";
import AdminRoute from "./components/AdminRoute.jsx";

import PublicLayout from "./pages/public/PublicLayout.jsx";
import HomePage from "./pages/public/HomePage.jsx";
import MostReadPage from "./pages/public/MostReadPage.jsx";
import CategoryPage from "./pages/public/CategoryPage.jsx";
import TagPage from "./pages/public/TagPage.jsx";
import SearchPage from "./pages/public/SearchPage.jsx";
import NewsDetailsPage from "./pages/public/NewsDetailsPage.jsx";

import LoginPage from "./pages/cms/LoginPage.jsx";
import CmsLayout from "./pages/cms/CmsLayout.jsx";
import CmsCategoriesPage from "./pages/cms/CmsCategoriesPage.jsx";
import CategoryFormPage from "./pages/cms/CategoryFormPage.jsx";
import CmsNewsPage from "./pages/cms/CmsNewsPage.jsx";
import NewsFormPage from "./pages/cms/NewsFormPage.jsx";
import CmsUsersPage from "./pages/cms/CmsUsersPage.jsx";
import UserFormPage from "./pages/cms/UserFormPage.jsx";

export default function App() {
    return (
        <Routes>
            <Route element={<PublicLayout />}>
                <Route path="/" element={<HomePage />} />
                <Route path="/najcitanije" element={<MostReadPage />} />
                <Route path="/kategorija/:id" element={<CategoryPage />} />
                <Route path="/tag/:tag" element={<TagPage />} />
                <Route path="/pretraga" element={<SearchPage />} />
                <Route path="/vest/:id" element={<NewsDetailsPage />} />
            </Route>

            <Route path="/login" element={<LoginPage />} />

            <Route
                path="/cms"
                element={
                    <ProtectedRoute>
                        <CmsLayout />
                    </ProtectedRoute>
                }
            >
                <Route index element={<Navigate to="/cms/kategorije" replace />} />

                <Route path="kategorije" element={<CmsCategoriesPage />} />
                <Route path="kategorije/nova" element={<CategoryFormPage />} />
                <Route path="kategorije/:id/izmena" element={<CategoryFormPage />} />

                <Route path="vesti" element={<CmsNewsPage />} />
                <Route path="vesti/nova" element={<NewsFormPage />} />
                <Route path="vesti/:id/izmena" element={<NewsFormPage />} />

                <Route
                    path="korisnici"
                    element={
                        <AdminRoute>
                            <CmsUsersPage />
                        </AdminRoute>
                    }
                />

                <Route
                    path="korisnici/novi"
                    element={
                        <AdminRoute>
                            <UserFormPage />
                        </AdminRoute>
                    }
                />

                <Route
                    path="korisnici/:id/izmena"
                    element={
                        <AdminRoute>
                            <UserFormPage />
                        </AdminRoute>
                    }
                />
            </Route>

            <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
    );
}