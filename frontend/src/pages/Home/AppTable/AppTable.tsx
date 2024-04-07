import { useEffect, useState } from "react";
import Table from "react-bootstrap/Table";
import { PaginationControl } from "react-bootstrap-pagination-control";

import { appService } from "@/services/appService";
import { AppsByPage } from "@/types/app";

import { AppItem } from "./AppItem/AppItem";
import styles from "./AppTable.module.css";

export function AppTable() {
  const [activePage, setActivePage] = useState<number>(1);
  const [appsByPage, setAppsByPage] = useState<AppsByPage>();

  useEffect(() => {
    async function fetchGamesByPage(page: number) {
      const res = await appService.getAppsByPage(page);
      setAppsByPage(res);
    }
    fetchGamesByPage(activePage);
  }, [activePage]);

  return (
    <div className="text-center">
      <Table hover className={`overflow-hidden rounded ${styles.appTable}`}>
        <thead className="table-dark">
          <tr>
            <th>id</th>
            <th>Name</th>
            <th>Image</th>
          </tr>
        </thead>
        <tbody>
          {appsByPage?.content.map(app => (
            <AppItem key={app.id} id={app.id} name={app.name} imageUrl={app.imageUrl} />
          ))}
        </tbody>
      </Table>
      {appsByPage && (
        <PaginationControl
          page={activePage}
          between={4}
          total={appsByPage.totalElements}
          limit={appsByPage.size}
          changePage={page => setActivePage(() => page)}
          ellipsis={1}
        />
      )}
    </div>
  );
}
