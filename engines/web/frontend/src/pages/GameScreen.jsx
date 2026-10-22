import { useEffect, useRef } from "react";
import { gameViews } from "../games";

export default function GameScreen({ msg, board, chat, send }) {
    const chatRef = useRef(null);
    const GameView = gameViews[msg.game];

    useEffect(() => {
        if (chatRef.current) chatRef.current.scrollTop = chatRef.current.scrollHeight;
    }, [chat]);

    return (
        <div className="game-wrap">
            <div className="game-head">
                <h1>#{msg.gameId} {msg.game}</h1>
                <button className="btn ghost" onClick={() => send({ type: "leave" })}>Leave</button>
            </div>

            {GameView ? <GameView board={board} send={send} /> : <p>Unknown game: {msg.game}</p>}

            <div className="chat" ref={chatRef}>
                {chat.map((text, i) => <div key={i} className="line">{text}</div>)}
            </div>
        </div>
    );
}
