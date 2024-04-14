import classNames from "classnames"
import { useQuery } from "react-query"
import { Link } from "react-router-dom"
import profileApi from "src/apis/profile.api"

export interface MessageProps {
    side: "left" | "right",
    message: string,
    senderId: number
}

export default function Message(props: MessageProps) {
    const profile = useQuery(['profile', props.senderId], () => profileApi.getProfile(props.senderId))

    return <div className={classNames("flex gap-5 chat", {
        "self-start chat-start": props.side == "left",
        "self-end chat-end": props.side == "right"
    })}>
        {
            props.side == "left" ?
            <Link to={`/profile/${props.senderId}`} target="_blank" className="avatar chat-image btn btn-circle btn-ghost">
                <img
                    className="w-10 rounded-full"
                    alt='Tailwind CSS Navbar component'
                    src={profile.data?.data.avatarUrl ?? 'https://daisyui.com/images/stock/photo-1534528741775-53994a69daeb.jpg'}
                />
            </Link>
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