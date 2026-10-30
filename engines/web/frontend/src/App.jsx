import { useSocket } from "./useSocket";
import CreateProfile from "./pages/CreateProfile";
import Disconnected from "./pages/Disconnected";
import Lobby from "./pages/Lobby";

export default function App() {
  const { status, loginError, view, toasts, connect, send } = useSocket();

  function page() {
    if (status === "closed") return <p>Disconnected.</p>;
    if (status === "open") {
      if (view?.type === "lobby") return <Lobby msg={view} send={send} />;
      if (view?.type === "gameStarted") return <p>Game {view.gameId} started (next step).</p>;
      return <p>Loading...</p>;
    }
    return <CreateProfile onJoin={connect} error={loginError} connecting={status === "connecting"} />;
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
