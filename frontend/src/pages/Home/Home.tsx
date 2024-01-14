import { GameTable } from "@/components/GameTable";
import { NavBar } from "@/components/NavBar";

import { Title } from "./Title";

export function Home() {
  return (
    <>
      <NavBar />
      <Title />
      <GameTable />
    </>
  );
}
