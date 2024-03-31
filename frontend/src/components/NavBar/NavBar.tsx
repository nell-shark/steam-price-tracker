import Button from "react-bootstrap/Button";
import Container from "react-bootstrap/Container";
import Nav from "react-bootstrap/Nav";
import Navbar from "react-bootstrap/Navbar";
import { Link } from "react-router-dom";

import { GITHUB_REPO_URL } from "@/data/constants";

import styles from "./Navbar.module.css";

export function NavBar() {
  return (
    <Navbar expand="lg" className={styles.navbar} bg="dark" data-bs-theme="dark">
      <Container>
        <Link className="navbar-brand" to="/">
          SPT
        </Link>
        <Navbar.Toggle aria-controls="navbarScroll" />
        <Navbar.Collapse id="navbarScroll" className="justify-content-between">
          <Nav className="my-2 my-lg-0" navbarScroll>
            <Link to="/" className="nav-link">
              Home
            </Link>
            <a href={GITHUB_REPO_URL} className="nav-link" target="_blank" rel="noreferrer">
              Github
            </a>
          </Nav>
          <Button href="/login" variant="outline-warning">
            Login
          </Button>
        </Navbar.Collapse>
      </Container>
    </Navbar>
  );
}
