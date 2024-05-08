import { PaginitionResponse } from "./utils.type"

export type GameSocketMessage = {
    message: string
    data: any
}

export type GameCreatedMessage = {
    message: string
    data: string
}

export type GameResult = {
    id: string
    player1Id: number,
    player2Id: number,
    status : "PLAYER1_WIN" | "PLAYER2_WIN" | "DRAW"
    createdAt: string
}

export interface GameHistoryResponse extends PaginitionResponse {
    games: GameResult[]
}

export type GameRuleset = {
    id: number,
    name: string,
    detail: {
        minute_per_turn: number,
        total_minute_per_player: number,
        turn_around_steps: number,
        turn_around_time_plus: number
    },
    description: {
        title: string
    } | null,
    published: boolean,
    createdAt: string,
}