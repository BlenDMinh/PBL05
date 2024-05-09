import { createContext, useContext, useEffect, useMemo, useState } from 'react'
import { ReactWithChild } from 'src/interface/app'
import { Chess, SQUARES, Square } from 'chess.js'
import { Dests, Key } from 'chessground/types'
import { ws } from 'src/constants/ws'
import useWebSocket, { ReadyState } from 'react-use-websocket'
import { getProfileFromLS, getSessionIdFromLS } from 'src/utils/auth'
import { GameRule, GameV2SocketData, MoveHistory } from 'src/pages/gamev2/types/game.v2.type'
import { BotPlayer, HumanPlayer, Player } from 'src/types/player.type'

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
  me: HumanPlayer | undefined
  opponent: Player | undefined
  resign: () => void
  onEnd: () => void
  isReceiveFromServer: boolean
  gameRule: GameRule | null
  firstMoveDone: () => boolean
}

const initContext: GameV2ContextInterface = {
  core: null,
  gameId: '',
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
  me: undefined,
  opponent: undefined,
  resign: () => null,
  onEnd: () => null,
  isReceiveFromServer: false,
  gameRule: null,
  firstMoveDone: () => false
}

export const GameV2Context = createContext<GameV2ContextInterface>(initContext)

export const FEN_DEFAULT = 'rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1'

