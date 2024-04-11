import { createContext, useContext, useMemo, useState } from "react"
import { ReactWithChild } from "src/interface/app"
import { Chess, SQUARES, Square }  from "chess.js"
import { Dests, Key } from "chessground/types";

export enum GameType {
    PVP,
    BOT
}

export interface GameV2ContextInterface {
    gameId: string,
    startGame: (gameId: string) => void,
    core: Chess | null,
    fen: string,
    setFen: React.Dispatch<React.SetStateAction<string>>,
    lastMove: Key[],
    setLastMove: React.Dispatch<React.SetStateAction<Key[]>>,
    getMoveableDests: () => Dests,
    turn: string,
    move: (from: Key, to: Key) => void
}

const initContext: GameV2ContextInterface = {
    core: null,
    gameId: "",
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
    const [gameId, setGameId] = useState("")
    const [core, setCore] = useState<Chess | null>(null)
    const [fen, setFen] = useState("")
    const [lastMove, setLastMove] = useState<Key[]>([])

    const startGame = (gameId: string) => {
        setGameId(gameId)
        setCore(new Chess())
    }

    const getMoveableDests = () => {
        const dests = new Map()
        if(!core)
            return dests
        SQUARES.forEach(s => {
          const ms = core.moves({ square: s, verbose: true })
          if (ms.length) dests.set(s, ms.map(m => m.to))
        })
        return dests
    }

    const turn = useMemo(() => core ? core.turn() : "white", [fen])

    const move = (from: Key, to: Key) => {
        if(!core)
            return
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
            setFen(core.fen())
            setLastMove([from, to])
            // setTimeout(randomMove, 500)
        }
    }

    return (
        <GameV2Context.Provider value={{
            gameId,
            startGame,
            core,
            fen,
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