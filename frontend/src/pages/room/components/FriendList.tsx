import { useContext, useState } from 'react'
import { useQuery } from 'react-query'
import friendApi from 'src/apis/friend.api'
import { AppContext, AppContextType } from 'src/contexts/app.context'
import { GameRuleset } from 'src/types/game.type'

interface FriendListProps {
  userId: string
  keyword: string
  selectCallback?: (opponentId: string) => void
}

export default function FriendList(props: FriendListProps) {
  const friends = useQuery(['friends', props.keyword], () => friendApi.getFriendList(props.userId, 1, 8, props.keyword))
    .data?.data.friends

  const app = useContext<AppContextType>(AppContext)

  return (
    <div className='flex flex-col'>
      {friends?.map((friend) => (
        <div key={friend.id} className='flex items-center justify-between p-2 border-b border-base-300'>
          <div className='flex gap-5 items-center'>
            <div className='avatar'>
              <div className='rounded-full w-8 h-8'>
                <img src={friend.avatarUrl} alt='' />
              </div>
            </div>
            <span className='font-bold'>{friend.displayName}</span>
          </div>
          <button className='btn btn-primary' onClick={() => props.selectCallback?.(friend.id.toString())}>
            Invite
          </button>
        </div>
      ))}
    </div>
  )
}