export default function GameV2ContextProvider({ children }: ReactWithChild) {
  const [gameType, setGameType] = useState(GameType.PVP)
  const [gameId, setGameId] = useState('')
  const [fen, setFen] = useState(FEN_DEFAULT)
  const [core, setCore] = useState<Chess | null>(new Chess())
  const [lastMove, setLastMove] = useState<Key[]>([])
  const [moveHistories, setMoveHistories] = useState<MoveHistory[]>(initContext.moveHistories)
  const [isPromoting, setPromoting] = useState(false)
  const [pendeingMove, setPendingMove] = useState<Key[]>([])
  const [turn, setTurn] = useState<'white' | 'black' | undefined>(initContext.turn)
  const [result, setResult] = useState(initContext.result)
  const [config, setConfig] = useState(DEFAULT_CONFIG)
  const [me, setMe] = useState<HumanPlayer | undefined>(initContext.me)
  const [opponent, setOpponent] = useState<Player | undefined>(initContext.opponent)
  const [isReceiveFromServer, setIsReceiveFromServer] = useState<boolean>(initContext.isReceiveFromServer)
  const [gameRule, setGameRule] = useState<GameRule | null>(initContext.gameRule)

  const firstMoveDone = () => moveHistories.length > 0

  const startGame = (gameId: string, gameType: GameType = GameType.PVP, gameConfig: GameConfig = DEFAULT_CONFIG) => {
    setGameType(gameType)
    setConfig(config)
    setGameId(gameId)
  }

  useEffect(() => {
    if (fen != core?.fen()) {
      const core = new Chess(fen)
      setCore(core)
    }
  }, [fen])

  useEffect(() => {
    if (core?.isGameOver() && me) {
      if (core.isDraw()) {
        setResult(GameResult.DRAW)
      } else if (turn === me.side) {
        setResult(GameResult.LOSE)
      } else if (turn != me.side) {
        setResult(GameResult.WIN)
      }
    }
  }, [core])

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

  const getGameRuleMillisPerTurn = () => {
    if (gameRule && gameRule.minutePerTurn && gameRule.minutePerTurn != -1) {
      return gameRule.minutePerTurn * 60 * 1000
    }
    return null
  }

  useEffect(() => {
    if (!lastMessage) return
    const json = JSON.parse(lastMessage?.data)
    console.log(json)
    const data = json.data as GameV2SocketData
    if (json.message === 'Player joined') {
      setIsReceiveFromServer(true)
      if (data.gameRule) {
        setGameRule(data.gameRule)
      }
      if (data.gamePlayer) {
        const player = data.gamePlayer
        if (player.id === getProfileFromLS().id) {
          setMe({
            id: player.id,
            avatarUrl: player.avatarUrl,
            displayName: player.displayName!,
            elo: player.elo!,
            side: player.white ? 'white' : 'black',
            remainMillis: player.white ? data.whiteRemainMillis : data.blackRemainMillis,
            remainMillisInTurn: player.white ? data.whiteRemainMillisInTurn : data.blackRemainMillisInTurn
          })
        } else {
          const player = data.gamePlayer
          const human: HumanPlayer = {
            id: player.id!,
            side: player.white ? 'white' : 'black',
            displayName: player.displayName!,
            avatarUrl: player.avatarUrl!,
            elo: player.elo!,
            remainMillis: player.white ? data.whiteRemainMillis : data.blackRemainMillis,
            remainMillisInTurn: player.white ? data.whiteRemainMillisInTurn : data.blackRemainMillisInTurn
          }
          setOpponent(human)
        }
      }
      switch (data.gameStatus) {
        case 'WHITE_WIN':
          if (me?.side === 'white') setResult(GameResult.WIN)
          else if (me?.side === 'black') setResult(GameResult.LOSE)
          break
        case 'BLACK_WIN':
          if (me?.side === 'black') setResult(GameResult.WIN)
          else if (me?.side === 'white') setResult(GameResult.LOSE)
          break

        default:
          break
      }
    } else if (json.message === 'Bot joined') {
      const bot: BotPlayer = {
        difficulty: data.gamePlayer?.difficulty!,
        side: data.gamePlayer?.white ? 'white' : 'black'
      }
      setOpponent(bot)
    } else if (json.message === 'Draw') {
      setResult(GameResult.DRAW)
    } else if (json.message === 'Resign') {
      if (data.resignSide != undefined) {
        const resignSide = data.resignSide ? 'white' : 'black'
        if (resignSide === me!.side) {
          setResult(GameResult.LOSE)
        } else {
          setResult(GameResult.WIN)
        }
      }
    } else if (json.message === 'Time up') {
      if (data.white === (me!.side === 'white')) {
        setResult(GameResult.LOSE)
      } else {
        setResult(GameResult.WIN)
      }
    }
    if (data) {
      if (data.whiteRemainMillis && data.blackRemainMillis && me && opponent) {
        if (me.side === 'white')
          setMe({
            ...me,
            remainMillis: data.whiteRemainMillis,
            remainMillisInTurn: data.whiteRemainMillisInTurn ?? getGameRuleMillisPerTurn()
          } as HumanPlayer)
        else
          setMe({
            ...me,
            remainMillis: data.blackRemainMillis,
            remainMillisInTurn: data.blackRemainMillisInTurn ?? getGameRuleMillisPerTurn()
          } as HumanPlayer)

        if (opponent.side === 'white')
          setOpponent({
            ...opponent,
            remainMillis: data.whiteRemainMillis,
            remainMillisInTurn: data.whiteRemainMillisInTurn ?? getGameRuleMillisPerTurn()
          } as HumanPlayer)
        else
          setOpponent({
            ...opponent,
            remainMillis: data.blackRemainMillis,
            remainMillisInTurn: data.blackRemainMillisInTurn ?? getGameRuleMillisPerTurn()
          } as HumanPlayer)
      }
      if (data.fen) setFen(data.fen)
      if (data.white != undefined) setTurn(data.white ? 'white' : 'black')
      if (data.moveHistories) {
        setMoveHistories(data.moveHistories)
        if (data.moveHistories.length > 0) {
          const lastMove = data.moveHistories[data.moveHistories.length - 1]
          setLastMove([lastMove.from.toLowerCase() as Key, lastMove.to.toLowerCase() as Key])
        } else {
          setLastMove([])
        }
      }
    }
  }, [lastMessage])

  const move = (from: Key, to: Key) => {
    if (!core) return
    if (turn !== me!.side) {
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
        (me!.side == 'white' ? 'WHITE_' : 'BLACK_') +
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

  return (
    <GameV2Context.Provider
      value={{
        gameId,
        startGame,
        core,
        fen,
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
        me,
        opponent,
        resign,
        onEnd,
        isReceiveFromServer,
        gameRule,
        firstMoveDone
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
