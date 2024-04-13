export const wsHost = import.meta.env.VITE_WS_URL

export const ws = {
    findOpponent: wsHost + "/find-opponent",
    game: (id: string) => id ? (wsHost + `/game-player/${id}`) : null,
    pvpgamev2: (id: string) => id ? (wsHost + `/v2/game-player/${id}`) : null,
    botgamev2: (id: string) => id ? (wsHost + `/v2/game-bot/${id}`) : null,
    chat: (sessionId: string) => sessionId ? (wsHost + `/chat?sid=${sessionId}`) : null
}