import axios from "axios";

const api = axios.create({
    baseURL: "http://localhost:4567/api",
    withCredentials: true,
});

api.interceptors.request.use((config) => {
    const user = JSON.parse(localStorage.getItem("user"));

    if (user?.jwt) {
        config.headers.Authorization = `Bearer ${user.jwt}`;
    }

    return config;
});

export default api;