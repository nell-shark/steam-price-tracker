import { axiosInstance } from "@/services/axiosInstance";
import { AppInfo } from "@/types/app";
import { UserLoginRequest } from "@/types/user";

class UserService {
  private baseUrl: string = "/api/v1/users";

  public async postUser(user: UserLoginRequest, captcha: string) {
    const { data } = await axiosInstance.post<number>(this.baseUrl, user, {
      params: {
        captcha
      }
    });
    return data;
  }

  public async getFavoriteAppsByUserId(userId: number) {
    const { data } = await axiosInstance.get<AppInfo[]>(`${this.baseUrl}/${userId}/apps`);
    return data;
  }

  public async addFavoriteApp(userId: number, appId: number) {
    await axiosInstance.post(`${this.baseUrl}/${userId}/apps`, appId);
  }
}

export const userService = new UserService();
