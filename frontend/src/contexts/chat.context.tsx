import { createContext, useContext, useEffect, useMemo, useState } from 'react'
import useWebSocket, { ReadyState } from 'react-use-websocket'
import chatApi from 'src/apis/chat.api'
import { ws } from 'src/constants/ws'
import { ReactWithChild } from 'src/interface/app'
import { Conversation, Message } from 'src/types/chat.type'
import { AppContext } from './app.context'

export interface ChatContextType {
  startChat: (sessionId: string) => void
  conversations: Conversation[]
  state: ReadyState
  sendMessage: (content: string, receiverId: string) => void
  onMessage: (senderId: string, callback: (message: Message) => void) => void
}

const initContext: ChatContextType = {
  startChat: () => null,
  conversations: [],
  state: ReadyState.CONNECTING,
  sendMessage: () => null,
  onMessage: () => null
}

export const ChatContext = createContext<ChatContextType>(initContext)

export const ChatContextProvider = ({ children }: ReactWithChild) => {
  const [sessionId, setSessionId] = useState('')
  const [conversations, setConversations] = useState<Conversation[]>([])
  const wsUrl = useMemo(() => ws.chat(sessionId), [sessionId])
  const chatSocket = useWebSocket(wsUrl)
  const state = chatSocket.readyState
  const callbackMap = useMemo(() => new Map<string, ((message: Message) => void)[]>(), [])
  const app = useContext(AppContext)

  const startChat = (sessionId: string) => {
    setSessionId(sessionId)
    chatApi.getConversations().then((res) => {
      setConversations(res.data as Conversation[])
    })
  }

  const sendMessage = (content: string, receiverId: string) => {
    chatSocket.sendJsonMessage({
      content: content,
      senderId: app.user?.id,
      receiverId: receiverId
    })
  }

  const onMessage = (senderId: string, callback: (message: Message) => void) => {
    if (callbackMap.has(senderId)) {
      const callbackList = callbackMap.get(senderId)
      if (callbackList!.indexOf(callback) == -1) {
        callbackList?.push(callback)
      }
    } else {
      callbackMap.set(senderId, [callback])
    }
  }

  useEffect(() => {
    const json = chatSocket.lastJsonMessage as Message
    if (!json) return
    json.isFromUser = json.senderId == app.user?.id
    const callbackList1 = callbackMap.get(json.senderId.toString())
    callbackList1?.forEach((callback) => callback.call(this, json))

    const callbackList2 = callbackMap.get(json.receiverId.toString())
    callbackList2?.forEach((callback) => callback.call(this, json))

    const conveId = conversations.findIndex((m) => m.id == json.senderId || m.id == json.receiverId)
    if (conveId != -1) {
      const conversation = conversations[conveId]
      const newConversations = [...conversations]
      newConversations.splice(conveId, 1)
      conversation.message = json
      newConversations.unshift(conversation)
      setConversations(newConversations)
    }
  }, [chatSocket.lastJsonMessage])

  // useEffect(() => {
  //     if(chatSocket.readyState == ReadyState.OPEN) {
  //         sendMessage("Hi", "100")
  //     }
  // }, [chatSocket.readyState])

  return (
    <ChatContext.Provider
      value={{
        startChat,
        conversations,
        state,
        sendMessage,
        onMessage
      }}
    >
      {children}
    </ChatContext.Provider>
  )
}

export const useChat = () => {
  const context = useContext(ChatContext)

  if (context === undefined) {
    throw new Error('useChat must be used within GameProvider')
  }

  return context
}
