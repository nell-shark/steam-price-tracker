import { axiosInstance } from "@/services/axiosInstanse.ts";

class GameService {
  public async getAllGameDTOs() {
    const { data } = await axiosInstance.get("/api/v1/apps");
    return data;
  }

  public async getGameById(id: number) {
    const { data } = await axiosInstance.get(`/api/v1/apps/${id}`);
    return data;
  }
}

export const gameService = new GameService();
