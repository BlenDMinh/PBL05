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
          {friend.displayName}
          <button
            className='btn btn-primary'
            onClick={() =>
              app.inviteOpponent(
                friend.id.toString(),
                {
                  id: 0,
                  name: 'Chơi 10 phút',
                  detail: {
                    minute_per_turn: -1,
                    total_minute_per_player: 10,
                    turn_around_steps: -1,
                    turn_around_time_plus: -1
                  },
                  published: true,
                  createdAt: new Date().toISOString()
                } as GameRuleset,
                'white',
                false
              )
            }
          >
            Invite
          </button>
        </div>
      ))}
    </div>
  )
}
