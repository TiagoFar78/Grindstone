import { useState } from "react";
import { useSocket } from "./useSocket";
import CreateProfile from "./pages/CreateProfile";
import Disconnected from "./pages/Disconnected";
import GameScreen from "./pages/GameScreen";
import Lobby from "./pages/Lobby";
import type { Toast } from "./components/Toasts";
import type { ErrorMsg, ServerMessage } from "./types";

export default function App() {
  const [serverMsg, setServerMsg] = useState<Exclude<ServerMessage, ErrorMsg>>(
    { type: "login", error: null, isConnecting: false }
  );

  function handleMessage(msg: ServerMessage) {
    if (msg.type === "error") addToast(msg.text);
    else setServerMsg(msg);
  }
  const { connect, send } = useSocket(handleMessage);
  
  const [toasts, setToasts] = useState<Toast[]>([]);
  function addToast(text: string) {
    const id = Date.now() + Math.random();
    setToasts((t) => [...t, { id, text }]);
    setTimeout(() => setToasts((t) => t.filter((x) => x.id !== id)), 4000);
  }

  function page() {
    switch (serverMsg.type) {
      case "login":
        return <CreateProfile onJoin={connect} error={serverMsg.error} isConnecting={serverMsg.isConnecting} />;

      case "lobby":
        return <Lobby msg={serverMsg} send={send} />;

      case "gameUpdate":
        return <GameScreen msg={serverMsg} send={send} />;

      case "disconnected":
        return <Disconnected />;

      default:
        console.error("Error id 2958934724 -> Unexpected screen");
        return <p>Unexpected state. Error id: 2958934724</p>;
    }
  }

  return (
    <>
      {page()}
      {toasts.map((t) => (
        <p key={t.id} style={{ color: "tomato" }}>{t.text}</p>
      ))}
    </>
  );
}