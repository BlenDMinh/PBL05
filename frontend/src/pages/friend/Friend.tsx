import FriendList from './components/FriendList'
import RequestList from './components/RequestList'
export interface Friend {}

export default function Friend() {
  return (
    <div className='flex flex-col w-full h-full items-center py-6 px-10'>
      <div className='flex w-full h-full py-5 gap-5'>
        {/* <div className='flex flex-col w-1/5 justify-stretch h-full gap-5 mr-10'>
          <Link
            className='btn bg-base-200 border-b-8 border-base-300 w-full flex-1 flex items-center justify-center'
            to=''
          >
            <span className='text-base-content font-bold text-xl'>Kết nối bạn bè</span>
          </Link>
          <Link
            to=''
            className='btn bg-base-200 border-b-8 border-base-300 w-full flex-1 flex items-center justify-center'
          >
            <span className='text-base-content font-bold text-xl'>Gửi thư mời</span>
          </Link>
          <Link
            className='btn bg-base-200 border-b-8 border-base-300 w-full flex-1 flex items-center justify-center'
            to=''
          >
            <span className='text-base-content font-bold text-xl'>Tìm bạn bè trên Facebook</span>
          </Link>
          <Link
            to=''
            className='btn bg-base-200 border-b-8 border-base-300 w-full flex-1 flex items-center justify-center'
          >
            <span className='text-base-content font-bold text-xl'>Tạo lời thách đấu</span>
          </Link>
        </div> */}
        <div className='w-2/3 h-full ml-10 flex flex-col'>
          <FriendList />
        </div>
        <div className='flex-1 h-full flex flex-col'>
          <RequestList />
        </div>
      </div>
    </div>
  )
}
