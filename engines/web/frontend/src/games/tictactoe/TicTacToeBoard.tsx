import type { GameViewProps } from "../../types";

type State = {
    board: number[][];
    winner: string | null;
    playerPlaying: string;
    isYourTurn: boolean;
};

const EMPTY = -1;
const SYMBOLS = ["X", "O"];

export default function TicTacToeBoard({ update, send }: GameViewProps) {
    const s = update as State;
    const gameOver = s.winner !== null;

    const status = gameOver
        ? (s.winner === "DRAW" ? "Draw!" : `${s.winner} wins!`)
        : `${s.playerPlaying}'s turn`;

    return (
        <>
            <p>{status}</p>
            <div className="board">
                {s.board.map((row, r) =>
                    row.map((v, c) => (
                        <button
                            key={`${r}-${c}`}
                            className={"cell" + (v !== EMPTY ? ` ${v === 0 ? "x" : "o"} filled` : "")}
                            disabled={gameOver || !s.isYourTurn || v !== EMPTY}
                            onClick={() => send({ type: "play", row: r, col: c })}
                        >
                            {SYMBOLS[v] ?? ""}
                        </button>
                    ))
                )}
            </div>
        </>
    );
}