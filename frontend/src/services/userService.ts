import { axiosInstance } from "@/services/axiosInstance";
import { AppInfo } from "@/types/app";

class UserService {
  private baseUrl: string = "/api/v1/users";

  public async getFavoriteAppsByUserId(userId: number) {
    const { data } = await axiosInstance.get<AppInfo[]>(`${this.baseUrl}/${userId}/apps`);
    return data;
  }

  public async addFavoriteApp(userId: number, appId: number) {
    await axiosInstance.post(`${this.baseUrl}/${userId}/apps`, appId);
  }
}

export const userService = new UserService();
