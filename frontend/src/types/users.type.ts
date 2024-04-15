import { PaginitionResponse } from "./utils.type"

export interface User {
  id: number
  displayName: string
  email: string
  status: string
  online: boolean,
  elo: number
  role: string,
  avatarUrl: string
}

export interface Friend extends User {
  friendFrom: string
}

export interface FriendListResponse extends PaginitionResponse {
  friends: Friend[]
}


export type FriendRequest = {
  senderId: number,
  receiverId: number,
  createdAt: string
}
export interface FriendRequestResponse extends PaginitionResponse {
  requests: FriendRequest[]
}