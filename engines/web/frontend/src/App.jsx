import { useSocket } from "./useSocket";
import Login from "./pages/Login";
import Lobby from "./pages/Lobby";
import GameScreen from "./pages/GameScreen";
import Toasts from "./components/Toasts";

export default function App() {
    const { status, view, board, chat, toasts, send } = useSocket();

    let screen;
    if (status === "closed") {
        screen = (
            <div className="center">
                <div className="hero">
                    <h1>Disconnected</h1>
                    <p>The connection to the server was lost.</p>
                    <button className="btn" onClick={() => location.reload()}>Reconnect</button>
                </div>
            </div>
        );
    } else if (!view) {
        screen = <div className="center"><div className="hero"><p>Connecting...</p></div></div>;
    } else if (view.type === "nickname") {
        screen = <Login msg={view} send={send} />;
    } else if (view.type === "lobby") {
        screen = <Lobby msg={view} send={send} />;
    } else if (view.type === "game") {
        screen = <GameScreen msg={view} board={board} chat={chat} send={send} />;
    }

    return (
        <>
            {screen}
            <Toasts toasts={toasts} />
        </>
    );
}
