import { useEffect, useState } from 'react'
import { ws } from 'src/constants/ws'
import useWebSocket, { ReadyState } from 'react-use-websocket'
import { getSessionIdFromLS } from 'src/utils/auth'
import { useNavigate } from 'react-router-dom'
import board from '../../assets/images/board.png'
import { GameSocketMessage } from 'src/types/game.type'
import { path } from 'src/constants/path'
enum State {
  START_FINDING,
  FINDING,
  GAME_FOUND,
  ERROR
}
export default function QuickMatch() {
  const [state, setState] = useState(State.START_FINDING)
  const [isError, setIsError] = useState(false)
  const [wsUrl, setWsUrl] = useState(ws.findOpponent(getSessionIdFromLS()))
  const { sendMessage, lastMessage, readyState } = useWebSocket(wsUrl)

  const navigate = useNavigate()

  useEffect(() => {
    if (readyState != ReadyState.OPEN) return
    const sessionId = getSessionIdFromLS()
    if (!sessionId) return
    sendMessage(
      JSON.stringify({
        message: 'JSESSIONID',
        data: sessionId
      })
    )
  }, [readyState])

  useEffect(() => {
    if (!lastMessage) return
    const message = lastMessage.data
    const json = JSON.parse(message) as GameSocketMessage
    if (json.message == 'Finding') setState(State.FINDING)
    if (json.message == 'Error') setState(State.ERROR)

    if (json.message == 'Game created') {
      setState(State.GAME_FOUND)
      const gameId = json.data
      // Navigate to game
      navigate(`${path.gamev2}/${gameId}`)
    }
  }, [lastMessage])

  if (state == State.START_FINDING) {
    return <>Connecting to server...</>
  }
  if (state == State.FINDING) {
    return (
      <div className='flex flex-col items-center justify-center relative'>
        <div>
          <img className='brightness-50 shadow-2xl' src={board} alt='Chess Board' />

          <div
            className='text-3xl text-base-content whitespace-nowrap'
            style={{
              position: 'absolute',
              top: '50%',
              left: '50%',
              transform: 'translate(-50%, -50%)',
              zIndex: '3'
            }}
          >
            Finding opponent...
          </div>
        </div>
      </div>
    )
  }

  if (state == State.GAME_FOUND) {
    return <>Game found</>
  }

  return <></>
}
