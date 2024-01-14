export interface GameItemProps {
  id: number;
  name: string;
  price: number;
}

export function GameItem({ id, name, price }: GameItemProps) {
  return (
    <tr onClick={() => console.log(`Clicked ${name}`)} style={{ cursor: "pointer" }}>
      <td>{id}</td>
      <td>{name}</td>
      <td>{price}</td>
    </tr>
  );
}
