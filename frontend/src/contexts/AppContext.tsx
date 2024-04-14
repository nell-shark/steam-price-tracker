import { Dispatch, ReactNode, SetStateAction, useState } from "react";
import { createContext, useContextSelector } from "use-context-selector";

import { User } from "@/types/user";

type AppContextValue = {
  user: User | null;
  setUser: Dispatch<SetStateAction<User | null>>;
};

type StateProviderProps = {
  readonly children: ReactNode;
};

const AppContext = createContext<AppContextValue | null>(null);

export function StateProvider({ children }: StateProviderProps) {
  const userFromStorage = localStorage.getItem("user")
    ? JSON.parse(localStorage.getItem("user")!)
    : null;

  const [user, setUser] = useState<User | null>(userFromStorage);

  return <AppContext.Provider value={{ user, setUser }}>{children}</AppContext.Provider>;
}

export function useAppContext() {
  return {
    user: useContextSelector(AppContext, value => value!.user),
    setUser: useContextSelector(AppContext, value => value!.setUser)
  };
}
