import classNames from "classnames"

export interface MessageProps {
    side: "left" | "right",
    message: string,
    senderAvatar?: string,
    senderName?: string,
}

export default function Message(props: MessageProps) {
    return <div className={classNames("flex gap-5", {
        "self-start": props.side == "left",
        "self-end": props.side == "right"
    })}>
        {
            props.side == "left" ?
            <div className="avatar btn btn-circle btn-ghost">
                <img
                    className="w-10 rounded-full"
                    alt='Tailwind CSS Navbar component'
                    src={props.senderAvatar ?? 'https://daisyui.com/images/stock/photo-1534528741775-53994a69daeb.jpg'}
                />
            </div>
            :
            <></>
        }
        <p className={classNames("shadow-xl card w-fit text-wrap break-normal p-2 justify-center max-w-2xl items-center", {
            "bg-primary text-primary-content": props.side == "right",
            "bg-base-300 text-base-content": props.side == "left"
        })}>
            {props.message}
        </p>
    </div>
}