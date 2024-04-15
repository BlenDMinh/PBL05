import { Conversation, PairChatResponse } from 'src/types/chat.type'
import { User } from 'src/types/users.type'
import http from 'src/utils/http'

export const URL_PROFILE = '/profile/player/'
export const URL_CHATTING_PAIR = '/chatting/pair/'

const profileApi = {
    getProfile(id: any) {
        return http.get<User>(`${URL_PROFILE}${id}`)
    }
}

export default profileApi