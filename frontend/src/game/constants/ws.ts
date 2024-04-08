export const wsHost = "ws://localhost:8080/chess-backend"

export const ws = {
    findOpponent: wsHost + "/find-opponent",
    game: (id: number) => id >= 0 ? wsHost + `/game-player/${id}` : null
}