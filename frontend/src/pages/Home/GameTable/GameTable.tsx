import Table from "react-bootstrap/Table";

import { GameItem, GameItemProps } from "./GameItem";

const apps: GameItemProps[] = [
  { id: 1, name: "GTA 5", price: 100 },
  { id: 2, name: "CS:GO", price: 200 },
  { id: 3, name: "Rust", price: 300 },
  { id: 4, name: "Hero Siege", price: 400 },
  { id: 5, name: "Dayz", price: 500 },
  { id: 6, name: "Warhammer", price: 600 },
  { id: 7, name: "Broforce", price: 700 },
  { id: 8, name: "Starcraft", price: 800 },
  { id: 9, name: "Need for Speed", price: 900 },
  { id: 620, name: "Portal 2", price: 1000 }
];

export function GameTable() {
  return (
    <Table hover className="overflow-hidden rounded">
      <thead className="table-dark">
        <tr>
          <th>#</th>
          <th>Name</th>
          <th>Price</th>
        </tr>
      </thead>
      <tbody>
        {apps.map(app => (
          <GameItem key={app.id} {...app} />
        ))}
      </tbody>
    </Table>
  );
}
