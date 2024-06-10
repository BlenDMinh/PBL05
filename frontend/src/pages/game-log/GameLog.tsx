import classNames from 'classnames'
import { useEffect, useState } from 'react'
import { useQuery } from 'react-query'
import { useParams } from 'react-router-dom'
import gameLogApi from 'src/apis/game-log.api'
import GameProfile from '../gamev2/components/GameProfile'
import Chessground from '@react-chess/chessground'
import { blankAvatar } from 'src/assets/images'

import 'src/pages/gamev2/css/chessground.base.css'
import 'src/pages/gamev2/css/chessground.brown.css'
import 'src/pages/gamev2/css/chessground.cburnett.css'

export default function GameLog() {
  const { gameId } = useParams()
  const [groundSize, setGroundSize] = useState<number>(0)
  const gameLogQuery = useQuery(['gameLog', gameId], () => gameLogApi.getGameLog(gameId ?? ''))

  const [move, setMove] = useState<number>(0)

  useEffect(() => {
    const updateSize = () => {
      setGroundSize(Math.min(window.innerHeight * 0.67, window.innerWidth * 0.67))
    }

    // Initial update
    updateSize()

    // Attach event listeners for window resize
    window.addEventListener('resize', updateSize)

    // Cleanup function to remove event listener
    return () => {
      window.removeEventListener('resize', updateSize)
    }
  }, [])

  if (gameLogQuery.status === 'loading') {
    return (
      <>
        <div className='w-full h-full flex flex-col items-center justify-center'>
          <span className='loading loading-spinner loading-lg' />
          <span className='text-lg mt-4'>Loading...</span>
        </div>
      </>
    )
  }

  if (gameLogQuery.status === 'error' || !gameLogQuery.data?.data) {
    return (
      <>
        <div className='w-full h-full flex flex-col items-center justify-center'>
          <span className='loading loading-spinner loading-lg' />
          <span className='text-lg mt-4'>Failed to load game log</span>
        </div>
      </>
    )
  }

  const gameLog = gameLogQuery.data?.data
  const whitePlayer = gameLog?.whitePlayer
  const blackPlayer = gameLog?.blackPlayer
  const logs = gameLog?.gameLogs

  return (
    <>
      <div className='w-full flex p-16 justify-between gap-16'>
        <div
          style={{ width: `${groundSize}px` }}
          className='flex flex-col rounded-lg items-center justify-center pointer-events-none'
        >
          <GameProfile profile={blackPlayer} role='Black' gameSide={'black'} />
          {logs.length > 0 ? (
            <Chessground
              key={gameId}
              width={groundSize}
              height={groundSize}
              config={{
                turnColor: 'white',
                fen: logs[move].fen,
                orientation: 'white'
                // draggable: {
                //   enabled: true
                // },
                // movable: {
                //   free: false,
                //   dests: game.getMoveableDests(),
                //   color: game.me?.side
                // },
                // events: {
                //   move: game.move
                // },
                // lastMove: game.lastMove
              }}
            />
          ) : (
            <div>
              <span className='text-lg'>No logs available</span>
            </div>
          )}
          <GameProfile profile={whitePlayer} gameSide={'white'} role='White' />
        </div>
        <div className='w-full h-full flex flex-col items-center gap-12'>
          <div className='join flex w-full'>
            <button className='join-item btn' onClick={() => setMove(Math.max(0, move - 1))}>
              «
            </button>
            <button className='join-item btn flex-1'>Move {move + 1}</button>
            <button className='join-item btn' onClick={() => setMove(Math.min(logs.length - 1, move + 1))}>
              »
            </button>
          </div>
          <div className='flex flex-col overflow-y-auto flex-1 bg-base-300 w-full gap-5 p-5'>
            {logs.map((log, index) => (
              <button
                key={log.id}
                className={classNames('btn btn-outline flex justify-between', {
                  'btn-primary': index === move
                })}
                onClick={() => setMove(index)}
              >
                <div className='flex gap-4 items-center'>
                  <div>{index + 1}</div>
                  <div className='avatar'>
                    <div className='rounded-full w-8 h-8 bg-base-200'>
                      <img
                        src={index % 2 == 0 ? whitePlayer.avatarUrl : blackPlayer.avatarUrl ?? blankAvatar}
                        alt='avatar'
                      />
                    </div>
                  </div>
                  <span className='w-64 text-left'>
                    {index % 2 == 0 ? whitePlayer.displayName : blackPlayer.displayName}
                  </span>
                  {log.message.data && log.message.data.from && (
                    <span className='text-accent'>
                      {log.message.data.from} → {log.message.data.to}
                    </span>
                  )}
                </div>
                <span className='text-sm'>{new Date(log.createdAt).toLocaleString()}</span>
              </button>
            ))}
          </div>
        </div>
      </div>
    </>
  )
}
