import { GameLogResponse } from 'src/types/game.type'
import http from 'src/utils/http'

export const URL_GAMELOG = '/game-log/:gameId'

const gameLogApi = {
    getGameLog(gameId: string) {
        return http.get<GameLogResponse>(URL_GAMELOG.replace(':gameId', gameId))
    }
}

export default gameLogApi