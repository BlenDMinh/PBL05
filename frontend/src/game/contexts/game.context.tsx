import { createContext, useContext, useEffect, useMemo, useState } from 'react'
import { ReactWithChild } from 'src/interface/app'
import { config } from '../configs/game'
import { GameAction, GameState } from '../types/game.type'
import { ChessPiece } from '../core/ChessPiece'
import { convertRawToGameState } from './util'
import { ws } from '../constants/ws'
import useWebSocket, { ReadyState } from 'react-use-websocket'
import { getSessionIdFromLS } from 'src/utils/auth'
import { GameSocketMessage } from '../types/ws.type'

export interface GameContextType {
  gameId: string
  startGame: (gameId: string) => void
  gameWidth: number
  gameHeight: number
  rawGameState: string | null
  setRawGameState: React.Dispatch<React.SetStateAction<string | null>>
  boardSize: number
  selectedGridIndex: number
  setSelectedGridIndex: React.Dispatch<React.SetStateAction<number>>
  gameState: GameState | null
  getCurrentSelectedChessPiece: () => ChessPiece | null
  doMove: (move: GameAction) => void
}

const initGameContext: GameContextType = {
  gameId: '',
  startGame: (gameId: string) => null,
  gameWidth: window.innerWidth * config.widthScale,
  gameHeight: window.innerHeight * config.heightScale,
  rawGameState: null,
  setRawGameState: () => null,
  boardSize: Math.min(window.innerWidth * config.widthScale, window.innerHeight * config.heightScale),
  selectedGridIndex: -1,
  setSelectedGridIndex: () => null,
  gameState: null,
  getCurrentSelectedChessPiece: () => null, // Placeholder implementation
  doMove: () => null
}

export const GameContext = createContext<GameContextType>(initGameContext)

export default function GameContextProvider({ children }: ReactWithChild) {
  const [gameWidth, setGameWidth] = useState(initGameContext.gameWidth)
  const [gameHeight, setGameHeight] = useState(initGameContext.gameHeight)
  const [rawGameState, setRawGameState] = useState(initGameContext.rawGameState)
  const [selectedGridIndex, setSelectedGridIndex] = useState(initGameContext.selectedGridIndex)

  const boardSize = useMemo(() => Math.min(gameWidth, gameHeight), [gameWidth, gameHeight])

  window.addEventListener('resize', () => {
    setGameWidth(window.innerWidth * config.widthScale)
    setGameHeight(window.innerHeight * config.heightScale)
  })

  const getCurrentSelectedChessPiece = () => {
    if (!gameState) return null
    if (selectedGridIndex !== -1) {
      const rowIndex = Math.floor(selectedGridIndex / 8)
      const colIndex = selectedGridIndex % 8
      return gameState.states[rowIndex][colIndex]
    }
    return null
  }

  const [gameId, startGame] = useState(initGameContext.gameId)
  const gameWsUrl = useMemo(() => ws.game(gameId), [gameId])
  const { sendMessage, lastMessage, readyState } = useWebSocket(gameWsUrl)

  useEffect(() => {
    if (readyState == ReadyState.OPEN) {
      const message = JSON.stringify({
        message: 'JSESSIONID',
        data: getSessionIdFromLS()
      })
      console.log(message)
      sendMessage(message)
    }
  }, [readyState])

  useEffect(() => {
    console.log(lastMessage)
    if (!lastMessage) return
    const json = JSON.parse(lastMessage?.data) as GameSocketMessage
    const data = json.data
    if (data) {
      if (data.rawGameState) setRawGameState(data.rawGameState)
    }
  }, [lastMessage])

  const gameState = useMemo(() => convertRawToGameState(rawGameState), [rawGameState])

  const doMove = (move: GameAction) => {
    sendMessage(
      JSON.stringify({
        message: 'Move',
        data: {
          from: {
            row: move.from.y,
            col: move.from.x
          },
          to: {
            row: move.to.y,
            col: move.to.x
          }
        }
      })
    )
  }

  return (
    <GameContext.Provider
      value={{
        gameId,
        startGame,
        gameWidth,
        gameHeight,
        rawGameState,
        setRawGameState,
        boardSize,
        selectedGridIndex,
        setSelectedGridIndex,
        gameState,
        getCurrentSelectedChessPiece,
        doMove
      }}
    >
      {children}
    </GameContext.Provider>
  )
}

export const useGame = () => {
  const context = useContext(GameContext)

  if (context === undefined) {
    throw new Error('useGame must be used within GameProvider')
  }

  return context
}
