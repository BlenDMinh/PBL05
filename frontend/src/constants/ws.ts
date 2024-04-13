export const wsHost = import.meta.env.VITE_WS_URL

export const ws = {
    findOpponent: (sessionId: string) => sessionId ? (wsHost + `/find-opponent?sid=${sessionId}`) : null,
    findBot: (sessionId: string) => sessionId ? (wsHost + `/find-bot?sid=${sessionId}`) : null,
    game: (id: string) => id ? (wsHost + `/game-player/${id}`) : null,
    pvpgamev2: (id: string, sessionId: string) => (id && sessionId) ? (wsHost + `/v2/game-player/${id}?sid=${sessionId}`) : null,
    botgamev2: (id: string, sessionId: string) => (id && sessionId) ? (wsHost + `/v2/game-bot/${id}?sid=${sessionId}`) : null,
    chat: (sessionId: string) => sessionId ? (wsHost + `/chat?sid=${sessionId}`) : null
}