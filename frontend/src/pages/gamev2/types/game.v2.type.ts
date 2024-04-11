export type GameV2SocketData = {
    fen: string,
    white: boolean,
    gamePlayer?: {
        id: number,
        displayName: string,
        white: boolean
    }
}