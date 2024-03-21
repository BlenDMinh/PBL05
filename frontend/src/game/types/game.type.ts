import { ChessPiece } from '../core/ChessPiece'

export type ChessPieceType = '0' | '1' | '2' | '3' | '4' | '5' | '6' | '7' | '8' | '9' | 'a' | 'b'

export type Position = {
  x: number
  y: number
}

export type GameState = {
  states: Array<Array<ChessPiece | null>>
}

export type GameAction = {
  from: Position
  to: Position
}
