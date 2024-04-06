import { useNavigate } from "react-router-dom";

import styles from "./AppItem.module.css";

export type AppItemProps = {
  readonly id: number;
  readonly name: string;
  readonly imageUrl: string;
};

export function AppItem({ id, name, imageUrl }: AppItemProps) {
  const navigate = useNavigate();
  return (
    <tr onClick={() => navigate(`/apps/${id}`)} className={styles.appItem}>
      <td>{id}</td>
      <td>{name}</td>
      <td>
        <img src={imageUrl} alt="" />{" "}
      </td>
    </tr>
  );
}
