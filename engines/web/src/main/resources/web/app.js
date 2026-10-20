const app = document.getElementById("app");
const toasts = document.getElementById("toasts");
const socket = new WebSocket((location.protocol === "https:" ? "wss://" : "ws://") + location.host + "/ws");

const GAME_ICONS = { TicTacToe: "❌⭕" };

let cells = [];
let gameChat = null;
let pickedGame = null;
// picks: 1-based position in lobbyOrder -> team number
const picks = new Map();

// ---------- Helpers ----------
function send(message) {
    socket.send(JSON.stringify(message));
}

function el(tag, props = {}, ...children) {
    const e = document.createElement(tag);
    Object.assign(e, props);
    e.append(...children.filter((c) => c != null && c !== false));
    return e;
}

const STORAGE_KEY = "grindstone.profile";

function loadProfile() {
    try {
        return JSON.parse(localStorage.getItem(STORAGE_KEY)) ?? {};
    } catch {
        return {};
    }
}

function saveProfile(name, emoji) {
    try {
        localStorage.setItem(STORAGE_KEY, JSON.stringify({ name, emoji }));
    } catch {
        // storage unavailable, ignore
    }
}

const saved = loadProfile();
let chosenEmoji = saved.emoji ?? null;
let draftName = saved.name ?? "";

function emojiBadge(emoji) {
    return el("span", { className: "emoji" }, emoji);
}

function toast(text) {
    const t = el("div", { className: "toast" }, text);
    toasts.append(t);
    setTimeout(() => t.remove(), 3500);
}

function panel(title, count, ...children) {
    return el("section", { className: "panel" },
        el("h2", {}, title, count != null ? el("span", { className: "count" }, String(count)) : null),
        ...children);
}

function logo() {
    return el("div", { className: "logo" },
        logoMark(),
        el("div", { className: "logo-text" }, "Grindstone", el("small", {}, "Playground")));
}

function logoMark() {
    return el("img", { className: "logo-mark", src: "favicon.ico", alt: "" });
}

// ---------- Views ----------
function renderNickname(msg) {
    const input = el("input", { className: "input", placeholder: "Your nickname", maxLength: 16, value: draftName });
    input.oninput = () => (draftName = input.value);
    const join = () => {
        const name = input.value.trim();
        saveProfile(name, chosenEmoji);
        // send "profile" (not "nickname") per new protocol
        send({ type: "profile", name, emoji: chosenEmoji });
    };
    input.onkeydown = (e) => {
        if (e.key === "Enter") join();
    };

    const grid = el("div", { className: "emoji-grid" });
    msg.emojis.forEach((emoji) => {
        grid.append(el("button", {
            type: "button",
            className: "emoji-option" + (emoji === chosenEmoji ? " selected" : ""),
            onclick: () => {
                chosenEmoji = chosenEmoji === emoji ? null : emoji;
                [...grid.children].forEach((c) => c.classList.toggle("selected", c.textContent === chosenEmoji));
            },
        }, emoji));
    });

    app.replaceChildren(
        el("div", { className: "center" },
            el("div", { className: "hero" },
                logoMark(),
                el("h1", {}, "Grindstone Playground"),
                el("p", {}, "Pick a nickname to jump in"),
                input,
                el("div", { className: "label" }, "Choose an emoji (or get a random one)"),
                grid,
                el("div", { className: "error" }, msg.error ?? ""),
                el("button", { className: "btn", onclick: join }, "Join"))));
    input.focus();
}

function teamsView(teams) {
    const wrap = el("div", { className: "run-teams" });
    teams.forEach((team, i) => {
        if (i > 0) wrap.append(el("span", { className: "vs" }, "VS"));
        wrap.append(el("span", { className: "pill t" + ((i % 4) + 1) }, team.join(", ")));
    });
    return wrap;
}

