import classNames from "classnames"

export interface ConversationProps {
    active: boolean
}

export default function Conversation(props: ConversationProps) {
    return <div className={classNames("h-32 w-full btn flex", {
        "btn-active": !props.active
    })}>
        <div className="avatar h-16">
            <img
                className="rounded-full"
                alt='Tailwind CSS Navbar component'
                src='https://daisyui.com/images/stock/photo-1534528741775-53994a69daeb.jpg'
            />
        </div>
        <div className="flex-1 flex flex-col items-start justify-evenly gap-2">
            <span className="text-base-content font-bold">Username</span>
            <p className="text-base-content font-thin">Last message</p>
        </div>
    </div>
}