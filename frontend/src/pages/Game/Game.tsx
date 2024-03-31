import { useParams } from "react-router-dom";

import { gameService } from "@/services/gameService.ts";

import styles from "./Game.module.css";

export function Game() {
  const { id } = useParams();

  async function fetchGameInfo() {
    const result = await gameService.getGameById(Number(id));
    console.log(result);
  }

  void fetchGameInfo();

  return (
    <div className={styles.game}>
      <h2>Game Page</h2>
      <p>Game ID: {id}</p>
    </div>
  );
}
