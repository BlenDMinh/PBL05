import { FaPaperPlane } from "react-icons/fa";
import { useEffect, useRef, useState } from "react";
import { useParams } from "react-router-dom";
import { useChat } from "src/contexts/chat.context";
import { ReadyState } from "react-use-websocket";
import { getSessionIdFromLS } from "src/utils/auth";
import { Message as MessageType, PairChatResponse } from "src/types/chat.type";
import Message from "./Message";
import { useQuery } from "react-query";
import chatApi from "src/apis/chat.api";
import { AxiosResponse } from "axios";
import InfiniteScroll from "react-infinite-scroll-component";

const fetchChat = async (id: any, page: number) => {
    const res = await chatApi.getConversationChat(id ?? "", page, 32)
    if(!res)
        return []
    const data = (res as AxiosResponse<PairChatResponse, any>).data
    if(page > data.totalPages)
        return []
    const messages = data.messages
    messages.map(m => {
        m.isFromUser = m.senderId != id
    })
    return messages.reverse()
}

function BeginChatting() {
    return <>
        <div className="w-full h-fit flex flex-col items-center">
            <span className="text-base-content">You can now chat with each other</span>
            <span className="text-base-content font-bold">Say hi</span>
        </div>
    </>
}

export interface ChatBoxProps {
    id?: number | string
}

export default function ChatBox(props: ChatBoxProps) {
    const { id } = useParams()
    const [previousId, setPreviousId] = useState<string | undefined>(undefined)
    const [messages, setMessages] = useState<MessageType[]>([])
    const messagesRef = useRef<MessageType[]>()
    const [sendingMessage, setSendingMessage] = useState("")
    const chat = useChat()
    const [page, setPage] = useState(1)
    const [isEnd, setEnd] = useState(false)

    useEffect(() => {
        console.log(id, page)
        if(id != previousId) {
            setPage(1)
            setEnd(false)
            setPreviousId(id)
            if(page == 1) {
                fetchChat(id ?? (props.id?.toString() ?? ""), 1).then((_messages) => {
                    setMessages(_messages)
                })
            } else {
                setMessages([])
            }
        } else {
            fetchChat(id ?? (props.id?.toString() ?? ""), page).then((_messages) => {
                if(_messages.length)
                    setMessages(messages.concat(_messages))
                else
                    setEnd(true)
            })
        }
    }, [id, page])

    useEffect(() => {
        messagesRef.current = messages
    }, [id, messages])

    useEffect(() => {
        if(chat.state != ReadyState.OPEN) {
            console.log("Starting chat")
            chat.startChat(getSessionIdFromLS())
        }
        else {
            chat.onMessage(id ?? (props.id?.toString() ?? ""), (message) => {
                setMessages([message].concat(messagesRef.current as MessageType[]))
            })
        }
    }, [id, chat.state])
    const send = (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault()
        chat.sendMessage(sendingMessage, id ?? (props.id?.toString() ?? ""))
        setSendingMessage("")
    }
    const fetch = () => {
        setPage(page + 1)
    }
    if(!id && !props.id) {
        return <>
            <div className="w-full h-full flex flex-col justify-center items-center">
                <span className="text-base-content font-bold text-xl">Click on a conversation to start chatting</span>
            </div>
        </>
    }
    if(chat.state != ReadyState.OPEN) {
        return <>
            <div className="w-full h-full flex flex-col justify-center items-center">
                <span className="loading loading-spinner loading-lg" />
                <span className="text-base-content font-bold text-xl">Connecting to server...</span>
            </div>
        </>
    }
    return <>
        <div className="w-full h-full flex flex-col">
            <div id="chatBoxContainer" className="flex-1 flex flex-col-reverse overflow-y-auto ">
                <InfiniteScroll
                    className="p-5"
                    dataLength={messages.length}
                    next={fetch}
                    style={{ display: "flex", flexDirection: "column-reverse" }} //To put endMessage and loader to the top.
                    inverse={true}
                    hasMore={!isEnd}
                    loader={<div className="flex w-full justify-center"><span className="loading loading-lg loading-dots"/></div>}
                    endMessage={<BeginChatting />}
                    scrollableTarget='chatBoxContainer'
                >
                    {messages.map(m => <Message key={m.id} side={m.isFromUser ? "right" : "left"} message={m.content} senderId={m.senderId} />)}
                </InfiniteScroll>
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