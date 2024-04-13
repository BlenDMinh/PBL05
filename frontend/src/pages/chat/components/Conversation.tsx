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
        const fromDate = new Date(from)
        const toDate = new Date(to)
        const dif = toDate.getTime() - fromDate.getTime()
        const seconds = dif / 1000;
        const minutes = seconds / 60;
        if(minutes < 60)
            return `${Math.floor(minutes)} m`
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
    const [lastTime, setLastTime] = useState(calcTimeDif(props.conversation.message.sendedAt, Date.now()))
    useEffect(() => {
        setInterval(() => {
            setLastTime(calcTimeDif(props.conversation.message.sendedAt, Date.now()))
        }, 1000 * 60)
    }, [])

    return <Link className={classNames("h-32 w-full btn flex", {
        "btn-active": !props.active
    })}
        to={`/chat/${props.conversation.id}`}
    >
        <div className="avatar h-16">
            <img
                className="rounded-full"
                alt='Avatar'
                src={props.conversation.avatarUrl ?? 'https://daisyui.com/images/stock/photo-1534528741775-53994a69daeb.jpg'}
            />
            <div className="absolute bg-accent rounded-full w-5 h-5 border border-accent-content">

            </div>
        </div>
        <div className="flex-1 flex flex-col items-start justify-evenly gap-2">
            <span className="text-base-content font-bold">{props.conversation.displayName}</span>
            <p className="text-base-content font-thin">{props.conversation.message.content} .{lastTime}</p>
        </div>
    </Link>
}