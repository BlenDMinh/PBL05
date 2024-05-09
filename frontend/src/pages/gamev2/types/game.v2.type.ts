import { Key } from 'chessground/types'
import { BotDifficulty } from 'src/types/player.type'

export type GameV2SocketData = {
  fen: string
  white: boolean
  gamePlayer?: {
    id?: number
    displayName?: string
    avatarUrl?: string
    elo?: number
    difficulty?: BotDifficulty
    white: boolean
  }
  gameStatus: 'WAITING' | 'PLAYING' | 'WHITE_WIN' | 'BLACK_WIN' | 'DRAW'
  gameRule?: GameRule
  resignSide?: boolean
  moveHistories?: MoveHistory[]
  whiteRemainMillis?: number
  blackRemainMillis?: number
  whiteRemainMillisInTurn?: number
  blackRemainMillisInTurn?: number
}

export type MoveHistory = {
  from: Key
  to: Key
  promotion: string
  piece: string
}

export type GameRule = {
  id: number
  ruleName: string
  minutePerTurn: number
  totalMinutePerPlayer: number
  turnAroundPlusTime: number
  turnAroundStep: number
}
