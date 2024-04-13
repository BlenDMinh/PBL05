import Chessground from '@react-chess/chessground'
import { useEffect } from 'react'
import { useParams } from 'react-router-dom'
import { GameType, useGameV2 } from 'src/contexts/gamev2.context'
import { v4 as uuidv4 } from 'uuid';
import Modal from 'react-modal';

import 'src/pages/gamev2/css/chessground.base.css'
import 'src/pages/gamev2/css/chessground.brown.css'
import 'src/pages/gamev2/css/chessground.cburnett.css'

export interface BaseGameProps {
    gameType: GameType
}

export default function BaseGame(props: BaseGameProps) {
  const { gameId } = useParams()
  const game = useGameV2()
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

  if (!game.core) return <>Loading...</>

  console.log(game.result)

  return (
    <>
      <Chessground
        width={Math.min(window.innerWidth, window.innerHeight) * 0.8}
        height={Math.min(window.innerWidth, window.innerHeight) * 0.8}
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
      <dialog id="promotionModal" className="modal">
        <div className="modal-box">
          <h3 className="font-bold text-lg">Promotion</h3>
          <p className="py-4">Choose one of the following piece</p>
          <form method="dialog">
            <div className='w-full flex justify-evenly gap-5'>
              <button className="btn btn-primary flex-grow" onClick={() => game.promote("q")}>Queen</button>
              <button className="btn btn-primary flex-grow" onClick={() => game.promote("n")}>Knight</button>
              <button className="btn btn-primary flex-grow" onClick={() => game.promote("b")}>Bishop</button>
              <button className="btn btn-primary flex-grow" onClick={() => game.promote("r")}>Rook</button>
            </div>
            <button className="btn modal-action" onClick={() => game.promote()}>Cancel</button>
          </form>
        </div>
      </dialog>
    </>
  )
}