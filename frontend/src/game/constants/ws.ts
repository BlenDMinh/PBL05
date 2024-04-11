export const wsHost = "ws://192.168.100.171:8080/chess-backend"

export const ws = {
    findOpponent: wsHost + "/find-opponent",
    game: (id: string) => id ? wsHost + `/game-player/${id}` : null,
    gamev2: (id: string) => id ? wsHost + `/v2/game-player/${id}` : null
}