export const wsHost = "ws://localhost:8080/chess-backend"

export const ws = {
    findOpponent: wsHost + "/find-opponent",
    game: (id: string) => id ? wsHost + `/game-player/${id}` : null
}