import Chessground from '@react-chess/chessground'
import { useEffect } from 'react'
import { Link, useNavigate, useParams } from 'react-router-dom'
import { GameResult, GameType, useGameV2 } from 'src/contexts/gamev2.context'
import { v4 as uuidv4 } from 'uuid';
import Modal from 'react-modal';

import 'src/pages/gamev2/css/chessground.base.css'
import 'src/pages/gamev2/css/chessground.brown.css'
import 'src/pages/gamev2/css/chessground.cburnett.css'
import classNames from 'classnames';
import { path } from 'src/constants/path';
import ChatBox from '../chat/components/ChatBox';
import { ChatContextProvider } from 'src/contexts/chat.context';
import profileApi from 'src/apis/profile.api';
import { useQuery } from 'react-query';
import { getProfileFromLS } from 'src/utils/auth';
import GameProfile from './components/GameProfile';

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
  }, [gameId])

  useEffect(() => {
    console.log('Turn: ' + game.turn)
  }, [game.turn])

  useEffect(() => {
    if(game.isPromoting) {
      const modal = document.getElementById('promotionModal') as HTMLDialogElement
      if(modal) {
        modal.showModal()
      }
    }
  }, [game.isPromoting])

  useEffect(() => {
    if(game.result != GameResult.UNKNOWN) {
      const modal = document.getElementById('resultModal') as HTMLDialogElement
      if(modal) {
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
        <div className='flex-1 border-base-300 rounded-lg border'>
          Timer
        </div>
        <div className='h-24 w-full flex items-center gap-5 border-2 rounded-lg border-base-300 bg-base-200 p-2'>
          <GameProfile profile={player} role='You' />
        </div>
      </div>
      <div className={classNames(`flex-1 min-w-[${Math.min(window.innerWidth, window.innerHeight) * 0.8}px] min-h-[${Math.min(window.innerWidth, window.innerHeight) * 0.8}px] h-full rounded-lg border-2 border-base-300 flex items-center justify-center`)}>
        <Chessground
          width={Math.min(window.innerWidth, window.innerHeight) * 0.7}
          height={Math.min(window.innerWidth, window.innerHeight) * 0.7}
          config={{
            turnColor: game.turn,
            fen: game.fen,
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
      {props.gameType == GameType.PVP ?
      <ChatContextProvider>
        <div className="flex w-1/4 h-full border-2 rounded-lg border-base-300 bg-base-200">
          {
            game.opponentId ?
            <ChatBox id={game.opponentId} />
            :
            <span className='loading loading-spinner'/>
          }
        </div>
      </ChatContextProvider>
      : <></>
      }
    </div>
      <dialog id="promotionModal" className="modal">
        <div className="modal-box">
          <h3 className="font-bold text-lg">Promotion</h3>
          <p className="py-4">Choose one of the following piece</p>
          <form method="dialog">
            <div className='w-full flex justify-evenly gap-5'>
              <button className="btn btn-primary flex-1" onClick={() => game.promote("q")}>Queen</button>
              <button className="btn btn-primary flex-1" onClick={() => game.promote("n")}>Knight</button>
              <button className="btn btn-primary flex-1" onClick={() => game.promote("b")}>Bishop</button>
              <button className="btn btn-primary flex-1" onClick={() => game.promote("r")}>Rook</button>
            </div>
            <button className="btn modal-action" onClick={() => game.promote()}>Cancel</button>
          </form>
        </div>
      </dialog>
      <dialog id="resultModal" className="modal">
        <div className="modal-box">
          <h3 className="font-bold text-lg text-center">Game ended</h3>
          <p className={classNames("text-center text-6xl font-bold py-12", {
            "text-info" : game.result == GameResult.WIN,
            "text-error": game.result == GameResult.LOSE,
            "text-accent": game.result == GameResult.DRAW,
            "text-base-content": game.result == GameResult.UNKNOWN
          })}>
            {
            game.result == GameResult.WIN ? "WIN" : 
            game.result == GameResult.LOSE ? "LOSE" : 
            game.result ==  GameResult.DRAW ? "DRAW" : "IDK"
            }
          </p>
          <form method="dialog">
            <div className='w-full flex justify-evenly gap-5'>
              <button className="btn btn-primary flex-1 text-lg" onClick={() => navigate(props.gameType == GameType.PVP ? path.quickMatch : path.playWithBot)}>Play again</button>
              <button className="btn flex-1 text-lg" onClick={() => navigate(path.home)}>Exit</button>
            </div>
          </form>
        </div>
      </dialog>
    </>
  )
}