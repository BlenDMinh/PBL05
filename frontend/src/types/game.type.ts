import { HumanPlayer, Player } from './player.type'
import { User } from './users.type'
import { PaginitionResponse } from './utils.type'

export type GameSocketMessage = {
  message: string
  data: any
}

export type GameCreatedMessage = {
  message: string
  data: string
}

export type GameInvitationRequestMessage = {
  message: string
  data: {
    opponentId: 99
    gameRule?: {
      id: number
      ruleName: string
      minutePerTurn: number
      totalMinutePerPlayer: number
      turnAroundStep: number
      turnAroundPlusTime: number
    }
    meSide: 'WHITE' | 'BLACK' | 'RANDOM'
    rated: boolean
    invitationId: string
  }
}

export type GameResult = {
  id: string
  player1Id: number
  player2Id: number
  status: 'WHITE_WIN' | 'BLACK_WIN' | 'DRAW'
  createdAt: string
}

export interface GameHistoryResponse extends PaginitionResponse {
  games: GameResult[]
}

export type GameRuleset = {
  id: number
  name: string
  detail: {
    minute_per_turn: number
    total_minute_per_player: number
    turn_around_steps: number
    turn_around_time_plus: number
  }
  description?: {
    title?: string
    content?: string
  }
  published: boolean
  createdAt: string
}

export type GameLog = {
  "id": number,
  "fen": string,
  "message": {
    "message": string,
    "data": {
      "from": string,
      "to": string
    }
  },
  "playerId": number,
  "gameId": string,
  "createdAt": string
}

export type GameLogResponse = {
  game: {
    "id": string,
    "status": string,
    "whiteId": number,
    "blackId": number,
    "createdAt": string,
    "ruleSetDto": GameRuleset
  },
  whitePlayer: HumanPlayer,
  blackPlayer: HumanPlayer,
  gameLogs: GameLog[]
}