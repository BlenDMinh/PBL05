import { useEffect, useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import profileApi from 'src/apis/profile.api'
import { GameResult } from 'src/types/game.type'
import GameHistoryPlayer from './GameHistoryPlayer'
import classNames from 'classnames'
import { path } from 'src/constants/path'

export interface GameHistoryProps {
  id?: number
}

export default function GameHistory(props: GameHistoryProps) {
  const { id } = useParams()
  const userId = id ?? props.id ?? ''

  const [page, setPage] = useState(1)
  const [maxPage, setMaxPage] = useState(1)
  const [gameResults, setGameResults] = useState<GameResult[] | null>(null)

  useEffect(() => {
    profileApi.getGameHistory(userId, page).then((res) => {
      setGameResults(res.data.games)
      setMaxPage(res.data.totalPages)
    })
  }, [page, userId])

  if (!userId) {
    return (
      <div className='w-full flex justify-center bg-base-300 rounded-lg p-5'>
        <span className='font-bold text-xl'>hey no</span>
      </div>
    )
  }

  if (gameResults == null) {
    return (
      <div className='w-full flex justify-center bg-base-300 rounded-lg p-5'>
        <span className='loading loading-spinner loading-lg'></span>
      </div>
    )
  }

  if (gameResults.length == 0) {
    return (
      <div className='w-full flex justify-center bg-base-300 rounded-lg p-5'>
        <span className='font-bold text-xl'>You haven't play any game yet</span>
      </div>
    )
  }

  return (
    <div className='h-full'>
      <div className='join flex w-full justify-center mb-3'>
        <button className='join-item btn' onClick={() => setPage(Math.max(1, page - 1))}>
          «
        </button>
        <button className='join-item flex-1 btn'>Page {page}</button>
        <button className='join-item btn' onClick={() => setPage(Math.min(maxPage, page + 1))}>
          »
        </button>
      </div>
      <table className='table table-lg bg-base'>
        <thead>
          <tr>
            <th></th>
            <th>Players</th>
            <th>Result</th>
            <th>Date</th>
            <th>Action</th>
          </tr>
          {gameResults.map((res) => (
            <tr key={res.id} className='hover'>
              <td></td>
              <td className='flex flex-col gap-2 justify-center'>
                <GameHistoryPlayer id={res.player1Id} isWhite isUser={userId == res.player1Id} />
                <GameHistoryPlayer id={res.player2Id} isWhite={false} isUser={userId == res.player2Id} />
              </td>
              <td
                className={classNames('text-lg', {
                  'text-success':
                    (userId == res.player1Id && res.status == 'WHITE_WIN') ||
                    (userId == res.player2Id && res.status == 'BLACK_WIN'),
                  'text-error': !(
                    (userId == res.player1Id && res.status == 'WHITE_WIN') ||
                    (userId == res.player2Id && res.status == 'BLACK_WIN')
                  )
                })}
              >
                {(userId == res.player1Id && res.status == 'WHITE_WIN') ||
                (userId == res.player2Id && res.status == 'BLACK_WIN')
                  ? 'WIN'
                  : 'LOSE'}
              </td>
              <td>{new Date(res.createdAt + ' UTC').toUTCString()}</td>
              <td className='text-primary font-bold'>
                <Link to={path.gameLog.replace(':gameId', res.id)}>View History</Link>
              </td>
            </tr>
          ))}
        </thead>
      </table>
    </div>
  )
}
