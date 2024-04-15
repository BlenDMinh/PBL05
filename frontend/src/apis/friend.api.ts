import { FriendListResponse, FriendRequestResponse } from "src/types/users.type"
import http from "src/utils/http"

export const URL_FRIEND_OF_USER = '/friend/player/{user_id}?page={page}&size={size}'
export const URL_FRIEND_REQUEST = '/friend/requests'

const friendApi = {
    getFriendList(user_id: string, page: number, size: number, keyword: string = "") {
        const url = URL_FRIEND_OF_USER
            .replace('{user_id}', user_id)
            .replace('{page}', page.toString())
            .replace('{size}', size.toString())
        return http.get<FriendListResponse>(url + (keyword ? `&keyword=${keyword}` : ''))
    },
    getFriendRequestList(page: number, size: number) {
        const url = URL_FRIEND_REQUEST + `?page=${page}&size=${size}`
        return http.get<FriendRequestResponse>(url)
    },
    replyFriendRequest(senderId: number, accept: boolean) {
        return http.put(URL_FRIEND_REQUEST, {
            senderId: senderId,
            accept: accept
        })
    }
}

export default friendApi