import { axiosInstance } from "@/services/axiosInstance";
import { AppInfo } from "@/types/app";
import { UserLoginRequest } from "@/types/user";

class UserService {
  public async postUser(user: UserLoginRequest, captcha: string) {
    const { data } = await axiosInstance.post<number>(`/api/v1/auth/users`, user, {
      params: {
        captcha
      }
    });
    return data;
  }

  public async login(user: UserLoginRequest, captcha: string) {
    const params = new URLSearchParams();
    params.append("email", user.email);
    params.append("password", user.password);
    params.append("captcha", captcha);

    const { data } = await axiosInstance.post<number>(`/api/v1/auth/login`, params, {
      headers: {
        "Content-type": "application/x-www-form-urlencoded"
      }
    });

    return data;
  }

  public async getFavoriteAppsByUserId(userId: number) {
    const { data } = await axiosInstance.get<AppInfo[]>(`/api/v1/users/${userId}/apps`);
    return data;
  }

  public async addFavoriteApp(userId: number, appId: number) {
    await axiosInstance.post(`/api/v1/users/${userId}/apps`, appId);
  }

  public async logout() {
    await axiosInstance.post("/api/v1/auth/logout");
  }
}

export const userService = new UserService();
