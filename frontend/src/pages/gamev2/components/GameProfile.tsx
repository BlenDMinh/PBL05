import { Link } from 'react-router-dom'
import { User } from 'src/types/users.type'
import { path } from 'src/constants/path'
import { blankAvatar, botAvatar } from 'src/assets/images'
import { BotPlayer, HumanPlayer, Player } from 'src/types/player.type'
import { GameRule } from '../types/game.v2.type'
import { GameResult, useGameV2 } from 'src/contexts/gamev2.context'
import { useEffect, useRef, useState } from 'react'
import TimeLeftTurn from './TimeLeftTurn'
import TimeLeftTotal from './TimeLeftTotal'

export interface GameProfileProps {
  profile?: Player
  role: string
  gameSide?: 'black' | 'white'
  firstMoveDone: () => boolean
  gameResult: GameResult
}

const defaultProps: Partial<GameProfileProps> = {
  gameSide: 'white',
  firstMoveDone: () => false,
  gameResult: GameResult.UNKNOWN
}

function GameProfile(props: GameProfileProps) {
  if (!props.profile) return <></>

  if ((props.profile as BotPlayer).difficulty) {
    return (
      <div className='w-full flex items-center justify-between'>
        <div className='flex gap-3 items-center h-fit py-2 px-2'>
          <div className='avatar w-8 h-8'>
            <img className='rounded-full' alt='avatar' src={botAvatar} />
          </div>
          <div className='flex-1 flex flex-col items-start justify-evenly gap-1'>
            <p className='text-base-content font-bold text-sm'>AI bot ({props.role})</p>
            <span className='text-base-content text-sm'>
              {'difficulty: ' + (props.profile as BotPlayer).difficulty}
            </span>
          </div>
        </div>
      </div>
    )
  }

  return (
    <div className='w-full flex items-center justify-between'>
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
          </div>
        </div>
        {(props.profile as HumanPlayer)?.remainMillisInTurn &&
          props.profile.side === props.gameSide &&
          props.gameResult === GameResult.UNKNOWN &&
          props.firstMoveDone() === true && (
            <TimeLeftTurn milliseconds={(props.profile as HumanPlayer).remainMillisInTurn!} />
          )}
      </div>
      {(props.profile as HumanPlayer)?.remainMillis && (
        <TimeLeftTotal
          milliseconds={(props.profile as HumanPlayer).remainMillis!}
          running={
            props.profile.side === props.gameSide && props.firstMoveDone() && props.gameResult === GameResult.UNKNOWN
          }
        />
      )}
    </div>
  )
}
GameProfile.defaultProps = defaultProps
export default GameProfile
