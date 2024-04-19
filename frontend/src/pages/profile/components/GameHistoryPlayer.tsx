import classNames from 'classnames'
import { useQuery } from 'react-query'
import { Link } from 'react-router-dom'
import profileApi from 'src/apis/profile.api'

export interface GameHistoryPlayerProps {
  id: number
  isWhite: boolean
  isUser?: boolean
}

export default function GameHistoryPlayer(props: GameHistoryPlayerProps) {
  const profileQuery = useQuery(['profile', props.id], () => profileApi.getProfile(props.id))
  const profile = profileQuery.data?.data
  return (
    <div className='flex w-full h-5 items-center gap-5'>
      <div
        className={classNames('rounded-full w-3 h-3 border', {
          'bg-white border-slate-600': props.isWhite,
          'bg-slate-600 border-white': !props.isWhite
        })}
      ></div>
      {profile ? (
        <div className='flex gap-5'>
          <Link to={`/profile/${profile.id}`} className='avatar w-5 h-5'>
            <div className='w-5 rounded-full'>
              <img src={profile.avatarUrl} alt='' className='mr-2 w-1/3' />
            </div>
          </Link>
          <span
            className={classNames('text-base-content', {
              'font-bold': props.isUser
            })}
          >
            {profile.displayName}
            {props.isUser ? ' (You)' : ''}
          </span>
        </div>
      ) : (
        <div className='flex gap-5 items-center'>
          <div className='bg-base-200 w-5 h-5 rounded-full skeleton'></div>
          <div className='bg-base-200 h-2 w-20 skeleton'></div>
        </div>
      )}
    </div>
  )
}
