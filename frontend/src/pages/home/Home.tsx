import { useContext } from 'react'
import { AppContext, AppContextType, AuthenticateState } from 'src/contexts/app.context'
import { Link } from 'react-router-dom'
import { FaClock, FaRobot, FaUser } from 'react-icons/fa'
import { path } from 'src/constants/path'

export default function Home() {
  const { user, isAuthenticated } = useContext<AppContextType>(AppContext)
  return (
    <div className='flex flex-col w-full h-full items-center py-10 px-16 gap-20'>
      {isAuthenticated === AuthenticateState.AUTHENTICATED && (
        <div className='flex flex-col gap-5 w-full'>
          <div className='w-full flex gap-5'>
            <Link to={`${path.profile}/${user?.id}`} className='avatar btn btn-circle btn-ghost'>
              <div className='w-20 rounded-full'>
                <img
                  alt='Tailwind CSS Navbar component'
                  src={user?.avatarUrl ?? 'https://daisyui.com/images/stock/photo-1534528741775-53994a69daeb.jpg'}
                />
              </div>
            </Link>
            <span className='text-base-content text-3xl font-bold'>{user?.displayName}</span>
          </div>
          <div className='w-full flex gap-5'>
            <span className='text-base-content text-3xl font-bold'>ELO: </span>
            <span className='text-info text-3xl font-bold'>{user?.elo}</span>
          </div>
        </div>
      )}
      <div className='flex w-full gap-10 h-96'>
        <div className='flex flex-col w-1/3 h-full justify-evenly gap-10'>
          <Link className='btn bg-base-200 w-full h-1/4 border-b-8 border-base-300' to={path.quickMatch}>
            <span className='text-base-content font-bold text-xl flex gap-2 items-center'>
              <FaClock />
              Quick Match
            </span>
          </Link>
          <Link to={path.playWithBot} className='btn bg-base-200 w-full h-1/4 border-b-8 border-base-300'>
            <span className='text-base-content font-bold text-xl flex gap-2 items-center'>
              <FaRobot />
              Play with bot
            </span>
          </Link>
          <div className='tooltip h-1/4' data-tip='Currently not available'>
            <div className='btn bg-base-200 w-full h-full border-b-8 border-base-300'>
              <span className='text-base-content font-bold text-xl flex gap-2 items-center'>
                <FaUser />
                Play with friends
              </span>
            </div>
          </div>
        </div>
        <div className='card bg-base-200 h-full w-1/3 border-b-8 border-base-300'>
          <figure>
            <img src='/puzzle.jpeg' alt='Shoes' />
          </figure>
          <div className='card-body'>
            <h2 className='card-title'>Puzzle</h2>
            <p>Solve many chess puzzles</p>
            <div className='card-actions justify-end'>
              <div className='tooltip' data-tip='Currently not available'>
                <button className='btn btn-primary btn-disabled'>Play</button>
              </div>
            </div>
          </div>
        </div>
        <div className='card bg-base-200 h-full w-1/3 border-b-8 border-base-300'>
          <figure>
            <img src='/robot.jpg' alt='Shoes' />
          </figure>
          <div className='card-body'>
            <h2 className='card-title'>Play with AI</h2>
            <p>Challenge yourself with smart Artifical Intelligent</p>
            <div className='card-actions justify-end'>
              <Link to={path.playWithBot} className='btn btn-primary'>
                Play
              </Link>
            </div>
          </div>
        </div>
      </div>
      {isAuthenticated === AuthenticateState.AUTHENTICATED && (
        <div className='w-full flex flex-col gap-5'>
          <span className='w-full text-base-content font-bold text-2xl'>Your games</span>
          <div className='w-full flex justify-center bg-base-300 rounded-lg p-5'>
            <span className='font-bold text-xl'>You haven't play any game yet</span>
          </div>
        </div>
      )}
    </div>
  )
}
