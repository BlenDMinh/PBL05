import { useContext, useEffect } from "react";
import { Outlet, useNavigate } from "react-router-dom";
import { path } from "src/constants/path";
import { AppContext, AppContextType, AuthenticateState } from "src/contexts/app.context";
import GameV2ContextProvider from "src/contexts/gamev2.context";
import GameLayout from "src/layouts/GameLayout";

export default function GameV2Routes() {
    const { isAuthenticated } = useContext<AppContextType>(AppContext)
    const navigate = useNavigate()
  
    useEffect(() => {
      if(isAuthenticated == AuthenticateState.UNKNOWN)
        return
      if (isAuthenticated != AuthenticateState.AUTHENTICATED) {
        navigate(path.login)
      }
    }, [isAuthenticated])
    return <GameLayout>
            <GameV2ContextProvider>
             <Outlet />
            </GameV2ContextProvider>
        </GameLayout>
}