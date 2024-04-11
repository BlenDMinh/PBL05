import Chessground from '@react-chess/chessground'
import { useEffect } from 'react'
import { useParams } from 'react-router-dom'
import { useGameV2 } from 'src/contexts/gamev2.context'

import 'src/pages/gamev2/css/chessground.base.css'
import 'src/pages/gamev2/css/chessground.brown.css'
import 'src/pages/gamev2/css/chessground.cburnett.css'

export default function GameV2() {
  const { gameId } = useParams()
  const game = useGameV2()
  useEffect(() => {
    if (gameId) game.startGame(gameId)
  }, [gameId])

  if (!game || !game.core || !game.turn || !game.side || !game.fen) return <>Loading...</>

  return (
    <>
      <Chessground
        width={Math.min(window.innerWidth, window.innerHeight)}
        height={Math.min(window.innerWidth, window.innerHeight)}
        config={{
          turnColor: game.turn,
          orientation: game.side,
          fen: game.fen,
          movable: {
            free: true,
            dests: game.getMoveableDests(),
            color: game.side
          },
          events: {
            move: game.move
          },
          autoCastle: false,
          lastMove: game.lastMove,
          animation: {
            enabled: true,
            duration: 2
          }
        }}
      />
    </>
  )
}
