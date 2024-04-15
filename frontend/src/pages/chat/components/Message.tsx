import classNames from "classnames"
import { useEffect, useState } from "react"
import { useMutation, useQuery } from "react-query"
import { Link } from "react-router-dom"
import profileApi from "src/apis/profile.api"
import { User } from "src/types/users.type"

export interface MessageProps {
    side: "left" | "right",
    message: string,
    senderId: number
}

export default function Message(props: MessageProps) {
    const [user, setUser] = useState<User | null>(null)
    const profileMutation = useMutation({
        mutationFn: (id: number) => profileApi.getProfile(id),
        onSuccess: (data) => {
            setUser(data.data as User)
        }
    })
    useEffect(() => {
        profileMutation.mutate(props.senderId)
    }, [props.senderId])

    return <div className={classNames("flex gap-5 chat", {
        "self-start chat-start": props.side == "left",
        "self-end chat-end": props.side == "right"
    })}>
        {
            props.side == "left" ?
            <>
            {
                user ?
                <Link to={`/profile/${user?.id}`} target="_blank" className="avatar chat-image btn btn-circle btn-ghost">
                    <img
                        className="w-10 rounded-full"
                        alt='Tailwind CSS Navbar component'
                        src={user?.avatarUrl ?? 'https://daisyui.com/images/stock/photo-1534528741775-53994a69daeb.jpg'}
                    />
                </Link>
                :
                <div className="rounded-full skeleton btn btn-circle btn-ghost w-12 h-12"></div>
            }
            </>
            :
            <></>
        }
        <div className={classNames("chat-bubble", {
            "chat-bubble-primary": props.side == "right"
        })}>
            {props.message}
        </div>
    </div>
}