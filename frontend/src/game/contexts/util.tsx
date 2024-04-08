import { ChessPiece } from "../core/ChessPiece";
import { ChessPieceType, GameState } from "../types/game.type";

export const convertRawToGameState = (rawGameState: string | null): GameState | null => {
    if(!rawGameState)
        return null
    const states: Array<Array<ChessPiece | null>> = [];
    for (let i = 0; i < 8; i++) {
        const row: Array<ChessPiece | null> = [];
        for (let j = 0; j < 8; j++) {
            const pieceType: ChessPieceType = rawGameState.charAt(i * 8 + j) as ChessPieceType;
            if (pieceType !== 'c' as ChessPieceType) {
                // Pushing null for now, we'll assign the actual ChessPiece instance later
                row.push(null);
            } else {
                row.push(null); // Empty square
            }
        }
        states.push(row);
    }

    // Construct GameState object after iterating through the rawGameState
    const gameState: GameState = {
        states: states
    };

    // Assign the actual ChessPiece instances based on the constructed GameState
    for (let i = 0; i < 8; i++) {
        for (let j = 0; j < 8; j++) {
            const pieceType: ChessPieceType = rawGameState.charAt(i * 8 + j) as ChessPieceType;
            if (pieceType !== 'c' as ChessPieceType) {
                const piece = ChessPiece.createPiece(pieceType, { x: j, y: i }, gameState);
                gameState.states[i][j] = piece;
            }
        }
    }

    return gameState;
};