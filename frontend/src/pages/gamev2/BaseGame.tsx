/* eslint-disable jsx-a11y/aria-role */
import Chessground from '@react-chess/chessground'
import { useEffect, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { GameResult, GameType, useGameV2 } from 'src/contexts/gamev2.context'
import { v4 as uuidv4 } from 'uuid'

import 'src/pages/gamev2/css/chessground.base.css'
import 'src/pages/gamev2/css/chessground.brown.css'
import 'src/pages/gamev2/css/chessground.cburnett.css'
import classNames from 'classnames'
import { path } from 'src/constants/path'
import ChatBox from '../chat/components/ChatBox'
import { ChatContextProvider } from 'src/contexts/chat.context'
import profileApi from 'src/apis/profile.api'
import { useQuery } from 'react-query'
import { getProfileFromLS } from 'src/utils/auth'
import GameProfile from './components/GameProfile'
import PromotionModal from './components/PromotionModal'
import ResultModal from './components/ResultModal'
import { FaArrowRight, FaCheck, FaFlag, FaHandHolding, FaHandshake } from 'react-icons/fa'
import ChatModal from '../chat/ChatModal'
import { PieceTextureKey, pieceTexture } from './texture/piece.texture'
import { User } from 'src/types/users.type'

export interface BaseGameProps {
  gameType: GameType
}

export default function BaseGame(props: BaseGameProps) {
  
  const { gameId } = useParams()
  const game = useGameV2()
  const navigate = useNavigate()

  const [resign, setResign] = useState(false)

  const opponentQuery = useQuery(['profile', game.opponentId], () => game.opponentId ? profileApi.getProfile(game.opponentId) : null)
  const [opponent, setOpponent] = useState<User>()

  useEffect(() => {
    if(opponentQuery.data?.data) {
      setOpponent(opponentQuery.data?.data)
    }
  }, [opponentQuery.status])

  const player = getProfileFromLS()

  useEffect(() => {
    game.startGame(gameId ?? uuidv4(), props.gameType)
    const modal = document.getElementById('resultModal') as HTMLDialogElement
    modal.close()
  }, [gameId])

  useEffect(() => {
    console.log('Turn: ' + game.turn)
  }, [game.turn])

  useEffect(() => {
    if (game.isPromoting) {
      const modal = document.getElementById('promotionModal') as HTMLDialogElement
      if (modal) {
        modal.showModal()
      }
    }
  }, [game.isPromoting])

  useEffect(() => {
    if (game.result != GameResult.UNKNOWN) {
      const modal = document.getElementById('resultModal') as HTMLDialogElement
      if (modal) {
        modal.showModal()
      }
    }
  }, [game.result])

  if (!game.core) return <>Loading...</>

  return (
    <>
      <div className='flex w-full h-screen items-center justify-center px-32 pt-32 pb-16 gap-12'>
        <div className='w-1/4 h-full flex flex-col gap-12'>
          <div className='h-24 w-full flex items-center gap-5 border-2 rounded-lg border-base-300  bg-base-200 p-2'>
            <GameProfile profile={opponentQuery.data?.data} role='Opponent' />
          </div>
          <div className='flex-1 bg-base-200 rounded-lg'>
          </div>
          <div className='flex w-full justify-around'>
            {
              resign ? 
              <div className='flex w-1/2 gap-2 justify-evenly'>
                <button className='btn btn-error w-1/2' onClick={() => game.resign()}>
                  Yes
                </button>
                <button className='btn btn-primary w-1/2' onClick={() => setResign(false)}>
                  No
                </button>
              </div>
              :
              <button className='btn btn-error' onClick={() => setResign(true)}>
                <FaFlag />
                Resign
              </button>
            }
            <div className='tooltip' data-tip='Not working yet'>
              <button className='btn btn-info btn-disabled' onClick={() => game.resign()}>
                <FaHandshake />
                Draw
              </button>
            </div>
          </div>
          <div className='h-24 w-full flex items-center gap-5 border-2 rounded-lg border-base-300 bg-base-200 p-2'>
            <GameProfile profile={player} role='You' />
          </div>
        </div>
        <div
          className={classNames(
            `flex-1 min-w-[${Math.min(window.innerWidth, window.innerHeight) * 0.8}px] min-h-[${
              Math.min(window.innerWidth, window.innerHeight) * 0.8
            }px] h-full rounded-lg border-2 border-base-300 flex items-center justify-center`
          )}
        >
          <Chessground
            width={Math.min(window.innerWidth, window.innerHeight) * 0.7}
            height={Math.min(window.innerWidth, window.innerHeight) * 0.7}
            config={{
              turnColor: game.turn,
              fen: game.fen,
              orientation: game.side,
              draggable: {
                enabled: false
              },
              movable: {
                free: false,
                dests: game.getMoveableDests(),
                color: game.side
              },
              events: {
                move: game.move
              },
              lastMove: game.lastMove
            }}
          />
        </div>
        <div className='flex flex-col h-full w-1/4 gap-5'>
          {props.gameType == GameType.PVP && <div className='flex justify-end'>
            <ChatContextProvider>
              <ChatModal id={opponentQuery.data?.data.id} />
            </ChatContextProvider>
          </div>}
          <div className='flex-1 flex flex-col-reverse w-full border-2 p-3 rounded-lg border-base-300 bg-base-200 overflow-y-auto justify-start'>
            <div className='flex-1'></div>
            <div className='h-fit grid grid-cols-2 gap-3'>
              {game.moveHistories.map((move, id) => (
                <div key={id} className='flex items-center justify-start gap-5 h-10 bg-base-100 rounded-lg p-2'>
                  <div className={classNames('rounded-full w-8 h-8 p-1', {
                    'bg-slate-500': move.piece.includes('WHITE'),
                    'bg-slate-200': move.piece.includes('BLACK')
                  })}>
                    <img src={pieceTexture[move.piece as PieceTextureKey]} alt="" />
                  </div>
                  <span className='font-bold'>{move.to}</span>
                  {
                    move.promotion !== 'NONE' ? <>
                      <FaArrowRight />
                      <div className={classNames('rounded-full w-8 h-8 p-1', {
                        'bg-slate-500': move.piece.includes('WHITE'),
                        'bg-slate-200': move.piece.includes('BLACK')
                      })}>
                        <img src={pieceTexture[move.promotion as PieceTextureKey]} alt="" />
                      </div>
                    </>
                    :
                    <></>
                  }
                </div>
              ))}
            </div>
          </div>
        </div>
      </div>
      <PromotionModal />
      <ResultModal
        onReplay={() => {
          game.onEnd()
          navigate(props.gameType == GameType.PVP ? path.quickMatch : path.playWithBot)
        }}
        onExit={() => {
          game.onEnd()
          navigate(path.home)
        }}
      />
    </>
  )
}
