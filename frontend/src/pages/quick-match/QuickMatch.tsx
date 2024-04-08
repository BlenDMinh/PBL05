import { useEffect, useState } from "react"
import { ws } from "src/game/constants/ws"
import useWebSocket, { ReadyState } from 'react-use-websocket';
import { getSessionIdFromLS } from "src/utils/auth";
import { GameSocketMessage } from "src/game/types/ws.type";

enum State {
    START_FINDING,
    FINDING,
    GAME_FOUND,
    ERROR
}

export default function QuickMatch() {
    const [state, setState] = useState(State.START_FINDING)
    const [isError, setIsError] = useState(false)
    const [wsUrl, setWsUrl] = useState(ws.findOpponent)
    const { sendMessage, lastMessage, readyState } = useWebSocket(wsUrl)

    useEffect(() => {
        console.log(ReadyState[readyState])
        if(readyState != ReadyState.OPEN)
            return;
        const sessionId = getSessionIdFromLS()
        if(!sessionId)
            return;
        sendMessage(JSON.stringify({
            "message": "JSESSIONID",
            "data": sessionId
        }))
    }, [readyState]);

    useEffect(() => {
        if(!lastMessage)
            return
        const message = lastMessage.data
        const json = JSON.parse(message) as GameSocketMessage
        if(json.message == 'Finding')
            setState(State.FINDING)
        if(json.message == 'Error')
            setState(State.ERROR)

        if(json.message == "Game created") {
            setState(State.GAME_FOUND)
            const gameId = json.data
            // Navigate to game

        }
        console.log(lastMessage)
    }, [lastMessage])

    if(state == State.START_FINDING) {
        return <>Connecting to server...</>
    }

    if(state == State.FINDING) {
        return <>Finding opponent...</>
    }

    if(state == State.GAME_FOUND) {
        return <>Game found</>
    }

    return <></>
}