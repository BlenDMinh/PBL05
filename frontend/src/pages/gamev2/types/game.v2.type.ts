import { Key } from "chessground/types"

export type GameV2SocketData = {
    fen: string,
    white: boolean,
    gamePlayer?: {
        id?: number,
        displayName?: string,
        difficulty?: number,
        white: boolean
    },
    resignSide?: boolean,
    moveHistories?: MoveHistory[]
}

export type MoveHistory = {
    from: Key,
    to: Key,
    promotion: string,
    piece: string
}