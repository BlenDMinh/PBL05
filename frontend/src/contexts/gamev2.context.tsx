import { createContext, useContext, useEffect, useMemo, useState } from 'react'
import { ReactWithChild } from 'src/interface/app'
import { Chess, SQUARES, Square } from 'chess.js'
import { Dests, Key } from 'chessground/types'
import { ws } from 'src/constants/ws'
import useWebSocket, { ReadyState } from 'react-use-websocket'
import { getProfileFromLS, getSessionIdFromLS } from 'src/utils/auth'
import { GameV2SocketData, MoveHistory } from 'src/pages/gamev2/types/game.v2.type'
import { Player } from 'src/types/player.type'
import { bool, boolean } from 'yup'
interface SquareStyle {
  background: string
  borderRadius?: string
}
type Colour = string
export enum GameType {
  PVP,
  BOT
}

export enum GameResult {
  UNKNOWN,
  WIN,
  LOSE,
  DRAW
}

export interface GameConfig {
  botDifficulty: number
}

export const DEFAULT_CONFIG: GameConfig = {
  botDifficulty: 1
}

export interface GameV2ContextInterface {
  gameId: string
  startGame: (gameId: string, gameType?: GameType, gameConfig?: GameConfig) => void
  core: Chess | null
  fen: string
  side: 'white' | 'black' | undefined
  setFen: React.Dispatch<React.SetStateAction<string>>
  lastMove: Key[]
  moveHistories: MoveHistory[]
  setLastMove: React.Dispatch<React.SetStateAction<Key[]>>
  getMoveableDests: () => Dests
  turn: 'white' | 'black' | undefined
  move: (from: Key, to: Key) => void
  isPromoting: boolean
  promote: (piece?: 'q' | 'r' | 'b' | 'n' | null) => void
  result: GameResult
  opponent: Player | undefined
  resign: () => void
  onEnd: () => void
  onSquareClick: (square: Square) => void
  rightClickedSquares: {
    [key in Square]?: { backgroundColor: Colour } | undefined
  }
  optionSquares: any
  moveSquares: any
}

const initContext: GameV2ContextInterface = {
  core: null,
  gameId: '',
  side: undefined,
  startGame: () => null,
  fen: '',
  setFen: () => null,
  lastMove: [],
  moveHistories: [],
  setLastMove: () => null,
  getMoveableDests: () => new Map(),
  turn: undefined,
  move: () => null,
  isPromoting: false,
  promote: () => null,
  result: GameResult.UNKNOWN,
  opponent: undefined,
  resign: () => null,
  onEnd: () => null,
  onSquareClick: () => null,
  rightClickedSquares: {},
  optionSquares: {},
  moveSquares: {},
}

export const GameV2Context = createContext<GameV2ContextInterface>(initContext)

