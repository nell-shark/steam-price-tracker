import { useEffect, useState } from "react";
import Table from "react-bootstrap/Table";
import { PaginationControl } from "react-bootstrap-pagination-control";
import { useSearchParams } from "react-router-dom";

import { appService } from "@/services/appService";
import { AppsByPage } from "@/types/app";

import { AppItem } from "./AppItem/AppItem";
import styles from "./AppTable.module.css";
import { SearchForm } from "./SearchForm";

export function AppTable() {
  const [activePage, setActivePage] = useState<number>(1);
  const [appsByPage, setAppsByPage] = useState<AppsByPage | null>(null);
  const [searchParams] = useSearchParams({ search: "" });
  const [status, setStatus] = useState<"loading" | "completed" | "error">("loading");
  const [errorMessage, setErrorMessage] = useState<string>("");

  useEffect(() => {
    async function fetchAppsByPage(page: number) {
      setStatus(() => "loading");
      try {
        const res = searchParams.get("search")
          ? await appService.searchAppsByPage(searchParams.get("search") ?? "", activePage)
          : await appService.getAppsByPage(page);
        console.log(res);
        setAppsByPage(res);
        setStatus(() => "completed");
      } catch (error) {
        setStatus(() => "error");
        setErrorMessage(() => (error as Error).message);
      }
    }

    fetchAppsByPage(activePage);
  }, [activePage, searchParams]);

  return (
    <>
      {status === "loading" && <h1>Loading...</h1>}
      {status === "error" && <h1>{errorMessage}</h1>}
      {status === "completed" && (
        <div className="text-center">
          <SearchForm />
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
      )}
    </>
  );
}
