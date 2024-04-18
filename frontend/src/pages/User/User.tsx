import { useEffect, useState } from "react";
import { Table } from "react-bootstrap";
import { useParams } from "react-router-dom";

import { AppItem } from "@/components/AppItem";
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
      <Table hover className={`overflow-hidden rounded`}>
        <thead className="table-dark">
          <tr>
            <th>id</th>
            <th>Name</th>
            <th>Image</th>
          </tr>
        </thead>
        <tbody>
          {favoriteApps?.map(app => (
            <AppItem key={app.id} id={app.id} name={app.name} imageUrl={app.imageUrl} />
          ))}
        </tbody>
      </Table>
    </div>
  );
}
