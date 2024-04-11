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
    game.startGame(gameId ?? '')
  }, [gameId])

  useEffect(() => {
    console.log('Turn: ' + game.turn)
  }, [game.turn])

  if (!game.core) return <>Loading...</>

  return (
    <>
      <Chessground
        width={Math.min(window.innerWidth, window.innerHeight)}
        height={Math.min(window.innerWidth, window.innerHeight)}
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
    </>
  )
}
