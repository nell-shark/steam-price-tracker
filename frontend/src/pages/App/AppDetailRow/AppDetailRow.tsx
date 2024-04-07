import { Col, Row } from "react-bootstrap";

import styles from "./AppDetailRow.module.css";

export type AppDetailRowProps = {
  readonly label: string;
  readonly value: string | number | boolean | undefined;
};

export function AppDetailRow({ label, value }: AppDetailRowProps) {
  if (value === undefined || value === "") {
    return null;
  }

  return (
    <Row className={styles.row}>
      <Col className={styles.col}>{label}</Col>
      <Col>{value}</Col>
    </Row>
  );
}
