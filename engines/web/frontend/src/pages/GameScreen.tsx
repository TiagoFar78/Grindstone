import { useEffect, useRef } from "react";
import { gameViews } from "../games/games";
import type { ClientMessage, GameMsg } from "../types";

type Props = {
    msg: GameMsg;
    send: (m: ClientMessage) => void;
};

export default function GameScreen({ msg, send }: Props) {
    const GameView = gameViews[msg.game];

    return (
        <div className="game-wrap">
            <div className="game-head">
                <h1>{msg.game}</h1>
                <button className="btn ghost" onClick={() => send({ type: "leave" })}>Leave</button>
            </div>

            {GameView ? <GameView update={msg.update} send={send} /> : <p>Unknown game: {msg.game}</p>}
        </div>
    );
}