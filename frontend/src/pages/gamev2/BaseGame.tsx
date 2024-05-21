import Chessground from '@react-chess/chessground'
import { useEffect, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { FEN_DEFAULT, GameResult, GameType, useGameV2 } from 'src/contexts/gamev2.context'
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
import GameProfile from './components/GameProfile'
import PromotionModal from './components/PromotionModal'
import ResultModal from './components/ResultModal'
import { FaArrowRight, FaCheck, FaFlag, FaHandHolding, FaHandshake } from 'react-icons/fa'
import ChatModal from '../chat/ChatModal'
import { PieceTextureKey, pieceTexture } from './texture/piece.texture'
import { BotPlayer, HumanPlayer, Player } from 'src/types/player.type'

export interface BaseGameProps {
  gameType: GameType
}

export default function BaseGame(props: BaseGameProps) {
  const { gameId } = useParams()
  const game = useGameV2()
  const navigate = useNavigate()
  const [groundSize, setGroundSize] = useState<number>(0)
  const [resign, setResign] = useState(false)

  useEffect(() => {
    if (gameId) game.startGame(gameId, props.gameType)
    const modal = document.getElementById('resultModal') as HTMLDialogElement
    if (modal) modal.close()
    return () => {
      game.setFen(FEN_DEFAULT)
    }
  }, [gameId])

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
    } else {
      const modal = document.getElementById('resultModal') as HTMLDialogElement
      if (modal) {
        modal.close()
      }
    }
  }, [game.result])
  useEffect(() => {
    const updateSize = () => {
      setGroundSize(Math.min(window.innerHeight * 0.67, window.innerWidth * 0.67))
    }

    // Initial update
    updateSize()

    // Attach event listeners for window resize
    window.addEventListener('resize', updateSize)

    // Cleanup function to remove event listener
    return () => {
      window.removeEventListener('resize', updateSize)
    }
  }, [])

  if (!game.core)
    return (
      <div className='bg-base h-full w-full flex items-center justify-center'>
        <span className='loading loading-lg loading-ring'></span>
      </div>
    )

  return (
    game.isReceiveFromServer && (
      <>
        <div className={classNames('flex h-screen items-center justify-center pt-16 gap-12 w-full')}>
          <div
            style={{ width: `${groundSize}px` }}
            className={classNames(`flex flex-col rounded-lg items-center justify-between`, {
              'pointer-events-none': game.result !== GameResult.UNKNOWN
            })}
          >
            <GameProfile
              profile={game.opponent}
              role='Opponent'
              gameSide={game.turn}
              firstMoveDone={game.firstMoveDone}
              gameResult={game.result}
            />
            <Chessground
              key={gameId}
              width={groundSize}
              height={groundSize}
              config={{
                turnColor: game.turn,
                fen: game.fen,
                orientation: game.me?.side === 'white' ? 'white' : 'black',
                draggable: {
                  enabled: true
                },
                movable: {
                  free: false,
                  dests: game.getMoveableDests(),
                  color: game.me?.side
                },
                events: {
                  move: game.move
                },
                lastMove: game.lastMove
              }}
            />
            <GameProfile
              profile={game.me}
              gameSide={game.turn}
              role='You'
              firstMoveDone={game.firstMoveDone}
              gameResult={game.result}
            />
          </div>
          <div className={classNames('flex flex-col gap-5 h-full', 'w-60')}>
            {props.gameType == GameType.PVP && (game.opponent as HumanPlayer)?.id && (
              <div className='flex justify-end'>
                <ChatContextProvider>
                  <ChatModal id={(game.opponent as HumanPlayer).id} />
                </ChatContextProvider>
              </div>
            )}
            <div className='flex-1 flex flex-col-reverse w-full border-2 p-3 rounded-lg border-base-300 bg-base-200 overflow-y-auto justify-start'>
              <div className='flex-1'></div>
              <div className='h-fit grid grid-cols-2 gap-3'>
                {game.moveHistories.map((move, id) => (
                  <div key={id} className='flex items-center justify-between h-5 bg-base-100 rounded-lg p-2'>
                    <div
                      className={classNames('rounded-full w-[20px] h-[20px]', {
                        'bg-slate-500': move.piece.includes('WHITE'),
                        'bg-slate-200': move.piece.includes('BLACK')
                      })}
                    >
                      <img src={pieceTexture[move.piece as PieceTextureKey]} alt='' />
                    </div>
                    <span className='font-semibold text-sm flex-1 pl-2'>{move.to}</span>
                    {move.promotion !== 'NONE' ? (
                      <>
                        <FaArrowRight />
                        <div
                          className={classNames('rounded-full w-[20px] h-[20px]', {
                            'bg-slate-500': move.piece.includes('WHITE'),
                            'bg-slate-200': move.piece.includes('BLACK')
                          })}
                        >
                          <img src={pieceTexture[move.promotion as PieceTextureKey]} alt='' />
                        </div>
                      </>
                    ) : (
                      <></>
                    )}
                  </div>
                ))}
              </div>
            </div>
            {game.result === GameResult.UNKNOWN && (
              <div className='flex w-full justify-around'>
                {resign ? (
                  <div className='flex w-1/2 gap-2 justify-evenly'>
                    <button className='btn btn-error w-1/2' onClick={() => game.resign()}>
                      Yes
                    </button>
                    <button className='btn btn-primary w-1/2' onClick={() => setResign(false)}>
                      No
                    </button>
                  </div>
                ) : (
                  <button className='btn btn-error' onClick={() => setResign(true)}>
                    <FaFlag />
                    Resign
                  </button>
                )}
                {props.gameType === GameType.PVP && (
                  <div className='tooltip' data-tip='Not working yet'>
                    <button className='btn btn-info btn-disabled' onClick={() => game.resign()}>
                      <FaHandshake />
                      Draw
                    </button>
                  </div>
                )}
              </div>
            )}
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
            window.location.reload()
          }}
        />
      </>
    )
  )
}
