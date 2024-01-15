import "./App.css";

import { BrowserRouter, Navigate, Route, Routes } from "react-router-dom";

import { Home } from "@/pages/Home";

import { Footer } from "./components/Footer";
import { NavBar } from "./components/NavBar";

export function App() {
  return (
    <>
      <NavBar />
      <BrowserRouter>
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="*" element={<Navigate to="/" />} />
        </Routes>
      </BrowserRouter>
      <Footer />
    </>
  );
}
