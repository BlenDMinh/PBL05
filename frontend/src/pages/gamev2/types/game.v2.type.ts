import { Key } from "chessground/types"

export type GameV2SocketData = {
    fen: string,
    white: boolean,
    gamePlayer?: {
        id: number,
        displayName: string,
        white: boolean
    },
    resignSide?: boolean,
    moveHistories?: MoveHistory[]
}

export type MoveHistory = {
    from: Key,
    to: Key,
    promotiom: string,
    piece: string
}