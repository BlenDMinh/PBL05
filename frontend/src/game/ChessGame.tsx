import { Container, Stage } from "@pixi/react";
import { useGame } from "./contexts/game.context";
import { useEffect } from "react";
import { SCALE_MODES, settings } from "pixi.js";
import BoardComponent from "./components/BoardComponent";

interface ChessGameProps {}

export default function ChessGame(props: ChessGameProps) {
    const game = useGame()
    settings.SCALE_MODE = SCALE_MODES.NEAREST
    return <>
        <Stage
            width={game.gameWidth}
            height={game.gameHeight}
            options={{
                antialias: true,
                resolution: window.devicePixelRatio,
            }}
        >
            <BoardComponent 
                game={game}
                width={game.boardSize}
                height={game.boardSize}
            />
        </Stage>
    </>
}