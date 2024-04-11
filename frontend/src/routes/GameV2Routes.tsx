import { Outlet } from "react-router-dom";
import GameV2ContextProvider from "src/contexts/gamev2.context";
import GameLayout from "src/layouts/GameLayout";

export default function GameV2Routes() {
    return <GameLayout>
            <GameV2ContextProvider>
             <Outlet />
            </GameV2ContextProvider>
        </GameLayout>
}