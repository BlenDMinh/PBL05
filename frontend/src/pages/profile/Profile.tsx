import * as React from 'react'
import IconFriend from '../../assets/svgs/friends.svg'
import { FaCamera, FaChess, FaGamepad, FaMinusSquare, FaPencilAlt, FaPencilRuler, FaSquare, FaUsers, FaWaveSquare, FaWifi } from 'react-icons/fa'
import { BiMessage } from 'react-icons/bi'
import { Link, useParams } from 'react-router-dom'
import { useQuery } from 'react-query'
import profileApi from 'src/apis/profile.api'
import { useMemo } from 'react'
import { User } from 'src/types/users.type'
import GameHistory from './components/GameHistory'
import { blankAvatar } from 'src/assets/images'
import { AppContext } from 'src/contexts/app.context'
import AvatarModal from './components/AvatarModal'
import FriendList from '../friend/components/FriendList'
import { FaPencil } from 'react-icons/fa6'
interface ProfileProps {}

const Profile: React.FC<ProfileProps> = () => {
  const { id } = useParams()
  const profileQuery = useQuery(['profile', id], () => profileApi.getProfile(id))
  const profile = useMemo(() => profileQuery.data ? profileQuery.data.data : null, [profileQuery.isFetched])
  
  const { user } = React.useContext(AppContext)

  if(!profile) {
    return (
      <div></div>
    )
  }

  return (
    <div className='flex flex-col w-full h-full py-6 px-10 gap-10'>
      <div className='p-10 rounded-lg bg-base-200 w-full h-1/2'>
        <div className='w-full h-full flex justify-between'>
          <div className='w-full h-full flex items-center gap-5'>
            <div className=''>
              <img src={profile.avatarUrl ?? blankAvatar} alt='Avatar' className='avatar w-48 h-48 rounded-full object-cover' />
              {profile.id === user?.id && <>
              <div className='h-12 w-12 z-10 relative -top-10'>
                <AvatarModal />
              </div>
              </>}
            </div>
            <div className='flex flex-col h-full'>
              <h1 className='text-2xl font-bold'>{profile.displayName}</h1>
              <h1 className='text-lg flex-1'>{profile.email}</h1>
              <div className='flex gap-10'>
                <div className='flex flex-col text-info text-4xl items-center'>
                  <FaWifi />
                  <span className='text-base-content text-xl'>Online</span>
                </div>
                <div className='flex flex-col text-secondary text-4xl items-center'>
                  <FaChess />
                  <span className='text-base-content text-xl'>102</span>
                </div>
                <div className='flex flex-col text-accent text-4xl items-center'>
                  <FaUsers />
                  <span className='text-base-content text-xl'>203 </span>
                </div>
              </div>
            </div>
          </div>
          <div>
            <button className='btn btn-ghost text-2xl w-16 h-16'>
              <FaPencil />
            </button>
          </div>
        </div>
      </div>
      <div className='w-full h-full flex flex-col'>
        <FriendList />
      </div>
      <div className='mt-5'>
        <h2 className='text-lg font-bold pl-5'>Previous games</h2>
        <GameHistory />
      </div>
    </div>
  )
}

export default Profile
