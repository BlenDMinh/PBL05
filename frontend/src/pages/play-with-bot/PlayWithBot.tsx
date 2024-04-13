import { useEffect, useState } from "react"
import { useNavigate } from "react-router-dom"
import useWebSocket, { ReadyState } from "react-use-websocket"
import { ws } from "src/constants/ws"
import { GameCreatedMessage } from "src/types/game.type"
import { getSessionIdFromLS } from "src/utils/auth"

export default function PlayWithBot() {
    const [wsUrl, setWsUrl] = useState(ws.findBot(getSessionIdFromLS()))
    const socket = useWebSocket(wsUrl)
    const [side, setSide] = useState<"random" | "white" | "black">("random")
    const [difficulty, setDifficulty] = useState(3)
    const [isCreating, setCreating] = useState(false)

    useEffect(() => {

    }, [socket.readyState])

    const createGame = () => {
        setCreating(true)
        socket.sendJsonMessage({
            "message": "Bot config",
            "data":{
                "side": side,
                "difficulty": difficulty
            }
        })
    }

    const navigate = useNavigate()

    useEffect(() => {
        const json = socket.lastJsonMessage as GameCreatedMessage
        if(json) {
            const id = json.data
            if(id)
                navigate(`/game/v2/bot/${id}`)
        }
    }, [socket.lastJsonMessage])

    if(socket.readyState == ReadyState.CLOSED)
        return <>
            <div className="w-full h-full flex flex-col items-center justify-center">
                <span className="loading loading-lg loading-spinner"></span>
                <span className="text-base-content font-bold text-xl">An error has occured</span>
                <button className="link link-primary font-bold" onClick={() => window.location.reload()}>Refresh</button>
            </div>
        </>
    if(socket.readyState != ReadyState.OPEN)
        return <>
            <div className="w-full h-full flex flex-col items-center justify-center">
                <span className="loading loading-lg loading-spinner"></span>
                <span className="text-base-content font-bold text-xl">Connecting to server</span>
            </div>
        </>

    return <>
        <div className="w-full h-full flex flex-col items-center justify-center">
            <span className="text-base-content font-bold text-xl">Bot config</span>
            <table className="w-1/3 table">
                <tr>
                    <td>
                        <span className="text-base-content font-bold text-lg">Side</span>
                    </td>
                    <td>
                        <select value={side} className="select select-primary" onChange={(e) => setSide(e.target.value as "random" | "white" | "black")}>
                            <option value="random">Random</option>
                            <option value="white">White</option>
                            <option value="black">Black</option>
                        </select>
                    </td>
                </tr>
                <tr>
                    <td>
                        <span className="text-base-content font-bold text-lg">Bot Difficulty</span>
                    </td>
                    <td>
                        <input type="range" min="1" max="5" value={difficulty} className="range range-primary" step="1" onChange={(e) => setDifficulty(parseInt(e.target.value))} />
                        <div className="w-full flex justify-between text-xs px-2">
                            <span>1</span>
                            <span>2</span>
                            <span>3</span>
                            <span>4</span>
                            <span>5</span>
                        </div>
                    </td>
                </tr>
            </table>
            <button className="btn btn-primary w-1/3 mt-10 text-primary-content font-bold text-xl" onClick={createGame}>Start</button>
            {
                isCreating ?
                <div className="mt-10 h-24 flex flex-col items-center gap-5">
                    <span className="text-base-content font-bold">Creating game</span>
                    <span className="loading loading-spinner"></span>
                </div>
                : 
                <div className="mt-10 h-24 flex flex-col items-center gap-5">
                    {/* <span className="text-base-content font-bold">Creating game</span>
                    <span className="loading loading-spinner"></span> */}
                </div>
            }
        </div>
    </>
}