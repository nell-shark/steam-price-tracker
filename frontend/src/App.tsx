import "./App.css";

import { BrowserRouter, Navigate, Route, Routes } from "react-router-dom";

import { Home } from "@/pages/Home";

export function App() {
  return (
    <>
      <BrowserRouter>
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="*" element={<Navigate to="/" />} />
        </Routes>
      </BrowserRouter>
    </>
  );
}
