import Chessground from "@react-chess/chessground";
import { useEffect } from "react";
import { useParams } from "react-router-dom";
import { useGameV2 } from "src/contexts/gamev2.context";

import "src/pages/gamev2/chessground.base.css"
import "src/pages/gamev2/chessground.brown.css"
import "src/pages/gamev2/chessground.cburnett.css"

export default function GameV2() {
    const { gameId } = useParams()
    const game = useGameV2()
    useEffect(() => {
        game.startGame(gameId ?? "")
    }, [gameId])

    if(!game.core)
        return <>Loading...</>
    
    return <>
        <Chessground config={{
            turnColor: "black",
            fen: game.fen,
            draggable: {
                enabled: false
            },
            movable: {
                free: false,
                dests: game.getMoveableDests(),
                color: "both"
            },
            events: {
                move: game.move
            },
            lastMove: game.lastMove,
        }} />
    </>
}