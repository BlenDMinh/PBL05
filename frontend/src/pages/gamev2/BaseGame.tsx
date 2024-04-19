/* eslint-disable jsx-a11y/aria-role */
import Chessground from '@react-chess/chessground'
import { useEffect } from 'react'
import { Link, useNavigate, useParams } from 'react-router-dom'
import { GameResult, GameType, useGameV2 } from 'src/contexts/gamev2.context'
import { v4 as uuidv4 } from 'uuid'
import Modal from 'react-modal'

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

export interface BaseGameProps {
  gameType: GameType
}

export default function BaseGame(props: BaseGameProps) {
  const { gameId } = useParams()
  const game = useGameV2()
  const navigate = useNavigate()

  const opponentQuery = useQuery(['profile', game.opponentId], () => profileApi.getProfile(game.opponentId))

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

  console.log(game.result)

  return (
    <>
      <div className='flex w-full h-screen items-center justify-center px-32 pt-32 pb-16 gap-12'>
        <div className='w-1/4 h-full flex flex-col gap-12'>
          <div className='h-24 w-full flex items-center gap-5 border-2 rounded-lg border-base-300  bg-base-200 p-2'>
            <GameProfile profile={opponentQuery.data?.data} role='Opponent' />
          </div>
          <button className='btn' onClick={() => game.resign()}>
            Resign
          </button>
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
        <div className='flex w-1/4 h-full border-2 rounded-lg border-base-300 bg-base-200'>
          {game.moveHistories.map((move) => (
            <div key={'aaaaaaa'} className='flex gap-5'>
              {move.from} {move.to} {move.piece} {move.promotiom}
            </div>
          ))}
        </div>
        {/* {props.gameType == GameType.PVP ? (
          <ChatContextProvider>
            <div className='flex w-1/4 h-full border-2 rounded-lg border-base-300 bg-base-200'>
              {game.opponentId ? <ChatBox id={game.opponentId} /> : <span className='loading loading-spinner' />}
            </div>
          </ChatContextProvider>
        ) : (
          <></>
        )} */}
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
