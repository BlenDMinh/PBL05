import Chessground from '@react-chess/chessground'
import { useEffect } from 'react'
import { useParams } from 'react-router-dom'
import { GameType, useGameV2 } from 'src/contexts/gamev2.context'
import { v4 as uuidv4 } from 'uuid';

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