function renderLobby(msg) {
    cells = [];
    gameChat = null;
    if (!msg.games.includes(pickedGame)) pickedGame = msg.games[0] ?? null;

    // picks keys are 1-based positions; remove stale entries for players now in a game
    const freePosSet = new Set(
        msg.online
            .map((p, i) => ({ p, pos: i + 1 }))
            .filter(({ p }) => !p.game)
            .map(({ pos }) => pos)
    );
    for (const pos of [...picks.keys()]) {
        if (!freePosSet.has(pos)) picks.delete(pos);
    }

    const me = msg.online.find((p) => p.you);

    // Games
    const gamesPanel = panel("Games", msg.games.length,
        ...msg.games.map((g) =>
            el("button", {
                className: "game-card" + (g === pickedGame ? " selected" : ""),
                onclick: () => {
                    pickedGame = g;
                    renderLobby(msg);
                },
            }, g)));

    // Running
    const runningPanel = panel("Running", msg.running.length,
        ...(msg.running.length
            ? msg.running.map((g) =>
                el("div", { className: "run" },
                    el("div", { className: "run-head" }, el("span", { className: "badge" }, "#" + g.id), g.game),
                    teamsView(g.teams)))
            : [el("div", { className: "empty" }, "No games running right now")]));

    // Online — position is 1-based index shown next to player name
    const rows = msg.online.map((p, i) => {
        const pos = i + 1; // 1-based position used as the pick key
        const busy = !!p.game;
        const right = busy
            ? el("span", { className: "badge" }, "In game #" + p.game)
            : el("div", { className: "chips" },
                ...[1, 2, 3, 4].map((n) =>
                    el("button", {
                        className: `chip t${n}` + (picks.get(pos) === n ? " active" : ""),
                        title: "Team " + n,
                        onclick: () => {
                            if (picks.get(pos) === n) picks.delete(pos);
                            else picks.set(pos, n);
                            renderLobby(msg);
                        },
                    }, String(n))));

        return el("div", { className: "player" + (busy ? " busy" : "") },
            el("span", { className: "pos" }, String(pos)),
            emojiBadge(p.emoji),
            el("span", { className: "pname" }, p.name, p.you ? el("span", { className: "you" }, "you") : null),
            right);
    });

    // Start bar — teams sent as lists of 1-based positions
    const byTeam = new Map();
    picks.forEach((team, pos) => {
        if (!byTeam.has(team)) byTeam.set(team, []);
        byTeam.get(team).push(pos - 1);
    });
    const keys = [...byTeam.keys()].sort((a, b) => a - b);
    const nameOf = (pos) => msg.online[pos - 1]?.name ?? "?";
    const summary = keys.length
        ? keys.map((k) => `Team ${k}: ${byTeam.get(k).map(nameOf).join(", ")}`).join("  ·  ")
        : "Pick a team number for each player to get started.";

    const start = el("button", {
        className: "btn",
        disabled: !keys.length || !pickedGame,
        // teams: array of arrays of 1-based positions (indexes)
        onclick: () => send({ type: "start", game: pickedGame, teams: keys.map((k) => byTeam.get(k)) }),
    }, pickedGame ? "Start " + pickedGame : "Start");

    // Rejoin button shown when the server says canRejoin
    const rejoinBtn = msg.canRejoin
        ? el("button", {
            className: "btn ghost",
            onclick: () => send({ type: "rejoin" }),
          }, "Rejoin game")
        : null;

    const playersPanel = panel("Players online", msg.online.length,
        el("p", { className: "hint" }, "Players on the same team share a number."),
        ...rows,
        el("div", { className: "startbar" },
            el("div", { className: "summary" }, summary),
            rejoinBtn,
            start));

    app.replaceChildren(
        el("div", { className: "shell" },
            el("div", { className: "topbar" },
                logo(),
                me ? el("div", { className: "user-chip" }, emojiBadge(me.emoji), me.name) : null),
            el("div", { className: "grid" },
                el("div", { className: "stack" }, gamesPanel, runningPanel),
                playersPanel)));
}

function renderGame(msg) {
    const board = el("div", { className: "board" });
    cells = [];
    for (let r = 0; r < 3; r++) {
        for (let c = 0; c < 3; c++) {
            const cell = el("button", { className: "cell", onclick: () => send({ type: "play", row: r, col: c }) });
            cells.push(cell);
            board.append(cell);
        }
    }

    gameChat = el("div", { className: "chat" });
    app.replaceChildren(
        el("div", { className: "game-wrap" },
            el("div", { className: "game-head" },
                el("h1", {}, `#${msg.gameId} ${msg.game}`),
                el("button", { className: "btn ghost", onclick: () => send({ type: "leave" }) }, "Leave")),
            board,
            gameChat));
}

function renderBoard(msg) {
    if (!cells.length) return;
    msg.board.forEach((row, r) => row.forEach((v, c) => {
        cells[r * 3 + c].textContent = v === 0 ? "X" : v === 1 ? "O" : "";
        cells[r * 3 + c].className = "cell"
            + (v === 0 ? " x filled" : v === 1 ? " o filled" : "")
            + (r === msg.row && c === msg.col ? " last" : "");
    }));
}

function renderChat(msg) {
    if (!gameChat) return;
    gameChat.append(el("div", { className: "line" }, msg.text));
    gameChat.scrollTop = gameChat.scrollHeight;
}

// ---------- Socket ----------
const handlers = {
    nickname: renderNickname,
    lobby: renderLobby,
    game: renderGame,
    board: renderBoard,
    chat: renderChat,
    error: (msg) => toast(msg.text),
};

socket.onmessage = (event) => {
    const msg = JSON.parse(event.data);
    (handlers[msg.type] ?? console.log)(msg);
};

socket.onclose = () => {
    app.replaceChildren(
        el("div", { className: "center" },
            el("div", { className: "hero" },
                el("h1", {}, "Disconnected"),
                el("p", {}, "The connection to the server was lost."),
                el("button", { className: "btn", onclick: () => location.reload() }, "Reconnect"))));
};
