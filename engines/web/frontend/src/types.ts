// --------------------------------------- Server ---------------------------------------
export type ServerMessage = LoginMsg | LobbyMsg | GameMsg | ErrorMsg | { type: "disconnected" };

// Login
export type LoginMsg = { type: "login", error: string | null, isConnecting: boolean };

// Lobby
export type LobbyMsg = { type: "lobby", name: string; games: string[]; running: RunningGame[]; online: OnlinePlayer[]; canRejoin: boolean };
export type OnlinePlayer = { name: string; emoji: string; you: boolean; gameSessionId: number | null };
export type RunningGame = { id: number; game: string; teams: string[][] };

// Game
export type GameMsg = { type: "gameUpdate"; game: string; update: unknown };
export type GameViewProps = { update: unknown; send: (m: GameCommand) => void };

// Error
export type ErrorMsg = { type: "error"; text: string };

// --------------------------------------- Client ---------------------------------------
export type ClientMessage =
  | { type: "start"; gameIndex: number; teams: number[][] }
  | { type: "rejoin" }
  | { type: "leave" }
  | { type: "editProfile"; emoji: string }
  | GameCommand;

export type GameCommand = { type: string; [key: string]: unknown };
