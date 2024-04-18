import { useEffect, useState } from "react";
import Table from "react-bootstrap/Table";
import { PaginationControl } from "react-bootstrap-pagination-control";
import { useSearchParams } from "react-router-dom";

import { AppItem } from "@/components/AppItem";
import { appService } from "@/services/appService";
import { AppsByPage } from "@/types/app";

import styles from "./AppTable.module.css";
import { SearchForm } from "./SearchForm";

export function AppTable() {
  const [appsByPage, setAppsByPage] = useState<AppsByPage | null>(null);
  const [searchParams, setSearchParams] = useSearchParams({ page: "1" });
  const [status, setStatus] = useState<"loading" | "completed" | "error">("loading");
  const [errorMessage, setErrorMessage] = useState<string>("");

  useEffect(() => {
    async function fetchAppsByPage(page: number, search?: string) {
      setStatus(() => "loading");
      try {
        const res = search
          ? await appService.searchAppsByPage(search, page)
          : await appService.getAppsByPage(page);
        setAppsByPage(res);
        setStatus(() => "completed");
      } catch (error) {
        setStatus(() => "error");
        setErrorMessage(() => (error as Error).message);
      }
    }

    const page = Number(searchParams.get("page"));
    const search = searchParams.get("search") ?? undefined;
    fetchAppsByPage(page, search);
  }, [searchParams]);

  return (
    <>
      {status === "loading" && <h1>Loading...</h1>}
      {status === "error" && <h1>{errorMessage}</h1>}
      {status === "completed" && appsByPage && (
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
              {// eslint-disable-next-line @typescript-eslint/no-unnecessary-condition
              appsByPage.content?.map(app => (
                <AppItem key={app.id} id={app.id} name={app.name} imageUrl={app.imageUrl} />
              ))}
            </tbody>
          </Table>
          <PaginationControl
            page={Number(searchParams.get("page")) || 1}
            between={4}
            total={appsByPage.totalElements}
            limit={appsByPage.size}
            changePage={page =>
              setSearchParams(prev => {
                prev.set("page", page.toString());
                return prev;
              })
            }
            ellipsis={1}
          />
        </div>
      )}
    </>
  );
}
