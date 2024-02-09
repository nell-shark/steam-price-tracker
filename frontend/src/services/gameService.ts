import { axiosInstance } from "@/services/axiosInstanse.ts";

class GameService {
  public async getAllGameDTOs() {
    const { data } = await axiosInstance.get("/api/v1/games");
    return data;
  }

  public async getGameById(id: number) {
    const { data } = await axiosInstance.get(`/api/v1/games/${id}`);
    return data;
  }
}

export const gameService = new GameService();
