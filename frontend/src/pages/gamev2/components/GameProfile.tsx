import { Link } from 'react-router-dom'
import { User } from 'src/types/users.type'
import { path } from 'src/constants/path'
import { blankAvatar, botAvatar } from 'src/assets/images'
import { BotPlayer, HumanPlayer, Player } from 'src/types/player.type'
import { GameRule } from '../types/game.v2.type'
import { useGameV2 } from 'src/contexts/gamev2.context'
import { useEffect, useRef, useState } from 'react'

export interface GameProfileProps {
  profile?: Player
  role: string
  gameRule: GameRule | null
}

export default function GameProfile(props: GameProfileProps) {

  const intervalRef = useRef<NodeJS.Timeout | null>(null)
  const [time, setTime] = useState<number>(props.gameRule ? props.gameRule.minutePerTurn * 60 : 0)
  const game = useGameV2()

  useEffect(() => {
    if (intervalRef.current) clearInterval(intervalRef.current)
    if (!props.gameRule || props.gameRule?.minutePerTurn === -1) return
    setTime(props.gameRule ? props.gameRule.minutePerTurn * 60 : 0)
    if(!game.turn) return
    if(game.turn !== props.profile?.side) return
    const id = setInterval(() => {
      setTime(time => time - 1)
    }, 1000)
    intervalRef.current = id
    return () => {
      if (intervalRef.current) clearInterval(intervalRef.current)
    }
  }, [game.turn])

  if (!props.profile) return <></>

  if ((props.profile as BotPlayer).difficulty) {
    return (
      <div className='flex gap-3 items-center h-fit py-2 px-2'>
        <div className='avatar w-8 h-8'>
          <img className='rounded-full' alt='avatar' src={botAvatar} />
        </div>
        <div className='flex-1 flex flex-col items-start justify-evenly gap-1'>
          <p className='text-base-content font-bold text-sm'>AI bot ({props.role})</p>
          <span className='text-base-content text-sm'>{'difficulty: ' + (props.profile as BotPlayer).difficulty}</span>
        </div>
      </div>
    )
  }

  return (
    <div className='flex gap-3 items-center h-fit py-2 px-2'>
      <Link to={path.profile.replace(':id', (props.profile as HumanPlayer).id + '')} target='_blank'>
        <div className='avatar w-8 h-8'>
          <img className='rounded-full' alt='avatar' src={(props.profile as HumanPlayer).avatarUrl ?? blankAvatar} />
        </div>
      </Link>
      <div className='flex-1 flex flex-col items-start justify-evenly gap-1'>
        <span className='text-base-content font-bold text-sm'>
          {(props.profile as HumanPlayer).displayName} ({props.role})
        </span>
        <div className='flex justify-between gap-5'>
          <span className='text-base-content text-sm'>{'elo: ' + (props.profile as HumanPlayer).elo}</span>
          {
            props.gameRule?.minutePerTurn !== -1 &&
            <div className='border w-20 rounded-full bg-base-100 flex justify-center items-center'>
              {props.gameRule && <p className='text-base-content text-xs px-1 py-1'>
                {time}s
              </p>}
            </div>
          }
        </div>
      </div>
    </div>
  )
}
