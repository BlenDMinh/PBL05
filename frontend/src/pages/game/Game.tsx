import { useEffect } from "react"
import { useParams } from "react-router-dom"
import ChessGame from "src/game/ChessGame"
import { useGame } from "src/game/contexts/game.context"

export default function Game() {
    const { gameId } = useParams()
    const game = useGame()

    useEffect(() => {
        game.startGame(gameId ?? "")
    }, [gameId])

    useEffect(() => {}, [game.gameState])

    if(!game.gameState) {
        return <>Loading...</>
    }

    return <ChessGame />
}