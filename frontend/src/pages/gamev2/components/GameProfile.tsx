import { Link } from 'react-router-dom'
import { User } from 'src/types/users.type'
import { path } from 'src/constants/path'
import { blankAvatar, botAvatar } from 'src/assets/images'
import { BotPlayer, HumanPlayer, Player } from 'src/types/player.type'
import { GameRule } from '../types/game.v2.type'

export interface GameProfileProps {
  profile?: Player
  role: string
  gameRule: GameRule | null
}

export default function GameProfile(props: GameProfileProps) {
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
          <div className='border rounded-full bg-base-100 flex justify-center items-center'>
            {props.gameRule && <p className='text-base-content text-xs px-1 py-1'>
              {props.gameRule?.minutePerTurn === -1 ? '' : props.gameRule?.minutePerTurn! * 60}s
            </p>}
          </div>
        </div>
      </div>
    </div>
  )
}
