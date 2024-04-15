import { Dispatch, ReactNode, SetStateAction, useEffect, useState } from "react";
import { createContext, useContextSelector } from "use-context-selector";

import { AuthenticatedUser } from "@/types/user";

type AppContextValue = {
  user: AuthenticatedUser | null;
  setUser: Dispatch<SetStateAction<AuthenticatedUser | null>>;
};

type StateProviderProps = {
  readonly children: ReactNode;
};

const AppContext = createContext<AppContextValue | null>(null);

export function StateProvider({ children }: StateProviderProps) {
  const [user, setUser] = useState<AuthenticatedUser | null>(() => {
    const userFromStorage = localStorage.getItem("user");
    return userFromStorage ? JSON.parse(userFromStorage) : null;
  });

  useEffect(() => {
    if (user) {
      localStorage.setItem("user", JSON.stringify(user));
    } else {
      localStorage.removeItem("user");
    }
  }, [user]);

  return <AppContext.Provider value={{ user, setUser }}>{children}</AppContext.Provider>;
}

export function useAppContext() {
  return {
    user: useContextSelector(AppContext, value => value!.user),
    setUser: useContextSelector(AppContext, value => value!.setUser)
  };
}
