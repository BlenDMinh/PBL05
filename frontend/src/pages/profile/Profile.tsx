import * as React from 'react'
import IconFriend from '../../assets/svgs/friends.svg'
import { FaCamera, FaGamepad, FaMinusSquare, FaSquare } from 'react-icons/fa'
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
    <div className='flex flex-col w-full h-full py-6 px-10'>
      <div className='w-full flex items-center gap-5'>
        <div className=''>
          {profile.id === user?.id && <>
          <div className='h-12 w-12 z-10 absolute'>
            <AvatarModal />
          </div>
          </>}
          <img src={profile.avatarUrl ?? blankAvatar} alt='Avatar' className='avatar w-48 h-48 rounded-full' />
        </div>
        <h1 className='text-2xl font-bold'>{profile.displayName}</h1>
      </div>
      <div className='mt-2 flex flex-col items-center'>
        <h2 className='text-lg font-bold'>Thông tin cá nhân</h2>
        <div className='mt-2'>
          <p>
            <span className='font-bold'>Tên:</span> {profile.displayName}
          </p>
          <p>
            <span className='font-bold'>Điểm:</span> {profile.elo}
          </p>
          <p>
            <span className='font-bold'>Cấp độ:</span> 10
          </p>
        </div>
      </div>
      <div className='mt-5 bg-base-200'>
        <h2 className='text-lg font-bold flex flex-col items-center'>Các ván đấu</h2>
        <p className='text-base-content p-5'>Không có ván đấu nào đang diễn ra</p>
      </div>
      <div className='mt-5'>
        <h2 className='text-lg font-bold pl-5'>Previous games</h2>
        <GameHistory />
      </div>
    </div>
  )
}

export default Profile
