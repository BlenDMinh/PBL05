import classNames from "classnames"
import { useEffect, useState } from "react"
import { Link } from "react-router-dom"
import { Conversation as ConversationType } from "src/types/chat.type"
import { calcTimeDif } from "src/utils/utils"

export interface ConversationProps {
    conversation: ConversationType,
    active?: boolean
}

export default function Conversation(props: ConversationProps) {
    const [lastTime, setLastTime] = useState(calcTimeDif(props.conversation.message.sendedAt + " UTC", Date.now()))
    useEffect(() => {
        setInterval(() => {
            const sendTime = props.conversation.message.sendedAt + " UTC"
            setLastTime(calcTimeDif(sendTime, Date.now()))
        }, 1000 * 60)
    }, [])
    useEffect(() => {
        const sendTime = props.conversation.message.sendedAt + " UTC"
        setLastTime(calcTimeDif(sendTime, Date.now()))
    }, [props.conversation.message])

    return <Link className={classNames("h-32 w-full btn flex flex-nowrap justify-start", {
        "btn-active": !props.active
    })}
        to={`/chat/${props.conversation.id}`}
    >
        <div className="avatar h-16 w-16 min-h-[4rem] min-w-[4rem]">
            <img
                className="rounded-full"
                alt='Avatar'
                src={props.conversation.avatarUrl ?? 'https://daisyui.com/images/stock/photo-1534528741775-53994a69daeb.jpg'}
            />
            <div className="absolute bg-accent rounded-full w-5 h-5 border border-accent-content">

            </div>
        </div>
        <div className="flex-1 flex flex-col items-start justify-evenly gap-2 max-w-[calc(70%)]">
            <span className="text-base-content font-bold">{props.conversation.displayName}</span>
            <div className="flex w-full">
                <p className="text-start w-4/5 text-base-content font-thin truncate">{props.conversation.message.isFromUser ? "You: " : ""}{props.conversation.message.content}</p>
                <p className="text-base-content font-thin text-ellipsis min-w-fit">{" "}.{lastTime}</p>
            </div>
        </div>
    </Link>
}