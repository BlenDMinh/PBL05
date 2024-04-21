import { useEffect, useRef, useState } from "react";
import { FaComments, FaFontAwesome } from "react-icons/fa";
import { ChatContextProvider, useChat } from "src/contexts/chat.context";
import ChatBox from "src/pages/chat/components/ChatBox";
import { Message } from "src/types/chat.type";

export interface ChatModalProps {
    id: number | undefined
}

export default function ChatModal(props: ChatModalProps) {
    const chat = useChat()
    const [newMessageCount, setNewMessageCount] = useState(0)
    const countRef = useRef(newMessageCount)
    const openChatModal = () => {
        setNewMessageCount(0)
        const modal = document.getElementById('chatModal') as HTMLDialogElement
        modal.showModal()
    }

    useEffect(() => {
        if(props.id)
            chat.onMessage(props.id.toString(), notify)
    }, [props.id])

    if(!props.id) {
        return <>
            <div className="rounded-full skeleton w-12 h-12">

            </div>
        </>
    }
    
    countRef.current = newMessageCount

    const notify = (message: Message) => {
        if(!message.isFromUser)
            setNewMessageCount(countRef.current + 1)
    }

    return <>
        {
            newMessageCount > 0 ?   
            <div className="absolute w-5 h-5 rounded-full bg-accent text-accent-content flex items-center justify-center font-bold text-sm">
                {newMessageCount}
            </div>
            :
            <></>
        }
        <button className="btn btn-circle btn-info text-xl w-12 h-12" onClick={openChatModal}>
            <FaComments />
        </button>
        <dialog id="chatModal" className="modal">
            <div className="modal-box h-2/3">
                <ChatBox id={props.id} />
            </div>
            <form method="dialog" className="modal-backdrop">
                <button>close</button>
            </form>
        </dialog>
    </>
}