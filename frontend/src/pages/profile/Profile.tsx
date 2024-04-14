import * as React from 'react'
import IconFriend from '../../assets/svgs/friends.svg'
import { FaGamepad, FaMinusSquare, FaSquare } from 'react-icons/fa'
import { BiMessage } from 'react-icons/bi'
import { Link, useParams } from 'react-router-dom'
import { useQuery } from 'react-query'
import profileApi from 'src/apis/profile.api'
import { useMemo } from 'react'
import { User } from 'src/types/users.type'
interface ProfileProps {}

const Profile: React.FC<ProfileProps> = () => {
  const { id } = useParams()
  const profileQuery = useQuery(['profile', id], () => profileApi.getProfile(id))
  const profile = useMemo(() => profileQuery.data ? profileQuery.data.data : null, [profileQuery.isFetched])

  if(!profile) {
    return (
      <div></div>
    )
  }

  return (
    <div className='flex flex-col w-full h-full py-6 px-10'>
      <div className='w-full flex items-center gap-5'>
        <div className=''>
          <img src={profile.avatarUrl ?? 'https://daisyui.com/images/stock/photo-1534528741775-53994a69daeb.jpg'} alt='Avatar' className='avatar w-48 h-48 rounded-full' />
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
      <div className='mt-5 bg-base-200'>
        <h2 className='text-lg font-bold pl-5'>Các ván cờ đã kết thúc</h2>
        <table className='w-full'>
          <thead>
            <tr>
              <th className='p-3 w-1/5'></th>
              <th className='p-3 w-2/5'>Các kỳ thủ</th>
              <th className='p-3 w-1/5'>Kết quả</th>
              <th className='p-3 w-1/5'>Ngày</th>
            </tr>
          </thead>
          <tbody className='w-full'>
            <tr>
              <td className='p-3 w-1/5'>
                <FaGamepad className='text-blue-500' size={28} />
              </td>
              <td className='p-3 w-2/5'>
                <p className='flex items-center'>
                  <FaSquare className='text-green-500 inline-block align-middle mr-1' />
                  <span>Martinvelo</span> <span className='text-xs'>(432)</span>
                  <Link to={`/chat/:id`}>
                    <BiMessage className='ml-2' />
                  </Link>
                </p>
                <p className='flex items-center'>
                  <FaSquare className='text-green-500 inline-block align-middle mr-1' />
                  <span>thuuhee</span> <span className='text-xs'>(232)</span>
                </p>
              </td>
              <td className='p-3 flex items-center justify-center w-1/5'>
                <span className='flex items-center'>
                  <p>1</p>
                  <FaMinusSquare className='text-red-500 m-2' />
                  <p>0</p>
                </span>
              </td>
              <td className='p-3 flex items-center justify-center w-1/5'></td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  )
}

export default Profile
