import { GameTable } from "@/pages/Home/GameTable";
import { Title } from "@/pages/Home/Title";

import styles from "./Home.module.css";

export function Home() {
  return (
    <div className={styles.home}>
      <Title />
      <GameTable />
    </div>
  );
}
