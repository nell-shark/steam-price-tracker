import Button from "react-bootstrap/Button";
import Container from "react-bootstrap/Container";
import Nav from "react-bootstrap/Nav";
import Navbar from "react-bootstrap/Navbar";
import { Link, useNavigate } from "react-router-dom";

import { useAppContext } from "@/contexts/AppContext";
import { GITHUB_REPO_URL } from "@/data/constants";
import { userService } from "@/services/userService";

import styles from "./Navbar.module.css";

export function NavBar() {
  const navigate = useNavigate();
  const { user, setUser } = useAppContext();

  async function handleClick() {
    if (user) {
      setUser(() => null);
      await userService.logout();
    }

    navigate("/login");
  }

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
            {user && (
              <Link to={`/users/${user.id}`} className="nav-link">
                Favorites
              </Link>
            )}
          </Nav>
          <Button onClick={() => handleClick()} variant="outline-warning">
            {user ? "Logout" : "Login"}
          </Button>
        </Navbar.Collapse>
      </Container>
    </Navbar>
  );
}
