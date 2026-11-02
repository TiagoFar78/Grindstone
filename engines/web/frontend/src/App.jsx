import { useSocket } from "./useSocket";
import CreateProfile from "./pages/CreateProfile";
import Disconnected from "./pages/Disconnected";
import Lobby from "./pages/Lobby";

export default function App() {
  const { status, loginError, view, toasts, connect, send } = useSocket();

  function page() {
    if (status === "selectingName") return <CreateProfile onJoin={connect} error={loginError} />;

    if (status === "open") {
      if (view?.type === "lobby") return <Lobby playgroundName={view.playgroundName} msg={view} send={send} />;
      if (view?.type === "gameStarted") return <p>Game {view.gameId} started (next step).</p>;

      console.error("Error id 4173483926 -> Unexpected state:", status, view);
      return <p>Unexpected state. Error id: 4173483926</p>;
    }
    
    if (status === "closed") return <Disconnected />;

    console.error("Error id 2958934724 -> Unexpected state:", status, view);
    return <p>Unexpected state. Error id: 2958934724</p>;
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
