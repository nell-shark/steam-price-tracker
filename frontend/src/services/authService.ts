import axios from "axios";

import { BASE_URL } from "@/data/constants";
import { axiosInstance } from "@/services/axiosInstance";
import { AuthRequest, AuthResponse } from "@/types/auth";

class AuthService {
  private baseUrl: string = "/api/v1/auth";

  public async register(user: AuthRequest) {
    const { data } = await axiosInstance.post<AuthResponse>(`${this.baseUrl}/register`, user);
    this.setTokens(data);
    return data;
  }

  public async login(user: AuthRequest) {
    const { data } = await axiosInstance.post<AuthResponse>(`${this.baseUrl}/login`, user);
    this.setTokens(data);
    return data;
  }

  public async refreshToken() {
    console.log("refreshToken");
    const token = localStorage.getItem("refreshToken");
    this.removeTokens();
    const { data } = await axios.post<AuthResponse>(
      `${BASE_URL}${this.baseUrl}/refresh-token`,
      null,
      {
        headers: {
          Authorization: token ? `Bearer ${token}` : undefined
        }
      }
    );
    this.setTokens(data);
    return data;
  }

  public async logout() {
    this.removeTokens();
  }

  private setTokens(data: AuthResponse) {
    localStorage.setItem("accessToken", data.accessToken);
    localStorage.setItem("refreshToken", data.refreshToken);
  }

  private removeTokens() {
    localStorage.removeItem("accessToken");
    localStorage.removeItem("refreshToken");
  }
}

export const authService = new AuthService();
