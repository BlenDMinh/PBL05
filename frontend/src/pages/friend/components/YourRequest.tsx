import { faCheck, faXmark } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import classNames from "classnames";
import { useState } from "react";
import { useQuery } from "react-query";
import { Link } from "react-router-dom";
import friendApi from "src/apis/friend.api";
import profileApi from "src/apis/profile.api";
import { FriendRequest, User } from "src/types/users.type";
import { calcTimeDif } from "src/utils/utils";

export interface YourRequestProps {
    request: FriendRequest
}

export default function YourRequest(props: YourRequestProps) {
    const [isResponse, setResponse] = useState(false)
    const profileQuery = useQuery([props.request.senderId], () => profileApi.getProfile(props.request.senderId))
    if(profileQuery.status != "success") {
        return <></>
    }
    const profile = profileQuery.data?.data as User

    const response = (accept: boolean) => {
        friendApi.replyFriendRequest(props.request.senderId, accept)
        setResponse(true)
    }

    return <div className='bg-base-100 p-2 rounded-md relative flex items-center justify-between'>
        <div className="flex gap-5 items-center">
            <Link to={`/profile/${props.request.senderId}`} className='avatar btn btn-circle btn-ghost'>
                <div className='w-20 rounded-full'>
                    <img src={profile.avatarUrl} alt='' className='mr-2 w-1/3' />
                </div>
            </Link>
            <div className="flex flex-col">
                <span className='text-base-content'>{profile.displayName}</span>
                <span className="text-sm text-slate-500">{calcTimeDif(props.request.createdAt + " UTC", Date.now())}</span>
            </div>
        </div>
        {
            isResponse ?
            <span className="btn btn-disabled !text-info">Replied</span>
            :
            <div className="flex gap-2 items-center">
                <button className="text-lg btn-square btn-sm btn btn-ghost" onClick={() => response(true)}><FontAwesomeIcon icon={faCheck} /></button>
                <button  className="text-lg btn-square btn-sm btn btn-ghost" onClick={() => response(false)}><FontAwesomeIcon icon={faXmark} /></button>
            </div>
        }
    </div>
}