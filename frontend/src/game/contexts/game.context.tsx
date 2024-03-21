import { createContext, useContext, useMemo, useState } from "react";
import { ReactWithChild } from "src/interface/app";
import { config } from "../configs/game";
import { ChessPieceType, GameAction, GameState, Position } from "../types/game.type";
import { ChessPiece } from "../core/ChessPiece";

export interface GameContextType {
    gameWidth: number;
    gameHeight: number;
    rawGameState: string;
    setRawGameState: React.Dispatch<React.SetStateAction<string>>;
    boardSize: number;
    selectedGridIndex: number;
    setSelectedGridIndex: React.Dispatch<React.SetStateAction<number>>;
    gameState: GameState;
    getCurrentSelectedChessPiece: () => ChessPiece | null;
    doMove: (move: GameAction) => void
}

const convertRawToGameState = (rawGameState: string): GameState => {
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

const initGameContext: GameContextType = {
    gameWidth: window.innerWidth * config.widthScale,
    gameHeight: window.innerHeight * config.heightScale,
    rawGameState: '789ab98766666666cccccccccccccccccccccccccccccccc0000000012345321',
    setRawGameState: () => null,
    boardSize: Math.min(window.innerWidth * config.widthScale, window.innerHeight * config.heightScale),
    selectedGridIndex: -1,
    setSelectedGridIndex: () => null,
    gameState: convertRawToGameState('789ab98766666666cccccccccccccccccccccccccccccccc0000000012345321'),
    getCurrentSelectedChessPiece: () => null, // Placeholder implementation
    doMove: () => null
};

export const GameContext = createContext<GameContextType>(initGameContext);

export default function GameContextProvider({children}: ReactWithChild) {
    const [gameWidth, setGameWidth] = useState(initGameContext.gameWidth);
    const [gameHeight, setGameHeight] = useState(initGameContext.gameHeight);
    const [rawGameState, setRawGameState] = useState(initGameContext.rawGameState);
    const [selectedGridIndex, setSelectedGridIndex] = useState(initGameContext.selectedGridIndex);

    const boardSize = useMemo(() => Math.min(gameWidth, gameHeight), [gameWidth, gameHeight]);

    window.addEventListener('resize', () => {
        setGameWidth(window.innerWidth * config.widthScale);
        setGameHeight(window.innerHeight * config.heightScale);
    });

    const gameState = useMemo(() => convertRawToGameState(rawGameState), [rawGameState]);

    const getCurrentSelectedChessPiece = () => {
        if (selectedGridIndex !== -1) {
            const rowIndex = Math.floor(selectedGridIndex / 8);
            const colIndex = selectedGridIndex % 8;
            return gameState.states[rowIndex][colIndex];
        }
        return null;
    };

    const doMove = (move: GameAction) => {
        console.log(move)

        const fromIndex = move.from.y * 8 + move.from.x
        const toIndex = move.to.y * 8 + move.to.x

        const newGameState = [...rawGameState]

        newGameState[toIndex] = rawGameState[fromIndex]
        newGameState[fromIndex] = 'c'

        setRawGameState(newGameState.join(''))
    }

    return (
        <GameContext.Provider value={{
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
        }}>
            {children}
        </GameContext.Provider>
    );
}

export const useGame = () => {
    const context = useContext(GameContext);

    if (context === undefined) {
        throw new Error('useGame must be used within GameProvider');
    }

    return context;
};