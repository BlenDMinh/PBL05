import { Conversation } from 'src/types/chat.type'
import http from 'src/utils/http'

export const URL_CHATTING = '/chatting'

const chatApi = {
    getConversations() {
        return http.get<Conversation[]>(URL_CHATTING)
    }
}

export default chatApi