import { axiosInstance } from "@/services/axiosInstance";
import { UserLoginRequest } from "@/types/user";

class AuthService {
  private baseUrl: string = "/api/v1/auth";

  public async login(user: UserLoginRequest, captcha: string) {
    const params = new URLSearchParams();
    params.append("email", user.email);
    params.append("password", user.password);
    params.append("captcha", captcha);

    const { data } = await axiosInstance.post<number>(`${this.baseUrl}/login`, params, {
      headers: {
        "Content-type": "application/x-www-form-urlencoded"
      }
    });

    return data;
  }

  public async logout() {
    await axiosInstance.post(`${this.baseUrl}/logout`);
  }
}

export const authService = new AuthService();
