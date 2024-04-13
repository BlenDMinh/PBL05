import { createContext, useContext, useEffect, useMemo, useState } from "react"
import useWebSocket, { ReadyState } from "react-use-websocket"
import chatApi from "src/apis/chat.api"
import { ws } from "src/constants/ws"
import { ReactWithChild } from "src/interface/app"
import { Conversation, ReceiveMessage } from "src/types/chat.type"
import { getProfileFromLS } from "src/utils/auth"


export interface ChatContextType {
    startChat: (sessionId: string) => void,
    conversations: Conversation[],
    state: ReadyState,
    sendMessage: (content: string, receiverId: string) => void,
    onReceiveMessage: (senderId: string, callback: (message: ReceiveMessage) => void) => void
}

const initContext: ChatContextType = {
    startChat: () => null,
    conversations: [],
    state: ReadyState.CONNECTING,
    sendMessage: () => null,
    onReceiveMessage: () => null
}

export const ChatContext = createContext<ChatContextType>(initContext)

export const ChatContextProvider = ({children}: ReactWithChild) => {
    const [sessionId, setSessionId] = useState("")
    const [conversations, setConversations] = useState<Conversation[]>([])
    const wsUrl = useMemo(() => ws.chat(sessionId), [sessionId])
    const chatSocket = useWebSocket(wsUrl)
    const state = chatSocket.readyState
    const [callbackMap, setCallbackMap] = useState(new Map<string, (message: ReceiveMessage) => void>())
    
    const startChat = (sessionId: string) => {
        setSessionId(sessionId)
        chatApi.getConversations().then(res => {
            setConversations(res.data as Conversation[])
        })
    }

    const sendMessage = (content: string, receiverId: string) => {
        chatSocket.sendJsonMessage(
            {
                content: content,    
                senderId: getProfileFromLS().id,
                receiverId: receiverId
            }
        )
    }

    const onReceiveMessage = (senderId: string, callback: (message: ReceiveMessage) => void) => {
        callbackMap.set(senderId, callback)
    }

    useEffect(() => {
        console.log(chatSocket.lastJsonMessage)
        const json = chatSocket.lastJsonMessage as ReceiveMessage
        if(!json)
            return
        const callback = callbackMap.get(json.senderId.toString())
        callback?.call(this, json)
    }, [chatSocket.lastJsonMessage])

    // useEffect(() => {
    //     if(chatSocket.readyState == ReadyState.OPEN) {
    //         sendMessage("Hi", "100")
    //     }
    // }, [chatSocket.readyState])

    return <ChatContext.Provider value={{
        startChat,
        conversations,
        state,
        sendMessage,
        onReceiveMessage
    }}>
        {children}
    </ChatContext.Provider>
}

export const useChat = () => {
    const context = useContext(ChatContext)
  
    if (context === undefined) {
      throw new Error('useChat must be used within GameProvider')
    }
  
    return context
  }
  