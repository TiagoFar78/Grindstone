import type { ComponentType } from "react";
import type { GameViewProps } from "../types";
import TicTacToeBoard from "./tictactoe/TicTacToeBoard";

export const gameViews: Record<string, ComponentType<GameViewProps>> = {
    TicTacToe: TicTacToeBoard,
};