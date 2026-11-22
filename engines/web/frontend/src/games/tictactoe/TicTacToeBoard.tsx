import { useEffect, useLayoutEffect, useRef, useState } from "react";
import type { GameViewProps } from "../../types";
import "./style.css";

type State = {
    board: number[][];
    winner: string | null;
    playerPlaying: string;
    isYourTurn: boolean;
};

type WinLine = { x1: number; y1: number; x2: number; y2: number; winnerIndex: number };
type Measured = WinLine & { w: number; h: number }; // pixel coords + board size

const EMPTY = -1;
const SYMBOLS = ["X", "O"];

const LINES: [number, number][][] = [
    // rows
    [[0, 0], [0, 1], [0, 2]],
    [[1, 0], [1, 1], [1, 2]],
    [[2, 0], [2, 1], [2, 2]],
    // columns
    [[0, 0], [1, 0], [2, 0]],
    [[0, 1], [1, 1], [2, 1]],
    [[0, 2], [1, 2], [2, 2]],
    // diagonals
    [[0, 0], [1, 1], [2, 2]],
    [[0, 2], [1, 1], [2, 0]],
];

function findWinningLine(board: number[][]): WinLine | null {
    for (const [[r1, c1], [r2, c2], [r3, c3]] of LINES) {
        const v = board[r1][c1];
        if (v !== EMPTY && v === board[r2][c2] && v === board[r3][c3]) {
            // x = column, y = row (SVG coordinates)
            return { x1: c1, y1: r1, x2: c3, y2: r3, winnerIndex: v };
        }
    }
    return null;
}

export default function TicTacToeBoard({ update, send }: GameViewProps) {
    const s = update as State;
    const gameOver = s.winner !== null;
    console.log("winner:", s.winner, "gameOver:", gameOver, "board:", JSON.stringify(s.board), "line:", findWinningLine(s.board));

    const boardRef = useRef<HTMLDivElement>(null);
    const [line, setLine] = useState<Measured | null>(null);

    useLayoutEffect(() => {
        const board = boardRef.current;
        const win = gameOver && board ? findWinningLine(s.board) : null;
        if (!board || !win) {
            setLine(null);
            return;
        }

        const center = (r: number, c: number) => {
            const el = board.children[r * 3 + c] as HTMLElement;
            return { x: el.offsetLeft + el.offsetWidth / 2, y: el.offsetTop + el.offsetHeight / 2 };
        };

        const measure = () => {
            const a = center(win.y1, win.x1);
            const b = center(win.y2, win.x2);

            const cellW = (board.children[0] as HTMLElement).offsetWidth;
            const diagonal = win.x1 !== win.x2 && win.y1 !== win.y2;
            const ext = cellW * (diagonal ? 0.8 : 0.6); // how far past each end cell's center

            const dx = b.x - a.x;
            const dy = b.y - a.y;
            const len = Math.hypot(dx, dy);
            const ux = dx / len;
            const uy = dy / len;

            setLine({
                x1: a.x - ux * ext, y1: a.y - uy * ext,
                x2: b.x + ux * ext, y2: b.y + uy * ext,
                winnerIndex: win.winnerIndex,
                w: board.offsetWidth, h: board.offsetHeight,
            });
        };

        measure();
        const ro = new ResizeObserver(measure);
        ro.observe(board);
        return () => ro.disconnect();
    }, [s.board, gameOver]);

    const status = gameOver
        ? (s.winner === "DRAW" ? "Draw!" : `${s.winner} wins!`)
        : `${s.playerPlaying}'s turn`;

    return (
        <>
            <p>{status}</p>
            <div className="board" ref={boardRef}>
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

                {line && (
                    <svg
                        className={`win-line ${line.winnerIndex === 0 ? "x" : "o"}`}
                        viewBox={`0 0 ${line.w} ${line.h}`}
                    >
                        <line x1={line.x1} y1={line.y1} x2={line.x2} y2={line.y2} pathLength={1} />
                    </svg>
                )}
            </div>
        </>
    );
}