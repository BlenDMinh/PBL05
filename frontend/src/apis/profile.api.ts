import { Conversation, PairChatResponse } from 'src/types/chat.type'
import { GameHistoryResponse } from 'src/types/game.type'
import { User } from 'src/types/users.type'
import http from 'src/utils/http'

export const URL_PROFILE = '/profile/info/'
export const URL_PROFILE_GAME_HISTORY = '/profile/games-of/'

const profileApi = {
    getProfile(id: any) {
        return http.get<User>(`${URL_PROFILE}${id}`)
    },
    getGameHistory(id: any, page: number, size: number = 8) {
        return http.get<GameHistoryResponse>(`${URL_PROFILE_GAME_HISTORY}${id}?page=${page}&size=${size}`)
    }
}

export default profileApi