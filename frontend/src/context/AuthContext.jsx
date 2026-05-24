import { createContext, useContext, useState } from "react";
import api from "../api/axios.js";

const AuthContext = createContext(null);

function getInitialUser() {
    try {
        const savedUser = localStorage.getItem("user");

        if (!savedUser) {
            return null;
        }

        const parsedUser = JSON.parse(savedUser);

        if (!parsedUser?.jwt) {
            localStorage.removeItem("user");
            return null;
        }

        return parsedUser;
    } catch (error) {
        localStorage.removeItem("user");
        return null;
    }
}

export function AuthProvider({ children }) {
    const [user, setUser] = useState(getInitialUser);

    async function login(email, lozinka) {
        const response = await api.post("/login", {
            email: email.trim(),
            lozinka,
        });

        const loggedUser = response.data;

        console.log("ODGOVOR SA BACKEND LOGIN RUTE:", loggedUser);

        if (!loggedUser?.jwt) {
            throw new Error("Backend nije vratio JWT token.");
        }

        localStorage.setItem("user", JSON.stringify(loggedUser));
        setUser(loggedUser);

        return loggedUser;
    }

    function logout() {
        localStorage.removeItem("user");
        setUser(null);
    }

    const normalizedTip = user?.tip?.trim()?.toUpperCase();

    const value = {
        user,
        loading: false,
        login,
        logout,
        isLoggedIn: Boolean(user?.jwt),
        isAdmin: normalizedTip === "ADMINISTRATOR",
        isContentCreator: normalizedTip === "STVARALAC_SADRZAJA",
    };

    return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
    return useContext(AuthContext);
}