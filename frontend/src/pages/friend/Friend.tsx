import * as React from 'react'
import IconFriend from '../../assets/svgs/friends.svg'
import { Link } from 'react-router-dom'
import { FaSearch, FaUserAlt, FaUserAltSlash, FaUserMinus, FaUserPlus } from 'react-icons/fa'
export interface Friend {}

export default function Friend() {
  return (
    <div className='flex flex-col w-full h-full items-center py-6 px-10'>
      <div className='w-full flex items-center gap-5'>
        <div className='avatar btn btn-circle btn-ghost'>
          <div className='w-20 rounded-full'>
            <img src={IconFriend} alt='' className='mr-2 w-1/3' />
          </div>
        </div>
        <span className='text-base-content text-3xl font-bold mr-80'>Bạn bè</span>
        <div className='flex items-center'>
          <div className='relative flex-grow'>
            <input
              type='text'
              placeholder='Tìm kiếm bạn bè'
              className='border border-gray-300 rounded-md px-3 py-1 focus:outline-none w-64 pr-10'
            />
            <div className='absolute right-3 top-1/2 transform -translate-y-1/2'>
              <FaSearch className='h-5 w-5 text-gray-300' />
            </div>
          </div>
          <button className='bg-blue-500 text-white px-6 py-1 rounded-md ml-2'>Tìm kiếm</button>
        </div>
      </div>

      <div className='flex w-full h-3/5 mt-5'>
        <div className='flex flex-col w-2/3 h-full gap-5 grid grid-cols-2 grid-rows-2 mr-10'>
          <Link
            className='btn bg-base-200 border-b-8 border-base-300 w-full h-2/3 flex items-center justify-center'
            to=''
          >
            <span className='text-base-content font-bold text-xl'>Kết nối bạn bè</span>
          </Link>
          <Link
            to=''
            className='btn bg-base-200 border-b-8 border-base-300 w-full h-2/3 flex items-center justify-center'
          >
            <span className='text-base-content font-bold text-xl'>Gửi thư mời</span>
          </Link>
          <Link
            className='btn bg-base-200 border-b-8 border-base-300 w-full h-2/3 flex items-center justify-center'
            to=''
          >
            <span className='text-base-content font-bold text-xl'>Tìm bạn bè trên Facebook</span>
          </Link>
          <Link
            to=''
            className='btn bg-base-200 border-b-8 border-base-300 w-full h-2/3 flex items-center justify-center'
          >
            <span className='text-base-content font-bold text-xl'>Tạo lời thách đấu</span>
          </Link>
        </div>
        <div className='w-1/3 ml-10'>
          <h2 className='text-xl font-bold mb-3'>Danh sách bạn bè hiện tại</h2>
          {[1, 2, 3, 4, 5].map((index) => (
            <div key={index} className='bg-base-200 p-4 rounded-md relative flex items-center mb-3'>
              <div className='avatar btn btn-circle btn-ghost'>
                <div className='w-20 rounded-full'>
                  <img src={IconFriend} alt='' className='mr-2 w-1/3' />
                </div>
              </div>
              <span className='text-base-content'>Thu ha</span>
              <div className='absolute right-3 top-1/2 transform -translate-y-1/2'>
                <FaUserPlus className='h-5 w-5 text-gray-300' />
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  )
}
