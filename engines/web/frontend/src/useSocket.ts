import { useRef, useState } from "react";
import type { ClientMessage, ServerMessage } from "./types";

export function useSocket(onMessage: (msg: ServerMessage) => void) {
  const ws = useRef<WebSocket | null>(null);

  function connect(name: string, emoji: string) {
    onMessage({ type: "login", error: null, isConnecting: true });
    const url = `${import.meta.env.VITE_WS_URL}?name=${encodeURIComponent(name)}&emoji=${encodeURIComponent(emoji)}`;
    const socket = new WebSocket(url);
    ws.current = socket;

    socket.onmessage = (e) => {
      onMessage(JSON.parse(e.data));
    };

    socket.onclose = (e) => {
      if (e.code === 4000) onMessage({ type: "login", error: e.reason, isConnecting: false });
      else onMessage({ type: "disconnected" });
    };
  }

  function send(msg: ClientMessage) {
    ws.current?.send(JSON.stringify(msg));
  }

  return { connect, send };
}