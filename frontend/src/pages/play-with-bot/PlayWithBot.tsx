import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import useWebSocket, { ReadyState } from 'react-use-websocket'
import { ws } from 'src/constants/ws'
import { GameCreatedMessage } from 'src/types/game.type'
import { getSessionIdFromLS } from 'src/utils/auth'

import 'src/pages/gamev2/css/chessground.base.css'
import 'src/pages/gamev2/css/chessground.brown.css'
import 'src/pages/gamev2/css/chessground.cburnett.css'
import Chessground from '@react-chess/chessground'
import classNames from 'classnames'
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import { faChessKing, faQuestionCircle } from '@fortawesome/free-solid-svg-icons'

export default function PlayWithBot() {
  const [wsUrl, setWsUrl] = useState(ws.findBot(getSessionIdFromLS()))
  const socket = useWebSocket(wsUrl)
  const [side, setSide] = useState<'random' | 'white' | 'black'>('random')
  const [difficulty, setDifficulty] = useState<'EASIEST' | 'EASY' | 'MEDIUM' | 'HARD' | 'HARDEST'>('MEDIUM')
  const [isCreating, setCreating] = useState(false)

  useEffect(() => {}, [socket.readyState])

  const createGame = () => {
    setCreating(true)
    socket.sendJsonMessage({
      message: 'Bot config',
      data: {
        side: side === 'white' ? 'black' : side === 'black' ? 'white' : 'random',
        difficulty: difficulty
      }
    })
  }

  const navigate = useNavigate()

  useEffect(() => {
    const json = socket.lastJsonMessage as GameCreatedMessage
    if (json) {
      const id = json.data
      if (id) navigate(`/game/v2/bot/${id}`)
    }
  }, [socket.lastJsonMessage])

  if (socket.readyState == ReadyState.CLOSED)
    return (
      <>
        <div className='w-full h-full flex flex-col items-center justify-center'>
          <span className='loading loading-lg loading-spinner'></span>
          <span className='text-base-content font-bold text-xl'>An error has occured</span>
          <button className='link link-primary font-bold' onClick={() => window.location.reload()}>
            Refresh
          </button>
        </div>
      </>
    )
  if (socket.readyState != ReadyState.OPEN)
    return (
      <>
        <div className='w-full h-full flex flex-col items-center justify-center'>
          <span className='loading loading-lg loading-spinner'></span>
          <span className='text-base-content font-bold text-xl'>Connecting to server</span>
        </div>
      </>
    )

  return (
    <div className='flex w-full h-screen items-center flex-row-reverse px-32 pt-32 pb-16 gap-12'>
      <div
        className={classNames(
          `flex-1 min-w-[${Math.min(window.innerWidth, window.innerHeight) * 0.8}px] min-h-[${
            Math.min(window.innerWidth, window.innerHeight) * 0.8
          }px] h-full rounded-lg border-2 border-base-300 pointer-events-none`
        )}
      >
        <Chessground
          width={Math.min(window.innerWidth, window.innerHeight) * 0.7}
          height={Math.min(window.innerWidth, window.innerHeight) * 0.7}
          config={{
            orientation: side === 'white' ? 'white' : 'black'
          }}
        />
      </div>
      <div className='w-full h-full flex flex-col items-center justify-center'>
        <span className='text-base-content font-bold text-xl'>GAME SETTING</span>
        <table className='w-1/3 table'>
          <tr>
            <td>
              <span className='text-base-content font-bold text-lg'>Your side to move</span>
            </td>
            <td className='flex gap-2'>
              <div
                className={classNames(
                  'cursor-pointer rounded-md w-12 h-12 flex justify-center items-center bg-gray-800',
                  {
                    'outline outline-green-600': side === 'white'
                  }
                )}
                onClick={() => setSide('white')}
              >
                <FontAwesomeIcon size='2x' icon={faChessKing} color='white'></FontAwesomeIcon>
              </div>
              <div
                className={classNames('cursor-pointer rounded-md w-12 h-12 flex justify-center items-center', {
                  'outline outline-green-600': side === 'random'
                })}
                onClick={() => setSide('random')}
              >
                <FontAwesomeIcon size='2x' icon={faQuestionCircle}></FontAwesomeIcon>
              </div>
              <div
                className={classNames('cursor-pointer rounded-md w-12 h-12 flex justify-center items-center bg-white', {
                  'outline outline-green-600': side === 'black'
                })}
                onClick={() => setSide('black')}
              >
                <FontAwesomeIcon size='2x' icon={faChessKing} color='black'></FontAwesomeIcon>
              </div>
            </td>
          </tr>
          <tr>
            <td>
              <span className='text-base-content font-bold text-lg'>Difficulty</span>
            </td>
            <td>
              <select
                value={difficulty}
                className='select select-primary'
                onChange={(e) => setDifficulty(e.target.value as 'EASIEST' | 'EASY' | 'MEDIUM' | 'HARD' | 'HARDEST')}
              >
                <option value='EASIEST'>EASIEST</option>
                <option value='EASY'>EASY</option>
                <option value='MEDIUM'>MEDIUM</option>
                <option value='HARD'>HARD</option>
                <option value='HARDEST'>HARDEST</option>
              </select>
            </td>
          </tr>
          <tr>
            <td></td>
            <td>
              <div className='rating'>
                <input
                  type='radio'
                  name='rating-2'
                  className='mask mask-star-2 bg-orange-400'
                  checked={difficulty === 'EASIEST'}
                  onClick={() => setDifficulty('EASIEST')}
                />
                <input
                  type='radio'
                  name='rating-2'
                  className='mask mask-star-2 bg-orange-400'
                  checked={difficulty === 'EASY'}
                  onClick={() => setDifficulty('EASY')}
                />
                <input
                  type='radio'
                  name='rating-2'
                  className='mask mask-star-2 bg-orange-400'
                  checked={difficulty === 'MEDIUM'}
                  onClick={() => setDifficulty('MEDIUM')}
                />
                <input
                  type='radio'
                  name='rating-2'
                  className='mask mask-star-2 bg-orange-400'
                  checked={difficulty === 'HARD'}
                  onClick={() => setDifficulty('HARD')}
                />
                <input
                  type='radio'
                  name='rating-2'
                  className='mask mask-star-2 bg-orange-400'
                  checked={difficulty === 'HARDEST'}
                  onClick={() => setDifficulty('HARDEST')}
                />
              </div>
            </td>
          </tr>
        </table>
        <button className='btn btn-primary w-1/3 mt-10 text-primary-content font-bold text-xl' onClick={createGame}>
          Start
        </button>
        {isCreating ? (
          <div className='mt-10 h-24 flex flex-col items-center gap-5'>
            <span className='text-base-content font-bold'>Creating game</span>
            <span className='loading loading-spinner'></span>
          </div>
        ) : (
          <div className='mt-10 h-24 flex flex-col items-center gap-5'>
            {/* <span className="text-base-content font-bold">Creating game</span>
                    <span className="loading loading-spinner"></span> */}
          </div>
        )}
      </div>
    </div>
  )
}
