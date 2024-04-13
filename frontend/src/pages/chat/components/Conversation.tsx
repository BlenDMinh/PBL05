import classNames from "classnames"
import { useEffect, useState } from "react"
import { Link } from "react-router-dom"
import { Conversation as ConversationType } from "src/types/chat.type"

export interface ConversationProps {
    conversation: ConversationType,
    active?: boolean
}

export default function Conversation(props: ConversationProps) {
    const calcTimeDif = (from: any, to: any) => {
        console.log(from, to)
        const fromDate = new Date(from).getTime()
        const toDate = new Date(to).getTime()
        console.log(fromDate, toDate)
        const dif = toDate - fromDate
        const seconds = dif / 1000;
        const minutes = seconds / 60;
        if(minutes < 60)
            return `${Math.max(Math.floor(minutes), 1)} m`
        const hours = minutes / 60;
        if(hours < 24)
            return `${Math.floor(hours)} h`
        const days = hours / 24;
        if(days <= 60)
            return `${Math.floor(days)} D`
        const months = days / 30;
        if(months <= 24)
            return `${Math.floor(months)} M`
        const years = months / 12;
        return `${Math.floor(years)} Y`
    }
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
        <div className="avatar min-h-16 min-w-16">
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
                <p className="w-4/5 text-base-content font-thin truncate">{props.conversation.message.isFromUser ? "You: " : ""}{props.conversation.message.content}</p>
                <p className="text-base-content font-thin text-ellipsis">{" "}.{lastTime}</p>
            </div>
        </div>
    </Link>
}