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
  resignSide?: boolean
  moveHistories?: MoveHistory[]
  whiteRemainMillis?: number
  blackRemainMillis?: number
}

export type MoveHistory = {
  from: Key
  to: Key
  promotion: string
  piece: string
}
