import { useNavigate } from "react-router-dom";

export interface GameItemProps {
  id: number;
  name: string;
  price: number;
}

export function GameItem({ id, name, price }: GameItemProps) {
  const navigate = useNavigate();
  return (
    <tr onClick={() => navigate(`/games/${id}`)} style={{ cursor: "pointer" }}>
      <td>{id}</td>
      <td>{name}</td>
      <td>{price}</td>
    </tr>
  );
}
