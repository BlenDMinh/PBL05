import { Friend as FriendType } from "src/types/users.type";
import { FaUserPlus } from "react-icons/fa";
import { Link } from "react-router-dom";
import classNames from "classnames";

export interface YourFriendProps {
    friend: FriendType
}

export default function YourFriend(props: YourFriendProps) {
    return <div className='bg-base-100 p-2 rounded-md relative flex items-center justify-between'>
        <div className="flex gap-5 items-center">
            <Link to={`/profile/${props.friend.id}`} className='avatar btn btn-circle btn-ghost'>
                <div className='w-20 rounded-full'>
                    <img src={props.friend.avatarUrl} alt='' className='mr-2 w-1/3' />
                </div>
            </Link>
            <span className='text-base-content'>{props.friend.displayName}</span>
        </div>
        <div className='flex gap-5 items-center'>
            <div className="flex gap-2 items-center">
                <div className={classNames('rounded-full w-3 h-3', {
                    'bg-primary' : props.friend.online,
                    'bg-base-300' : !props.friend.online
                })} />
                {props.friend.online ? "Online" : "Offline"}
            </div>
            <button className="btn btn-primary">
                Invite
            </button>
        </div>
    </div>
}