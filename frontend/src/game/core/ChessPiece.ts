import { ChessPieceType, GameState, Position } from '../types/game.type'

export abstract class ChessPiece {
  public position: Position
  protected gameState: GameState
  public side: boolean

  constructor(position: Position, gameState: GameState, side: boolean) {
    this.position = position
    this.gameState = gameState
    this.side = side
  }

  abstract getAvailableMoves(): Array<Position>

  public isValidMove(x: number, y: number): boolean {
    // Check if the move is within the bounds of the board and not occupied by a piece of the same side
    if (x >= 0 && x < 8 && y >= 0 && y < 8) {
      const pieceAtDestination = this.gameState.states[y][x]
      if (pieceAtDestination === null || pieceAtDestination.side !== this.side) {
        return true
      }
    }
    return false
  }

  static createPiece(type: ChessPieceType, position: Position, gameState: GameState): ChessPiece {
    const side = /^[0-5]$/.test(type)
    switch (type.toLowerCase()) {
      case '0':
      case '6':
        return new Pawn(position, gameState, side)
      case '1':
      case '7':
        return new Rook(position, gameState, side)
      case '2':
      case '8':
        return new Knight(position, gameState, side)
      case '3':
      case '9':
        return new Bishop(position, gameState, side)
      case '4':
      case 'a':
        return new Queen(position, gameState, side)
      case '5':
      case 'b':
        return new King(position, gameState, side)
      default:
        throw new Error('Invalid chess piece type')
    }
  }
}

export class Pawn extends ChessPiece {
  constructor(position: Position, gameState: GameState, side: boolean) {
    super(position, gameState, side)
  }

  getAvailableMoves(): Array<Position> {
    const availableMoves: Array<Position> = []
    const x = this.position.x
    const y = this.position.y
    const direction = this.side ? -1 : 1 // Assuming white pawns move upwards, black pawns move downwards

    // Move forward
    if (this.isValidMove(x, y + direction)) {
      availableMoves.push({ x, y: y + direction })
      // Pawn can move two squares forward from its initial position
      if ((this.side && y === 6) || (!this.side && y === 1)) {
        if (this.isValidMove(x, y + 2 * direction)) {
          availableMoves.push({ x, y: y + 2 * direction })
        }
      }
    }

    // Capture diagonally
    if (
      this.gameState.states[y + direction] &&
      this.gameState.states[y + direction][x - 1] &&
      this.gameState.states[y + direction][x - 1]?.side !== this.side
    ) {
      console.log({ x: x - 1, y: y + direction })
      availableMoves.push({ x: x - 1, y: y + direction })
    }
    if (
      this.gameState.states[y + direction] &&
      this.gameState.states[y + direction][x + 1] &&
      this.gameState.states[y + direction][x + 1]?.side !== this.side
    ) {
      console.log({ x: x + 1, y: y + direction })
      availableMoves.push({ x: x + 1, y: y + direction })
    }

    return availableMoves
  }
}

export class Rook extends ChessPiece {
  constructor(position: Position, gameState: GameState, side: boolean) {
    super(position, gameState, side)
  }

  getAvailableMoves(): Array<Position> {
    const availableMoves: Array<Position> = []
    const x = this.position.x
    const y = this.position.y

    // Rook can move horizontally and vertically until it hits another piece or the edge of the board
    // Check horizontally
    for (let i = x - 1; i >= 0; i--) {
      if (this.isValidMove(i, y)) {
        availableMoves.push({ x: i, y })
      } else {
        break
      }
    }
    for (let i = x + 1; i < 8; i++) {
      if (this.isValidMove(i, y)) {
        availableMoves.push({ x: i, y })
      } else {
        break
      }
    }

    // Check vertically
    for (let j = y - 1; j >= 0; j--) {
      if (this.isValidMove(x, j)) {
        availableMoves.push({ x, y: j })
      } else {
        break
      }
    }
    for (let j = y + 1; j < 8; j++) {
      if (this.isValidMove(x, j)) {
        availableMoves.push({ x, y: j })
      } else {
        break
      }
    }

    return availableMoves
  }
}

export class Knight extends ChessPiece {
  constructor(position: Position, gameState: GameState, side: boolean) {
    super(position, gameState, side)
  }

  getAvailableMoves(): Array<Position> {
    const availableMoves: Array<Position> = []
    const x = this.position.x
    const y = this.position.y

    // Knight can move in an L-shape: two squares in one direction and one square perpendicular to that direction

    // Check all possible knight moves
    const possibleMoves: Array<Position> = [
      { x: x + 1, y: y + 2 },
      { x: x + 2, y: y + 1 },
      { x: x + 2, y: y - 1 },
      { x: x + 1, y: y - 2 },
      { x: x - 1, y: y - 2 },
      { x: x - 2, y: y - 1 },
      { x: x - 2, y: y + 1 },
      { x: x - 1, y: y + 2 }
    ]

    // Filter out moves that are out of bounds or occupied by a piece of the same side
    for (const move of possibleMoves) {
      if (this.isValidMove(move.x, move.y)) {
        availableMoves.push(move)
      }
    }

    return availableMoves
  }
}

