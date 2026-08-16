import { Client, type IMessage, type StompSubscription } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { authSession } from '@/lib/auth-session';
import { tryRestoreSession } from '@/lib/api/client';
import type { ChatMessage, Notification, SendChatMessage, UUID } from '@/lib/api/types';

export type SocketState = 'inactive' | 'connecting' | 'connected' | 'disconnected';
export type SocketErrorHandler = (error: unknown) => void;
export type SocketStateHandler = (state: SocketState) => void;

interface DesiredSubscription<T> {
  destination: string;
  callback: (message: T) => void;
  active?: StompSubscription;
}

const socketUrl = (): string => {
  const configured = import.meta.env.VITE_WS_URL as string | undefined;
  if (configured) return configured;
  const apiBase = (import.meta.env.VITE_API_BASE_URL as string | undefined)?.replace(/\/api\/v1\/?$/, '');
  const origin = (import.meta.env.VITE_BACKEND_ORIGIN as string | undefined) ?? apiBase ?? (import.meta.env.DEV ? 'http://localhost:8080' : window.location.origin);
  return `${origin}/ws`;
};

const tokenExpiresSoon = (token: string): boolean => {
  try {
    const payloadPart = token.split('.')[1];
    if (!payloadPart) return true;
    const payload = JSON.parse(atob(payloadPart.replace(/-/g, '+').replace(/_/g, '/'))) as { exp?: number };
    return typeof payload.exp !== 'number' || payload.exp * 1000 <= Date.now() + 30_000;
  } catch { return true; }
};

export class SocketManager {
  private client: Client | null = null;
  private sequence = 0;
  private readonly subscriptions = new Map<number, DesiredSubscription<unknown>>();
  private readonly stateHandlers = new Set<SocketStateHandler>();
  private readonly errorHandlers = new Set<SocketErrorHandler>();
  private state: SocketState = 'inactive';
  private readonly stopTokenListener: () => void;

  constructor() {
    this.stopTokenListener = authSession.subscribe((token) => {
      if (token && this.client?.connected) this.publishAuthRefresh(token);
    });
  }

  getState = (): SocketState => this.state;

  onStateChange = (handler: SocketStateHandler): (() => void) => {
    this.stateHandlers.add(handler);
    handler(this.state);
    return () => this.stateHandlers.delete(handler);
  };

  onError = (handler: SocketErrorHandler): (() => void) => {
    this.errorHandlers.add(handler);
    return () => this.errorHandlers.delete(handler);
  };

  connect = (): void => {
    if (this.client?.active) return;
    this.setState('connecting');
    const client = new Client({
      webSocketFactory: () => new SockJS(socketUrl()),
      reconnectDelay: 2_000,
      heartbeatIncoming: 10_000,
      heartbeatOutgoing: 10_000,
      beforeConnect: async () => {
        let token = authSession.getAccessToken();
        if (!token || tokenExpiresSoon(token)) token = (await tryRestoreSession())?.accessToken ?? null;
        if (!token) throw new Error('Socket authentication requires an active session.');
        client.connectHeaders = { Authorization: `Bearer ${token}` };
      },
      onConnect: () => {
        this.setState('connected');
        this.resubscribe();
      },
      onDisconnect: () => this.setState('disconnected'),
      onWebSocketClose: () => this.setState(client.active ? 'connecting' : 'disconnected'),
      onStompError: (frame) => this.emitError(new Error(frame.headers.message ?? frame.body ?? 'STOMP broker error')),
      onWebSocketError: (event) => this.emitError(event),
    });
    this.client = client;
    client.activate();
  };

  disconnect = async (): Promise<void> => {
    const client = this.client;
    this.client = null;
    this.subscriptions.forEach((subscription) => { subscription.active = undefined; });
    if (client) await client.deactivate();
    this.setState('inactive');
  };

  destroy = async (): Promise<void> => {
    this.stopTokenListener();
    this.subscriptions.clear();
    this.stateHandlers.clear();
    this.errorHandlers.clear();
    await this.disconnect();
  };

  subscribeToChat = (threadId: UUID, callback: (message: ChatMessage) => void): (() => void) =>
    this.subscribe(`/topic/chat/${threadId}`, callback);

  subscribeToNotifications = (callback: (notification: Notification) => void): (() => void) =>
    this.subscribe('/user/queue/notifications', callback);

  sendChatMessage = (threadId: UUID, message: SendChatMessage): void => {
    this.assertConnected();
    this.client?.publish({ destination: `/app/chat.send/${threadId}`, body: JSON.stringify(message), headers: { 'content-type': 'application/json' } });
  };

  refreshAuthentication = async (): Promise<void> => {
    const tokens = await tryRestoreSession();
    if (!tokens) throw new Error('Unable to refresh socket authentication.');
    if (this.client?.connected) this.publishAuthRefresh(tokens.accessToken);
  };

  private subscribe<T>(destination: string, callback: (message: T) => void): () => void {
    const id = ++this.sequence;
    const desired: DesiredSubscription<T> = { destination, callback };
    this.subscriptions.set(id, desired as DesiredSubscription<unknown>);
    if (this.client?.connected) desired.active = this.createSubscription(desired);
    return () => {
      const subscription = this.subscriptions.get(id);
      subscription?.active?.unsubscribe();
      this.subscriptions.delete(id);
    };
  }

  private createSubscription<T>(subscription: DesiredSubscription<T>): StompSubscription {
    return this.client!.subscribe(subscription.destination, (message: IMessage) => {
      try { subscription.callback(JSON.parse(message.body) as T); }
      catch (error) { this.emitError(error); }
    });
  }

  private resubscribe(): void {
    this.subscriptions.forEach((subscription) => {
      subscription.active?.unsubscribe();
      subscription.active = this.createSubscription(subscription);
    });
  }

  private publishAuthRefresh(token: string): void {
    this.client?.publish({ destination: '/app/auth.refresh', headers: { Authorization: `Bearer ${token}` } });
  }

  private assertConnected(): void {
    if (!this.client?.connected) throw new Error('Socket is not connected.');
  }

  private setState(state: SocketState): void {
    if (state === this.state) return;
    this.state = state;
    this.stateHandlers.forEach((handler) => handler(state));
  }

  private emitError(error: unknown): void {
    this.errorHandlers.forEach((handler) => handler(error));
  }
}

export const socketManager = new SocketManager();
