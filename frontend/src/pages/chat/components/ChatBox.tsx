import { FaPaperPlane } from "react-icons/fa";
import { useEffect, useRef, useState } from "react";
import { useParams } from "react-router-dom";
import { useChat } from "src/contexts/chat.context";
import { ReadyState } from "react-use-websocket";
import { getSessionIdFromLS } from "src/utils/auth";
import { Message as MessageType } from "src/types/chat.type";
import Message from "./Message";
import { useQuery } from "react-query";
import chatApi from "src/apis/chat.api";
import { AxiosResponse } from "axios";

const fetchChat = async (id: any) => {
    const res = await chatApi.getConversationChat(id ?? "")
    if(!res)
        return []
    const messages = (res as AxiosResponse<MessageType[], any>).data
    messages.map(m => {
        m.isFromUser = m.senderId != id
    })
    return messages.reverse()
}

export default function ChatBox() {
    const { id } = useParams()
    const [messages, setMessages] = useState<MessageType[]>([])
    const messagesRef = useRef<MessageType[]>()
    const [sendingMessage, setSendingMessage] = useState("")
    const chat = useChat()
    const query = useQuery([`chat/${id}`], () => fetchChat(id))

    useEffect(() => {
        if(query.status == "success") {
            setMessages(query.data)
        }
    }, [query.status])

    messagesRef.current = messages

    useEffect(() => {
        if(chat.state != ReadyState.OPEN)
            chat.startChat(getSessionIdFromLS())
        else {
            chat.onMessage(id ?? "", (message) => {
                const newMessages = [...messagesRef.current as MessageType[]]
                newMessages.unshift(message)
                setMessages(newMessages)
            })
        }
    }, [chat.state])

    const send = () => {
        chat.sendMessage(sendingMessage, id ?? "")
        setSendingMessage("")
    }
    if(!id) {
        return <>
            <div className="w-full h-full flex flex-col justify-center items-center">
                <span className="text-base-content font-bold text-xl">Click on a conversation to start chatting</span>
            </div>
        </>
    }
    return <>
        <div className="w-full h-full flex flex-col">
            <div className="flex-1 flex flex-col-reverse gap-2 overflow-y-auto p-5">
                {messages.map(m => <Message key={m.id} side={m.isFromUser ? "right" : "left"} message={m.content} />)}
            </div>
            <form className="w-full flex p-2 gap-2" onSubmit={send}>
                <input className="input input-bordered input-primary w-full" type="text" value={sendingMessage} onChange={(e) => {
                    setSendingMessage(e.target.value)
                }} />
                <button className="btn btn-circle btn-active btn-primary text-xl text-white" type="submit">
                    <FaPaperPlane />
                </button>
            </form>
        </div>
    </>
}