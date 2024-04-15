import classNames from "classnames";
import { useState } from "react";
import { useQuery } from "react-query";
import { Link } from "react-router-dom";
import friendApi from "src/apis/friend.api";
import profileApi from "src/apis/profile.api";
import { FriendRequest, User } from "src/types/users.type";

export interface YourRequestProps {
    request: FriendRequest
}

export default function YourRequest(props: YourRequestProps) {
    const [isResponse, setReponse] = useState(false)
    const profileQuery = useQuery([props.request.senderId], () => profileApi.getProfile(props.request.senderId))
    if(profileQuery.status != "success") {
        return <></>
    }
    const profile = profileQuery.data?.data as User

    const reponse = (accept: boolean) => {
        friendApi.replyFriendRequest(props.request.senderId, accept)
        setReponse(true)
    }

    return <div className='bg-base-200 p-2 rounded-md relative flex items-center justify-between'>
        <div className="flex gap-5 items-center">
            <Link to={`/profile/${props.request.senderId}`} className='avatar btn btn-circle btn-ghost'>
                <div className='w-20 rounded-full'>
                    <img src={profile.avatarUrl} alt='' className='mr-2 w-1/3' />
                </div>
            </Link>
            <span className='text-base-content'>{profile.displayName}</span>
        </div>
        {
            isResponse ?
            <span className="btn btn-disabled !text-info">Replied</span>
            :
            <details className="dropdown dropdown-end">
                <summary className="btn m-1 btn-ghost text-primary">Response</summary>
                <ul className="p-2 shadow menu dropdown-content z-[1] bg-base-100 rounded-box w-52">
                    <li><button onClick={() => reponse(true)}>Accept</button></li>
                    <li><button onClick={() => reponse(false)}>Decline</button></li>
                </ul>
            </details>
        }
    </div>
}