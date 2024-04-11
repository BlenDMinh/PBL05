import { createContext, useContext, useEffect, useMemo, useState } from "react"
import { ReactWithChild } from "src/interface/app"
import { Chess, SQUARES, Square }  from "chess.js"
import { Dests, Key } from "chessground/types";
import { ws } from "src/game/constants/ws";
import useWebSocket, { ReadyState } from "react-use-websocket";
import { getProfileFromLS, getSessionIdFromLS } from "src/utils/auth";
import { GameV2SocketData } from "src/pages/gamev2/types/game.v2.type";

export enum GameType {
    PVP,
    BOT
}

export interface GameV2ContextInterface {
    gameId: string,
    startGame: (gameId: string) => void,
    core: Chess | null,
    fen: string,
    side: "white" | "black",
    setFen: React.Dispatch<React.SetStateAction<string>>,
    lastMove: Key[],
    setLastMove: React.Dispatch<React.SetStateAction<Key[]>>,
    getMoveableDests: () => Dests,
    turn: "white" | "black",
    move: (from: Key, to: Key) => void
}

const initContext: GameV2ContextInterface = {
    core: null,
    gameId: "",
    side: "white",
    startGame: () => null,
    fen: "",
    setFen: () => null,
    lastMove: [],
    setLastMove: () => null,
    getMoveableDests: () => new Map(),
    turn: "white",
    move: () => null
}

export const GameV2Context = createContext<GameV2ContextInterface>(initContext)

export default function GameV2ContextProvider({children}: ReactWithChild) {
    const [gameType, setGameType] = useState(GameType.PVP)
    const [gameId, setGameId] = useState("")
    const [fen, setFen] = useState("")
    const core = useMemo(() => fen ? new Chess(fen) : null, [fen])
    const [side, setSide] = useState<"black" | "white">("white")
    const [lastMove, setLastMove] = useState<Key[]>([])

    const startGame = (gameId: string, gameType: GameType = GameType.PVP) => {
        setGameType(gameType)
        setGameId(gameId)
    }

    const wsUrl = useMemo(() => gameType == GameType.PVP ? ws.pvpgamev2(gameId) : ws.botgamev2(gameId), [gameId, gameType])

    const { sendMessage, lastMessage, readyState } = useWebSocket(wsUrl)

    useEffect(() => {
        if(readyState == ReadyState.OPEN) {
            if(gameType == GameType.PVP) {
                const message = JSON.stringify({
                    "message": "JSESSIONID",
                    "data": getSessionIdFromLS()
                })
                console.log(message)
                sendMessage(message)
            }
            else if(gameType == GameType.BOT) {
                const message = JSON.stringify({
                    message: "Human join",                      
                    data:{                      
                        sessionId: getSessionIdFromLS(),                     
                        difficulty: 2             
                    }  
                })
                console.log(message)
                sendMessage(message)
            }
        }
    }, [readyState])

    useEffect(() => {
        if(!lastMessage)
            return;
        const json = JSON.parse(lastMessage?.data)
        console.log(json)
        const data = json.data as GameV2SocketData
        setFen(data.fen)
        // console.log(data.gamePlayer.displayName + " " + getProfileFromLS().displayName)
        if(data.gamePlayer && data.gamePlayer.displayName === getProfileFromLS().displayName) {
            // console.log(data.gamePlayer.white ? "white" : "black")
            setSide(data.gamePlayer.white ? "white" : "black")
        }
    }, [lastMessage])

    const getMoveableDests = () => {
        const dests = new Map()
        if(!core)
            return dests
        SQUARES.forEach(s => {
          const ms = core.moves({ square: s, verbose: true })
          if (ms.length) dests.set(s, ms.map(m => m.to))
        })
        // console.log(dests)
        return dests
    }

    const turn = useMemo(() => core ? (core.turn() == 'b' ? "black" : "white") : "white", [fen])

    const move = (from: Key, to: Key) => {
        if(!core)
            return
        if(turn !== side) {
            setFen(core.fen())
            return
        }
        const moves = core.moves({ verbose: true })
        console.log(moves)
        for (let i = 0, len = moves.length; i < len; i++) { /* eslint-disable-line */
            if (moves[i].flags.indexOf("p") !== -1 && moves[i].from === from) {
                // setPendingMove([from, to])
                // setSelectVisible(true)
                return
            }
        }
        
        if (core.move({ from, to, promotion: "x" })) {
            setLastMove([from, to])
            const message = {
                message: "Move",
                data: {
                    from: from,
                    to: to
                }
            }
            sendMessage(JSON.stringify(message))
        }
        setFen(core.fen())
    }

    return (
        <GameV2Context.Provider value={{
            gameId,
            startGame,
            core,
            fen,
            side,
            setFen,
            lastMove,
            setLastMove,
            getMoveableDests,
            turn,
            move
        }}>
            {children}
        </GameV2Context.Provider>
    )
}

export const useGameV2 = () => {
    const context = useContext(GameV2Context);

    if (context === undefined) {
        throw new Error('useGame must be used within GameProvider');
    }

    return context;
}