import { FaPaperPlane } from "react-icons/fa";
import { useEffect, useRef, useState } from "react";
import { useParams } from "react-router-dom";
import { useChat } from "src/contexts/chat.context";
import { ReadyState } from "react-use-websocket";
import { getSessionIdFromLS } from "src/utils/auth";
import { ReceiveMessage } from "src/types/chat.type";
import Message from "./Message";

export default function ChatBox() {
    const { id } = useParams()
    const [messages, setMessages] = useState<ReceiveMessage[]>([])
    const messagesRef = useRef<ReceiveMessage[]>()
    const [sendingMessage, setSendingMessage] = useState("")
    const chat = useChat()

    messagesRef.current = messages

    useEffect(() => {
        if(chat.state != ReadyState.OPEN)
            chat.startChat(getSessionIdFromLS())
        else {
            chat.onReceiveMessage(id ?? "", (message) => {
                const newMessages = [...messagesRef.current]
                newMessages.push(message)
                console.log(newMessages)
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
                {messages.map(m => <Message key={m.id} side="left" message={m.content} />)}
            </div>
            <div className="w-full flex p-2 gap-2">
                <input className="input input-bordered input-primary w-full" type="text" value={sendingMessage} onChange={(e) => {
                    setSendingMessage(e.target.value)
                }} />
                <button className="btn btn-circle btn-active btn-primary text-xl text-white"
                    onClick={send}
                >
                    <FaPaperPlane />
                </button>
            </div>
        </div>
    </>
}