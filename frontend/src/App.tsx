import "./App.css";

import { Container } from "react-bootstrap";
import { BrowserRouter, Navigate, Route, Routes } from "react-router-dom";

import { Footer } from "@/components/Footer";
import { NavBar } from "@/components/NavBar";
import { StateProvider } from "@/contexts/AppContext";
import { App as AppPage } from "@/pages/App";
import { Home } from "@/pages/Home";
import { Login } from "@/pages/Login";
import { Registration } from "@/pages/Registration";
import { User } from "@/pages/User/User";

export function App() {
  return (
    <StateProvider>
      <BrowserRouter>
        <NavBar />
        <Container>
          <Routes>
            <Route path="/" element={<Home />} />
            <Route path="/apps/:id" element={<AppPage />} />
            <Route path="/login" element={<Login />} />
            <Route path="/registration" element={<Registration />} />
            <Route path="/users/:id" element={<User />} />
            <Route path="*" element={<Navigate to="/" />} />
          </Routes>
          <Footer />
        </Container>
      </BrowserRouter>
    </StateProvider>
  );
}
