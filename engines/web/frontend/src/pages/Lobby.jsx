import { useState } from "react";
import Logo from "../components/Logo";
import Panel from "../components/Panel";
import { emojiOf } from "../emojis";

const inGame = (p) => p.gameSessionId != null && p.gameSessionId >= 0;

function Teams({ teams }) {
    return (
        <div className="run-teams">
            {teams.map((team, i) => (
                <span key={i} style={{ display: "contents" }}>
                    {i > 0 && <span className="vs">VS</span>}
                    <span className={"pill t" + ((i % 4) + 1)}>{team.join(", ")}</span>
                </span>
            ))}
        </div>
    );
}

export default function Lobby({ msg, send }) {
    const [pickedGame, setPickedGame] = useState(null);
    const [rawPicks, setRawPicks] = useState({}); // 1-based position -> team number

    const game = msg.games.includes(pickedGame) ? pickedGame : (msg.games[0] ?? null);

    // ignore picks of players that are now in a game
    const picks = {};
    msg.online.forEach((p, i) => {
        if (!inGame(p) && rawPicks[i + 1]) picks[i + 1] = rawPicks[i + 1];
    });

    const togglePick = (pos, team) =>
        setRawPicks((prev) => {
            const next = { ...prev };
            if (next[pos] === team) delete next[pos];
            else next[pos] = team;
            return next;
        });

    // team number -> list of 0-based indexes
    const byTeam = {};
    Object.entries(picks).forEach(([pos, team]) => {
        (byTeam[team] ??= []).push(Number(pos) - 1);
    });
    const teamKeys = Object.keys(byTeam).map(Number).sort((a, b) => a - b);
    const summary = teamKeys.length
        ? teamKeys.map((k) => `Team ${k}: ${byTeam[k].map((i) => msg.online[i]?.name ?? "?").join(", ")}`).join("  ·  ")
        : "Pick a team number for each player to get started.";

    const me = msg.online.find((p) => p.you);

    return (
        <div className="shell">
            <div className="topbar">
                <Logo />
                {me && (
                    <div className="user-chip">
                        <span className="emoji">{emojiOf(me.emoji)}</span>
                        {me.name}
                    </div>
                )}
            </div>

            <div className="grid">
                <div className="stack">
                    <Panel title="Games" count={msg.games.length}>
                        {msg.games.map((g) => (
                            <button
                                key={g}
                                className={"game-card" + (g === game ? " selected" : "")}
                                onClick={() => setPickedGame(g)}
                            >
                                {g}
                            </button>
                        ))}
                    </Panel>

                    <Panel title="Running" count={msg.running.length}>
                        {msg.running.length === 0 && <div className="empty">No games running right now</div>}
                        {msg.running.map((g) => (
                            <div key={g.id} className="run">
                                <div className="run-head"><span className="badge">#{g.id}</span>{g.game}</div>
                                <Teams teams={g.teams} />
                            </div>
                        ))}
                    </Panel>
                </div>

                <Panel title="Players online" count={msg.online.length}>
                    <p className="hint">Players on the same team share a number.</p>
                    {msg.online.map((p, i) => {
                        const pos = i + 1;
                        const busy = inGame(p);
                        return (
                            <div key={p.name} className={"player" + (busy ? " busy" : "")}>
                                <span className="pos">{pos}</span>
                                <span className="emoji">{emojiOf(p.emoji)}</span>
                                <span className="pname">
                                    {p.name}
                                    {p.you && <span className="you">you</span>}
                                </span>
                                {busy ? (
                                    <span className="badge">In game #{p.gameSessionId}</span>
                                ) : (
                                    <div className="chips">
                                        {[1, 2, 3, 4].map((n) => (
                                            <button
                                                key={n}
                                                className={`chip t${n}` + (picks[pos] === n ? " active" : "")}
                                                title={"Team " + n}
                                                onClick={() => togglePick(pos, n)}
                                            >
                                                {n}
                                            </button>
                                        ))}
                                    </div>
                                )}
                            </div>
                        );
                    })}

                    <div className="startbar">
                        <div className="summary">{summary}</div>
                        {msg.canRejoin && (
                            <button className="btn ghost" onClick={() => send({ type: "rejoin" })}>
                                Rejoin game
                            </button>
                        )}
                        <button
                            className="btn"
                            disabled={!teamKeys.length || !game}
                            onClick={() =>
                                send({
                                    type: "start",
                                    gameIndex: msg.games.indexOf(game),
                                    teams: teamKeys.map((k) => byTeam[k]),
                                })
                            }
                        >
                            {game ? "Start " + game : "Start"}
                        </button>
                    </div>
                </Panel>
            </div>
        </div>
    );
}