export default function GameV2ContextProvider({ children }: ReactWithChild) {
  const [gameType, setGameType] = useState(GameType.PVP)
  const [gameId, setGameId] = useState('')
  const [fen, setFen] = useState('rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1')
  const [core, setCore] = useState<Chess | null>(new Chess())
  const [side, setSide] = useState<'black' | 'white' | undefined>(undefined)
  const [lastMove, setLastMove] = useState<Key[]>([])
  const [moveHistories, setMoveHistories] = useState<MoveHistory[]>(initContext.moveHistories)
  const [isPromoting, setPromoting] = useState(false)
  const [pendeingMove, setPendingMove] = useState<Key[]>([])
  const [turn, setTurn] = useState<'white' | 'black' | undefined>(undefined)
  const [result, setResult] = useState(initContext.result)
  const [config, setConfig] = useState(DEFAULT_CONFIG)
  const [opponent, setOpponent] = useState(initContext.opponent)

  const startGame = (gameId: string, gameType: GameType = GameType.PVP, gameConfig: GameConfig = DEFAULT_CONFIG) => {
    setGameType(gameType)
    setConfig(config)
    setGameId(gameId)
    setResult(GameResult.UNKNOWN)
  }

  useEffect(() => {
    if (fen != core?.fen()) {
      const core = new Chess(fen)
      setCore(core)
    }
    if (core?.isGameOver() && side) {
      if (turn == side) {
        setResult(GameResult.LOSE)
      } else if (turn != side) {
        setResult(GameResult.WIN)
      } else {
        setResult(GameResult.DRAW)
      }
    }
  }, [fen])

  const wsUrl = useMemo(
    () =>
      gameType == GameType.PVP
        ? ws.pvpgamev2(gameId, getSessionIdFromLS())
        : ws.botgamev2(gameId, getSessionIdFromLS()),
    [gameId, gameType]
  )

  const { sendMessage, lastMessage, readyState } = useWebSocket(wsUrl)

  useEffect(() => {
    if (readyState == ReadyState.OPEN) {
      setResult(GameResult.UNKNOWN)
      if (gameType == GameType.PVP) {
        // const message = JSON.stringify({
        //   message: 'JSESSIONID',
        //   data: getSessionIdFromLS()
        // })
        // console.log(message)
        // sendMessage(message)
      } else if (gameType == GameType.BOT) {
        // const message = JSON.stringify({
        //   message: 'Human join',
        //   data: {
        //     sessionId: getSessionIdFromLS(),
        //     difficulty: config.botDifficulty
        //   }
        // })
        // console.log(message)
        // sendMessage(message)
      }
    } else {
    }
  }, [readyState])

  useEffect(() => {
    if (!lastMessage) return
    const json = JSON.parse(lastMessage?.data)
    const data = json.data as GameV2SocketData
    // console.log(data.gamePlayer.displayName + " " + getProfileFromLS().displayName)
    if (json.message === 'Player joined') {
      if (data.gamePlayer) {
        if (data.gamePlayer.id === getProfileFromLS().id) {
          setSide(data.gamePlayer.white ? 'white' : 'black')
        } else {
          setOpponent(data.gamePlayer)
        }
      }
    } else if (json.message === 'Bot joined') {
      setOpponent({ difficulty: data.gamePlayer?.difficulty })
    } else if (json.message === 'Mate') {
      //
    } else if (json.message === 'Resign') {
      if (data.resignSide != undefined) {
        const resignSide = data.resignSide ? 'white' : 'black'
        if (resignSide === side) {
          setResult(GameResult.LOSE)
        } else {
          setResult(GameResult.WIN)
        }
      }
    }

    if (data) {
      if (data.fen) setFen(data.fen)
      if (data.white != undefined) setTurn(data.white ? 'white' : 'black')
      if (data.moveHistories) {
        setMoveHistories(data.moveHistories)
        if (data.moveHistories.length > 0) {
          const lastMove = data.moveHistories[data.moveHistories.length - 1]
          setLastMove([lastMove.from.toLowerCase() as Key, lastMove.to.toLowerCase() as Key])
        }
      }
    }
  }, [lastMessage])

  useEffect(() => {
    console.log({ opponent })
  }, [opponent])

  const move = (from: Key, to: Key) => {
    if (!core) return
    if (turn !== side) {
      // setFen(core.fen())
      return
    }
    const moves = core.moves({ verbose: true })
    for (let i = 0, len = moves.length; i < len; i++) {
      /* eslint-disable-line */
      if (moves[i].flags.indexOf('p') !== -1 && moves[i].from === from) {
        setPendingMove([from, to])
        setPromoting(true)
        // setSelectVisible(true)
        return
      }
    }
    try {
      if (core.move({ from, to, promotion: 'x' })) {
        setLastMove([from, to])
        const message = {
          message: 'Move',
          data: {
            from: from,
            to: to
          }
        }
        sendMessage(JSON.stringify(message))
        setFen(core.fen())
      }
    } catch (e) {
      setFen(core.fen())
      setCore(new Chess(core.fen()))
    }
  }

  const getMoveableDests = () => {
    const dests = new Map()
    if (!core) return dests
    SQUARES.forEach((s) => {
      const ms = core.moves({ square: s, verbose: true })
      if (ms.length)
        dests.set(
          s,
          ms.map((m) => m.to)
        )
    })
    // console.log(dests)
    return dests
  }

  const promote = (piece?: 'q' | 'r' | 'b' | 'n' | null) => {
    setPromoting(false)
    if (!core) {
      return
    }
    if (!piece) {
      setFen(core.fen())
      setCore(new Chess(core.fen()))
      return
    }
    if (core.move({ from: pendeingMove[0], to: pendeingMove[1], promotion: piece })) {
      setLastMove(pendeingMove)
      const pieceStr =
        (side == 'white' ? 'WHITE_' : 'BLACK_') +
        (piece == 'b' ? 'BISHOP' : piece == 'n' ? 'KNIGHT' : piece == 'q' ? 'QUEEN' : 'ROOK')
      const message = {
        message: 'Promotion',
        data: {
          from: pendeingMove[0],
          to: pendeingMove[1],
          promotion: pieceStr
        }
      }
      sendMessage(JSON.stringify(message))
      setFen(core.fen())
    }
  }

  const resign = () => {
    sendMessage(
      JSON.stringify({
        message: 'Resign'
      })
    )
  }

  const onEnd = () => {
    setResult(GameResult.UNKNOWN)
  }

  // Chessboard
  const [moveFrom, setMoveFrom] = useState<Square | null>(null)
  const [moveTo, setMoveTo] = useState<Square | null>(null)
  const [moveSquares, setMoveSquares] = useState({})
  const [optionSquares, setOptionSquares] = useState({})
  const [rightClickedSquares, setRightClickedSquares] = useState<{
    [key in Square]?: { backgroundColor: Colour } | undefined
  }>({})
  function onSquareRightClick(square: Square) {
    const colour = 'rgba(0, 0, 255, 0.4)'
    setRightClickedSquares({
      ...rightClickedSquares,
      [square]:
        rightClickedSquares[square] && rightClickedSquares[square]?.backgroundColor === colour
          ? undefined
          : { backgroundColor: colour }
    })
  }
  function getMoveOptions(square: Square) {
    if (!core) return []
    const moves = core.moves({
      square,
      verbose: true
    })
    if (moves.length === 0) {
      setOptionSquares({})
      return false
    }

    const newSquares: { [key: string]: SquareStyle } = {}
    moves.map((move) => {
      newSquares[move.to] = {
        background:
          core.get(move.to) && core.get(move.to).color !== core.get(square).color
            ? 'radial-gradient(circle, rgba(0,0,0,.1) 85%, transparent 85%)'
            : 'radial-gradient(circle, rgba(0,0,0,.1) 25%, transparent 25%)',
        borderRadius: '50%'
      }
      return move
    })
    newSquares[square] = {
      background: 'rgba(255, 255, 0, 0.4)'
    }
    setOptionSquares(newSquares)
    return true
  }

  function onSquareClick(square: Square) {
    if (!core) return
    setRightClickedSquares({})

    // from square
    if (!moveFrom) {
      const hasMoveOptions = getMoveOptions(square)
      if (hasMoveOptions) setMoveFrom(square)
      return
    }

    // to square
    if (!moveTo) {
      // check if valid move before showing dialog
      const moves = core.moves({
        verbose: true,
        square: moveFrom
      })
      const foundMove = moves.find((m) => m.from === moveFrom && m.to === square)
      // not a valid move
      if (!foundMove) {
        // check if clicked on new piece
        const hasMoveOptions = getMoveOptions(square)
        // if new piece, setMoveFrom, otherwise clear moveFrom
        setMoveFrom(hasMoveOptions ? square : null)
        return
      }

      // valid move
      setMoveTo(square)

      // if promotion move
      if (
        (foundMove.color === 'w' && foundMove.piece === 'p' && square[1] === '8') ||
        (foundMove.color === 'b' && foundMove.piece === 'p' && square[1] === '1')
      ) {
        setPendingMove([moveFrom, square])
        setPromoting(true)
        return
      }

      // // is normal move
      const coreCopy: Chess = new Chess(core.fen())
      const move = coreCopy.move({
        from: moveFrom,
        to: square
      })

      // if invalid, setMoveFrom and getMoveOptions
      if (move === null) {
        const hasMoveOptions = getMoveOptions(square)
        if (hasMoveOptions) setMoveFrom(square)
        return
      }
      setLastMove([moveFrom, square])
      const message = {
        message: 'Move',
        data: {
          from: moveFrom,
          to: square
        }
      }
      sendMessage(JSON.stringify(message))
      setFen(core.fen())

      setMoveFrom(null)
      setMoveTo(null)
      setOptionSquares({})
      return
    }
  }

  return (
    <GameV2Context.Provider
      value={{
        gameId,
        startGame,
        core,
        fen,
        side,
        setFen,
        lastMove,
        moveHistories,
        setLastMove,
        getMoveableDests,
        turn,
        move,
        isPromoting,
        promote,
        result,
        opponent,
        resign,
        onEnd,
        onSquareClick,
        rightClickedSquares,
        optionSquares,
        moveSquares,
      }}
    >
      {children}
    </GameV2Context.Provider>
  )
}

export const useGameV2 = () => {
  const context = useContext(GameV2Context)

  if (context === undefined) {
    throw new Error('useGame must be used within GameProvider')
  }

  return context
}
