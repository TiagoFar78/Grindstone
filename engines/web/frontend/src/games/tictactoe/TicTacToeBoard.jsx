const SYMBOLS = { 0: "X", 1: "O" };

export default function TicTacToeBoard({ board, send }) {
    const cells = [];
    for (let r = 0; r < 3; r++) {
        for (let c = 0; c < 3; c++) {
            const v = board?.board[r][c];
            const cls =
                "cell" +
                (v === 0 ? " x filled" : v === 1 ? " o filled" : "") +
                (board && r === board.row && c === board.col ? " last" : "");
            cells.push(
                <button key={`${r}-${c}`} className={cls} onClick={() => send({ type: "play", row: r, col: c })}>
                    {SYMBOLS[v] ?? ""}
                </button>
            );
        }
    }
    return <div className="board">{cells}</div>;
}
