import { AppTable } from "@/pages/Home/AppTable";
import { Title } from "@/pages/Home/Title";

import styles from "./Home.module.css";

export function Home() {
  return (
    <div className={styles.home}>
      <Title />
      <AppTable />
    </div>
  );
}
