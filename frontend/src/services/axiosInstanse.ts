import axios from "axios";

import { BASE_URL } from "@/data/constants.ts";

export const axiosInstance = axios.create({
  baseURL: BASE_URL,
  withCredentials: true,
  headers: {
    "Content-type": "application/json"
  }
});
