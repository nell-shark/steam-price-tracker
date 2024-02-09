import { useParams } from "react-router-dom";

import { gameService } from "@/services/gameService.ts";

export function Game() {
  const { id } = useParams();

  async function fetchGameInfo() {
    const result = await gameService.getGameById(Number(id));
    console.log(result);
  }

  void fetchGameInfo();

  return (
    <div>
      <h2>Game Page</h2>
      <p>Game ID: {id}</p>
    </div>
  );
}
