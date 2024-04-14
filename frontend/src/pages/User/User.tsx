import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";

import { userService } from "@/services/userService";
import { AppInfo } from "@/types/app";

export function User() {
  const { id } = useParams();
  const [favoriteApps, setFavoriteApps] = useState<AppInfo[] | null>(null);
  const [errorMessage, setErrorMessage] = useState<string>("");

  useEffect(() => {
    async function fetchFavoriteApps() {
      try {
        const data = await userService.getFavoriteAppsByUserId(Number(id));
        setFavoriteApps(() => data);
      } catch (error) {
        const e = error as Error;
        setErrorMessage(() => e.message);
      }
    }
    fetchFavoriteApps();
  }, [id]);

  return (
    <div>
      {errorMessage && <h1>{errorMessage}</h1>}
      {favoriteApps?.map(app => <p key={app.id}>{app.id}</p>)}
    </div>
  );
}
