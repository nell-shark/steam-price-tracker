import { useEffect, useState } from "react";
import { Col, Row } from "react-bootstrap";
import { useParams } from "react-router-dom";

import { appService } from "@/services/appService";
import { AppById } from "@/types/app";

import styles from "./App.module.css";
import { AppDetailRow } from "./AppDetailRow";

export function App() {
  const { id } = useParams();
  const [app, setApp] = useState<AppById | null>(null);
  const [status, setStatus] = useState<"loading" | "completed" | "error">("loading");
  const [errorMessage, setErrorMessage] = useState<string>("");

  useEffect(() => {
    async function fetchAppInfo() {
      setStatus(() => "loading");
      try {
        const result = await appService.getAppById(Number(id));
        setApp(() => result);
        setStatus(() => "completed");
      } catch (error) {
        setErrorMessage(() => (error as Error).message);
        setStatus(() => "error");
      }
    }

    fetchAppInfo();
  }, [id]);

  const appDetails = [
    { label: "Id:", value: app?.id },
    { label: "Name:", value: app?.name },
    { label: "Type:", value: app?.type },
    { label: "Free:", value: app?.free ? "Yes" : "" },
    { label: "Platforms:", value: app?.platforms.join(" ") },
    { label: "Developers:", value: app?.developers },
    { label: "Publishers:", value: app?.publishers },
    { label: "Website:", value: app?.website },
    { label: "Metacritic:", value: app?.metacritic?.score },
    {
      label: "Release date:",
      value: app?.releaseDate?.comingSoon ? "soon" : app?.releaseDate?.releaseDate
    },
    { label: "Description:", value: app?.shortDescription }
  ];

  return (
    <div className={styles.app}>
      {status === "loading" && <h1>Loading...</h1>}
      {status === "error" && (
        <div>
          <h1>Error!</h1>
          <p>{errorMessage}</p>
        </div>
      )}
      {status === "completed" && app && (
        <>
          {appDetails.map(detail => (
            <AppDetailRow key={detail.label} label={detail.label} value={detail.value} />
          ))}
          <img src={app.headerImage} className="w-100 rounded" alt="" />
        </>
      )}
    </div>
  );
}
