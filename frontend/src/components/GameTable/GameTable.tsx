import { Container } from "react-bootstrap";
import Table from "react-bootstrap/Table";

import { GameItem, GameItemProps } from "./GameItem";

const games: GameItemProps[] = [
  { id: 1, name: "GTA 5", price: 100 },
  { id: 2, name: "CS:GO", price: 200 },
  { id: 3, name: "Rust", price: 300 },
  { id: 4, name: "Hero Siege", price: 400 },
  { id: 5, name: "Dayz", price: 500 },
  { id: 6, name: "Warhammer", price: 600 },
  { id: 7, name: "Broforce", price: 700 },
  { id: 8, name: "Starcraft", price: 800 },
  { id: 9, name: "Need for Speed", price: 900 }
];

export function GameTable() {
  return (
    <Container>
      <Table hover>
        <thead className="table-dark">
        <tr>
          <th>#</th>
          <th>Name</th>
          <th>Price</th>
        </tr>
        </thead>
        <tbody>
        {games.map(game => (
          <GameItem key={game.id} {...game} />
        ))}
        </tbody>
      </Table>
    </Container>
  );
}
