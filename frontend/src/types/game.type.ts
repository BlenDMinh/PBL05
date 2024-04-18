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