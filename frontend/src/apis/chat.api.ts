import { Conversation, Message } from 'src/types/chat.type'
import http from 'src/utils/http'

export const URL_CHATTING = '/chatting'
export const URL_CHATTING_PAIR = '/chatting/pair/'

const chatApi = {
    getConversations() {
        return http.get<Conversation[]>(URL_CHATTING)
    },
    async getConversationChat(userId: string) {
        if(!userId)
            return []
        return http.get<Message[]>(URL_CHATTING_PAIR + userId)
    },
}

export default chatApi