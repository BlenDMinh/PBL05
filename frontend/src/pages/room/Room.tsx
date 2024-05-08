import { useParams } from 'react-router-dom'
import GameProfile from '../gamev2/components/GameProfile'
import Chessground from '@react-chess/chessground'
import classNames from 'classnames'
import { getProfileFromLS } from 'src/utils/auth'
import { useMemo, useState } from 'react'
import { FaLink } from 'react-icons/fa'
import InvatationModal from './components/InvatationModal'
import FriendList from './components/FriendList'

export default function Room() {
  const { id } = useParams()
  const user = getProfileFromLS()
  const [side, setSide] = useState<'white' | 'black'>('white')
  const [keyword, setKeyword] = useState('')

  const userPlayer = useMemo(() => {
    const player = { ...user, side: side, role: 'You' }
    return player
  }, [user, side])

  const openLinkModal = () => {
    const modal = document.getElementById('link-modal') as HTMLDialogElement
    if (modal) {
      modal.showModal()
    }
  }

  const calcChessGroundSize = () => Math.min(window.innerHeight * 0.67, window.innerHeight * 0.67)

  return (
    <>
      <div className='w-full h-screen pt-32 pb-16 px-32 flex items-center justify-between'>
        <div
          style={{ width: `${calcChessGroundSize()}px` }}
          className={classNames(`flex flex-col rounded-lg items-center justify-center`)}
        >
          {/* <div className='w-full flex items-center justify-between'>
                    <GameProfile profile={userPlayer} role='Opponent' gameRule={null} />
                    <div className='rounded-lg border-base-300 border-2 bg-base-200'>
                        <p className='text-lg m-2'>10:00</p>
                    </div>
                </div> */}
          <div
            className={classNames(
              `min-w-[${Math.min(window.innerWidth, window.innerHeight) * 0.8}px] min-h-[${
                Math.min(window.innerWidth, window.innerHeight) * 0.8
              }px] pointer-events-none`,
              'hidden md:block'
            )}
          >
            <Chessground
              width={calcChessGroundSize()}
              height={calcChessGroundSize()}
              config={
                {
                  // orientation: game.me?.side === 'white' ? 'white' : 'black',
                }
              }
            />
          </div>
          <div className='w-full flex items-center justify-between'>
            <GameProfile profile={userPlayer} gameRule={null} role='You' />
            <div className='rounded-lg border-base-300 border-2 bg-base-200'>
              <p className='text-lg m-2'>10:00</p>
            </div>
          </div>
        </div>
        <div className='rounded-lg bg-base-300 h-full w-1/2 flex flex-col p-6'>
          <span className='text-base-content font-bold text-xl'>Find a friend</span>
          <input className="input" type="text" placeholder="Search" value={keyword} onChange={(e) => setKeyword(e.target.value)} />
          <div className='overflow-y-auto flex-1'>
            <FriendList userId={user.id.toString()} keyword={keyword} />
          </div>
          <InvatationModal content={`http://localhost:3000/room/${id}`} />
        </div>
      </div>
    </>
  )
}
