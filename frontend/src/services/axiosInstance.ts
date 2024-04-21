import axios from "axios";

import { BASE_URL } from "@/data/constants.ts";
import { authService } from "@/services/authService";

export const axiosInstance = axios.create({
  baseURL: BASE_URL,
  withCredentials: true,
  withXSRFToken: true,
  headers: {
    "Content-type": "application/json"
  }
});

axiosInstance.interceptors.request.use(
  config => {
    const token = config.url?.includes("/api/v1/auth/refresh-token")
      ? localStorage.getItem("refreshToken")
      : localStorage.getItem("accessToken");

    config.headers.Authorization = token ? `Bearer ${token}` : "";
    return config;
  },
  error => {
    return Promise.reject(error);
  }
);

axiosInstance.interceptors.response.use(
  response => response,
  async error => {
    const status = error.response ? error.response.status : null;
    const refreshToken = localStorage.getItem("refreshToken");

    if (status === 401 && refreshToken) {
      try {
        await authService.refreshToken();
        return axiosInstance(error.config);
      } catch (error) {
        window.location.href = "/login";
      }
    }
    return Promise.reject(error);
  }
);
