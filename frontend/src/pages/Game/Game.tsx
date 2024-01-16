import { useParams } from "react-router-dom";

export function Game() {
  const { id } = useParams();
  return (
    <div>
      <h2>Game Page</h2>
      <p>Game ID: {id}</p>
    </div>
  );
}
