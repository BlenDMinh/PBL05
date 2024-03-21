import { Outlet } from "react-router-dom";
import ChessGame from "src/game/ChessGame";
import GameContextProvider from "src/game/contexts/game.context";
import GameLayout from "src/layouts/GameLayout";

export default function GameRoutes() {
    return <GameLayout>
            <GameContextProvider>
                <ChessGame />
            </GameContextProvider>
        </GameLayout>
}