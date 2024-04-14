import { axiosInstance } from "@/services/axiosInstance";
import { AppInfo } from "@/types/app";
import { User } from "@/types/user";

class UserService {
  public async postUser(user: User, captcha: string) {
    const { data } = await axiosInstance.post<number>(`/api/v1/users`, user, {
      params: {
        captcha
      }
    });
    return data;
  }

  public async login(user: User, captcha: string) {
    const params = new URLSearchParams();
    params.append("email", user.email);
    params.append("password", user.password);
    params.append("captcha", captcha);

    const { data } = await axiosInstance.post(`/api/login`, params, {
      headers: {
        "Content-type": "application/x-www-form-urlencoded"
      }
    });
    return data;
  }

  public async getUser() {
    const { data } = await axiosInstance.get<User>(`/api/v1/users`);
    return data;
  }

  public async getFavoriteApps(userId: number) {
    const { data } = await axiosInstance.get<AppInfo[]>(`/api/v1/users/${userId}`);
    return data;
  }

  public async addFavoriteApp(userId: number, appId: number) {
    await axiosInstance.post(`/api/v1/users/${userId}/apps`, appId);
  }

  public async logout() {
    localStorage.removeItem("user");
    await axiosInstance.post("/api/logout");
  }
}

export const userService = new UserService();
