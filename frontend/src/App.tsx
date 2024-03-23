import "./App.css";

import { Container } from "react-bootstrap";
import { BrowserRouter, Navigate, Route, Routes } from "react-router-dom";

import { Footer } from "@/components/Footer";
import { NavBar } from "@/components/NavBar";
import { Game } from "@/pages/Game";
import { Home } from "@/pages/Home";

export function App() {
  return (
    <BrowserRouter>
      <NavBar />
      <Container>
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/apps/:id" element={<Game />} />
          <Route path="*" element={<Navigate to="/" />} />
        </Routes>
        <Footer />
      </Container>
    </BrowserRouter>
  );
}
