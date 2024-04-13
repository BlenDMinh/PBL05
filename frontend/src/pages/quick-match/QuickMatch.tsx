import { useEffect, useState } from 'react'
import { ws } from 'src/constants/ws'
import useWebSocket, { ReadyState } from 'react-use-websocket'
import { getSessionIdFromLS } from 'src/utils/auth'
import { GameSocketMessage } from 'src/game/types/ws.type'
import { useNavigate } from 'react-router-dom'
import board from '../../assets/images/board.png'
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

  const navigate = useNavigate()

  useEffect(() => {
    console.log(ReadyState[readyState])
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
      navigate(`/game/v2/${gameId}`)
    }
    console.log(lastMessage)
  }, [lastMessage])

  if (state == State.START_FINDING) {
    return <>Connecting to server...</>
  }
  if (state == State.FINDING) {
    return (
      <div className='h-full w-full flex flex-col items-center justify-center relative'>
        <div style={{ position: 'relative' }}>
          <img
            src={board}
            alt='Chess Board'
            width={Math.min(window.innerWidth, window.innerHeight)}
            height={Math.min(window.innerWidth, window.innerHeight)}
            style={{ position: 'relative', zIndex: '1', boxShadow: '0 0 10px rgba(0, 0, 0, 0.2)' }}
          />
          <div className='absolute top-0 left-0 w-full h-full bg-black opacity-50' style={{ zIndex: '2' }}></div>
          <div
            className='text-3xl text-base-content'
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
