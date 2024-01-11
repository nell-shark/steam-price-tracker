import { BrowserRouter, Navigate, Route, Routes } from "react-router-dom";

import { GlobalStyle } from "@/App.styled.ts";
import { Home } from "@/pages/Home";

export function App() {
  return (
    <>
      <GlobalStyle />
      <BrowserRouter>
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="*" element={<Navigate to="/" />} />
        </Routes>
      </BrowserRouter>
    </>
  );
}
