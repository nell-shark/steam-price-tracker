import { JwtPayload as Payload } from "jwt-decode";

export type JwtPayload = Payload & {
  userId: number;
  role: "ROLE_USER" | "ROLE_ADMIN";
};
