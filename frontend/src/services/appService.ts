import { axiosInstance } from "@/services/axiosInstance";
import { AppsByPage } from "@/types/app";

class AppService {
  public async getAppsByPage(page: number = 1) {
    const { data } = await axiosInstance.get<AppsByPage>("/api/v1/apps", {
      params: {
        page
      }
    });
    return data;
  }

  public async getAppById(id: number) {
    const { data } = await axiosInstance.get(`/api/v1/apps/${id}`);
    return data;
  }
}

export const appService = new AppService();
