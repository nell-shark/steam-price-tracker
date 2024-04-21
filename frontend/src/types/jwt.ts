import { JwtPayload as Payload } from "jwt-decode";

export type JwtPayload = Payload & {
  userId: number;
};
