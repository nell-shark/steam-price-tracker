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
    const { data } = await axiosInstance.post<AuthResponse>(`${this.baseUrl}/refresh-token`);
    this.setTokens(data);
    return data;
  }

  public async logout() {
    localStorage.removeItem("accessToken");
    localStorage.removeItem("refreshToken");
    await axiosInstance.post(`${this.baseUrl}/logout`);
  }

  private setTokens(data: AuthResponse) {
    localStorage.setItem("accessToken", data.accessToken);
    localStorage.setItem("refreshToken", data.refreshToken);
  }
}

export const authService = new AuthService();
