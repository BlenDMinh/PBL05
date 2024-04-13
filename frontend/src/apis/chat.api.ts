import { Conversation, PairChatResponse } from 'src/types/chat.type'
import http from 'src/utils/http'

export const URL_CHATTING = '/chatting'
export const URL_CHATTING_PAIR = '/chatting/pair/'

const chatApi = {
    getConversations() {
        return http.get<Conversation[]>(URL_CHATTING)
    },
    async getConversationChat(userId: string, page: number, size: number) {
        if(!userId)
            return []
        return http.get<PairChatResponse>(URL_CHATTING_PAIR + userId + `?page=${page}&size=${size}`)
    },
}

export default chatApi