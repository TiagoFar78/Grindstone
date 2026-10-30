import { useRef, useState } from "react";

export function useSocket() {
  const ws = useRef(null);
  const [status, setStatus] = useState("idle"); // idle | connecting | open | closed
  const [loginError, setLoginError] = useState(null);
  const [view, setView] = useState(null); // last lobby / gameStarted message
  const [toasts, setToasts] = useState([]);

  function addToast(text) {
    const id = Date.now() + Math.random();
    setToasts((t) => [...t, { id, text }]);
    setTimeout(() => setToasts((t) => t.filter((x) => x.id !== id)), 4000);
  }

  function connect(name, emoji) {
    setLoginError(null);
    setStatus("connecting");
    const url = `${import.meta.env.VITE_WS_URL}?name=${encodeURIComponent(name)}&emoji=${encodeURIComponent(emoji)}`;
    const socket = new WebSocket(url);
    ws.current = socket;

    socket.onopen = () => setStatus("open");
    socket.onmessage = (e) => {
      const msg = JSON.parse(e.data);
      switch (msg.type) {
        case "lobby":
        case "gameStarted":
          setView(msg);
          break;
        case "error":
          addToast(msg.text);
          break;
        default:
          console.log("unhandled:", msg);
      }
    };
    socket.onclose = (e) => {
      if (e.code === 4000) {
        setLoginError(e.reason);
        setStatus("idle");
      } else {
        setStatus("closed");
      }
    };
  }

  function send(msg) {
    ws.current?.send(JSON.stringify(msg));
  }

  return { status, loginError, view, toasts, connect, send };
}
