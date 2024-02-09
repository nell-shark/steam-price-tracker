export const BASE_URL: string = import.meta.env.PROD
  ? import.meta.env.VITE_BASE_URL
  : "http://localhost:8080";
