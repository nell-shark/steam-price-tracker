import { FormEvent, useRef } from "react";
import { Form, InputGroup } from "react-bootstrap";
import { useSearchParams } from "react-router-dom";

export function SearchForm() {
  const searchInputRef = useRef<HTMLInputElement>(null);
  const [searchParams, setSearchParams] = useSearchParams({ search: "" });

  function handleSubmit(e: FormEvent<HTMLFormElement>) {
    e.preventDefault();
    setSearchParams(prev => {
      const searchValue = searchInputRef.current?.value ?? "";
      if (searchValue === "") {
        prev.delete("search");
      } else {
        prev.set("search", searchValue);
      }
      prev.set("page", String(1));
      return prev;
    });
  }

  return (
    <form onSubmit={e => handleSubmit(e)}>
      <div className="justify-content-center mb-5 row">
        <div className="col-lg-6">
          <InputGroup className="mb-3">
            <InputGroup.Text id="basic-addon1">🔎︎</InputGroup.Text>
            <Form.Control
              placeholder="Search a app"
              aria-label="search"
              aria-describedby="basic-addon1"
              size="lg"
              ref={searchInputRef}
              defaultValue={searchParams.get("search") ?? ""}
            />
          </InputGroup>
        </div>
      </div>
    </form>
  );
}
