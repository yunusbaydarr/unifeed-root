type Listener = (accessToken: string | null) => void;

let token: string | null = null;
const listeners = new Set<Listener>();

export const authSession = {
  getAccessToken: () => token,
  setAccessToken: (accessToken: string | null) => {
    if (token === accessToken) return;
    token = accessToken;
    listeners.forEach((listener) => listener(token));
  },
  subscribe: (listener: Listener) => {
    listeners.add(listener);
    return () => listeners.delete(listener);
  },
};
