import { Container } from "@pixi/react"
import { useMemo } from "react"
import GameContextProps from "../interfaces/gameContext.interface"
import BoardGridComponent from "./BoardGridComponent"
import ChessPieceComponent from "./ChessPieceComponent"
import { ChessPieceType } from "../types/game.type"

interface BoardComponentProps extends GameContextProps {
    width: number,
    height: number
}

export default function BoardComponent(props: BoardComponentProps) {
    const gridWidth = useMemo(() => props.width / 8, [props.width])
    const gridHeight = useMemo(() => props.height / 8, [props.height])

    const game = props.game;
    const currentSelectedPiece = game.getCurrentSelectedChessPiece();

    // Determine possible moves and mark corresponding squares
    const possibleMoves = currentSelectedPiece ? currentSelectedPiece.getAvailableMoves() : [];

    console.log(game.gameState)

    return (
        <Container
            x={game.gameWidth / 2}
            y={game.gameHeight / 2}
        >
            {[...Array(8 * 8).keys()].map(i =>
                <BoardGridComponent
                    game={game}
                    key={'b' + i}
                    x={Math.floor(i % 8) * gridWidth - game.boardSize / 2}
                    y={Math.floor(i / 8) * gridHeight - game.boardSize / 2}
                    gridX={i % 8}
                    gridY={Math.floor(i / 8)}
                    width={gridWidth}
                    height={gridHeight}
                    type={(i + Math.floor(i / 8)) % 2 == 0 ? 'light' : 'dark'}
                    highlightMove={possibleMoves.some(move => move.x === i % 8 && move.y === Math.floor(i / 8))}
                />
            )}
            {[...Array(8 * 8).keys()].map(i =>
                game.rawGameState[i] !== 'c' ?
                    <ChessPieceComponent
                        game={game}
                        key={'c' + i}
                        x={Math.floor(i % 8) * gridWidth - game.boardSize / 2}
                        y={Math.floor(i / 8) * gridHeight - game.boardSize / 2}
                        width={gridWidth}
                        height={gridHeight}
                        index={i}
                        isSelected={i === game.selectedGridIndex}
                        type={game.rawGameState[i] as ChessPieceType}
                    />
                    :
                    <></>
            )}
        </Container>
    );
}