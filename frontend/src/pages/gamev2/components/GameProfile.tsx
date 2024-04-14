import { Link } from "react-router-dom";
import { User } from "src/types/users.type";

export interface GameProfileProps {
    profile?: User,
    role: string
}

export default function GameProfile(props: GameProfileProps) {
    if(!props.profile) {
        return <>
            <div className="rounded-full skeleton btn btn-circle btn-ghost w-12 h-12">
            </div>
            <div className="flex-1 flex flex-col items-start justify-evenly gap-2 max-w-[calc(70%)]">
                <div className="skeleton h-4 w-32"></div>
                <div className="skeleton h-4 w-16"></div>
            </div>
        </>
    }
    return <>
        <Link to={`/profile/${props.profile.id}`} target="_blank" className="avatar btn btn-circle btn-ghost w-12 h-12">
            <img
                className="rounded-full"
                alt='Tailwind CSS Navbar component'
                src={props.profile.avatarUrl ?? 'https://daisyui.com/images/stock/photo-1534528741775-53994a69daeb.jpg'}
            />
        </Link>
        <div className="flex-1 flex flex-col items-start justify-evenly gap-2 max-w-[calc(70%)]">
            <span className="text-base-content font-bold">{props.profile.displayName} ({props.role})</span>
            <span  className="text-base-content">{props.profile.elo}</span>
        </div>
    </>
}