export type Conversation = {
    id: number,
    displayName: string,
    online: boolean,
    avatarUrl: string,
    lastChatTime: string
}

export type ReceiveMessage = {
        id: number,
        content: string,
        senderId: number,
        receiverId: number,
        sendedAt: string
}