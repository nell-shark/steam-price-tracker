import { Form, InputGroup } from "react-bootstrap";

export function Title() {
  return (
    <div className="my-5">
      <h1 className="display-4 text-center">Steam Price Tracker</h1>
      <h2 className="fw-light text-center mb-5">Stay informed, make smarter purchases</h2>
      <div className="justify-content-center mb-5 row">
        <div className="col-lg-6">
          <InputGroup className="mb-3">
            <InputGroup.Text id="basic-addon1">🔎︎</InputGroup.Text>
            <Form.Control
              placeholder="Search a app"
              aria-label="search"
              aria-describedby="basic-addon1"
              size="lg"
            />
          </InputGroup>
        </div>
      </div>
    </div>
  );
}