export class Bishop extends ChessPiece {
  constructor(position: Position, gameState: GameState, side: boolean) {
    super(position, gameState, side)
  }

  getAvailableMoves(): Array<Position> {
    const availableMoves: Array<Position> = []
    const x = this.position.x
    const y = this.position.y

    // Bishop can move diagonally until it hits another piece or the edge of the board

    // Check diagonally (top-right direction)
    let i = x + 1
    let j = y + 1
    while (i < 8 && j < 8 && this.isValidMove(i, j)) {
      availableMoves.push({ x: i, y: j })
      if (this.gameState.states[j][i]) break
      i++
      j++
    }

    // Check diagonally (top-left direction)
    i = x - 1
    j = y + 1
    while (i >= 0 && j < 8 && this.isValidMove(i, j)) {
      availableMoves.push({ x: i, y: j })
      if (this.gameState.states[j][i]) break
      i--
      j++
    }

    // Check diagonally (bottom-right direction)
    i = x + 1
    j = y - 1
    while (i < 8 && j >= 0 && this.isValidMove(i, j)) {
      availableMoves.push({ x: i, y: j })
      if (this.gameState.states[j][i]) break
      i++
      j--
    }

    // Check diagonally (bottom-left direction)
    i = x - 1
    j = y - 1
    while (i >= 0 && j >= 0 && this.isValidMove(i, j)) {
      availableMoves.push({ x: i, y: j })
      if (this.gameState.states[j][i]) break
      i--
      j--
    }

    return availableMoves
  }
}

export class King extends ChessPiece {
  constructor(position: Position, gameState: GameState, side: boolean) {
    super(position, gameState, side)
  }

  getAvailableMoves(): Array<Position> {
    const availableMoves: Array<Position> = []
    const x = this.position.x
    const y = this.position.y

    // King can move one square in any direction

    // Check all adjacent squares
    const possibleMoves: Array<Position> = [
      { x: x + 1, y },
      { x: x + 1, y: y + 1 },
      { x, y: y + 1 },
      { x: x - 1, y: y + 1 },
      { x: x - 1, y },
      { x: x - 1, y: y - 1 },
      { x, y: y - 1 },
      { x: x + 1, y: y - 1 }
    ]

    // Filter out moves that are out of bounds or occupied by a piece of the same side
    for (const move of possibleMoves) {
      if (this.isValidMove(move.x, move.y)) {
        availableMoves.push(move)
      }
    }

    return availableMoves
  }
}

export class Queen extends ChessPiece {
  constructor(position: Position, gameState: GameState, side: boolean) {
    super(position, gameState, side)
  }

  getAvailableMoves(): Array<Position> {
    const availableMoves: Array<Position> = []
    const x = this.position.x
    const y = this.position.y

    // Queen can move in any direction (horizontally, vertically, or diagonally)
    // This can be implemented by combining the movements of a Rook and a Bishop

    // Rook-like moves
    for (let i = x - 1; i >= 0; i--) {
      if (this.isValidMove(i, y)) {
        availableMoves.push({ x: i, y })
        if (this.gameState.states[y][i]) break
      } else {
        break
      }
    }
    for (let i = x + 1; i < 8; i++) {
      if (this.isValidMove(i, y)) {
        availableMoves.push({ x: i, y })
        if (this.gameState.states[y][i]) break
      } else {
        break
      }
    }
    for (let j = y - 1; j >= 0; j--) {
      if (this.isValidMove(x, j)) {
        availableMoves.push({ x, y: j })
        if (this.gameState.states[j][x]) break
      } else {
        break
      }
    }
    for (let j = y + 1; j < 8; j++) {
      if (this.isValidMove(x, j)) {
        availableMoves.push({ x, y: j })
        if (this.gameState.states[j][x]) break
      } else {
        break
      }
    }

    // Bishop-like moves
    let i = x + 1
    let j = y + 1
    while (i < 8 && j < 8 && this.isValidMove(i, j)) {
      availableMoves.push({ x: i, y: j })
      if (this.gameState.states[j][i]) break
      i++
      j++
    }

    i = x - 1
    j = y + 1
    while (i >= 0 && j < 8 && this.isValidMove(i, j)) {
      availableMoves.push({ x: i, y: j })
      if (this.gameState.states[j][i]) break
      i--
      j++
    }

    i = x + 1
    j = y - 1
    while (i < 8 && j >= 0 && this.isValidMove(i, j)) {
      availableMoves.push({ x: i, y: j })
      if (this.gameState.states[j][i]) break
      i++
      j--
    }

    i = x - 1
    j = y - 1
    while (i >= 0 && j >= 0 && this.isValidMove(i, j)) {
      availableMoves.push({ x: i, y: j })
      if (this.gameState.states[j][i]) break
      i--
      j--
    }

    return availableMoves
  }
}
