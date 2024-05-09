import { useEffect, useMemo, useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import { FaSearch } from 'react-icons/fa'
import { getProfileFromLS } from 'src/utils/auth'
import { useQuery } from 'react-query'
import { Friend as FriendType } from 'src/types/users.type'
import friendApi from 'src/apis/friend.api'
import YourFriend from './YourFriend'

export default function FriendList() {
  const id = getProfileFromLS().id
  const [page, setPage] = useState(1)
  const [friendList, setFriendList] = useState<FriendType[]>([])
  const [maxPage, setMaxPage] = useState(1)
  const [keyword, setKeyword] = useState('')

  useEffect(() => {
    if (id)
      friendApi.getFriendList(id.toString(), page, 16, keyword).then((res) => {
        setFriendList(res.data.friends)
        setMaxPage(res.data.totalPages)
      })
  }, [page, keyword, id])

  return (
    <>
      <h2 className='text-xl font-bold mb-3'>Current Friends</h2>
      <div className='w-full flex gap-3 items-center mb-3'>
        <input
          value={keyword}
          onChange={(e) => setKeyword(e.target.value)}
          className='flex-1 input input-primary'
          placeholder='Search by display name or email'
        />
        <FaSearch className='text-xl' />
      </div>
      <div className='flex-1 flex flex-col overflow-y-auto bg-base-300 rounded-lg p-3 gap-3'>
        {friendList == null ? (
          <div className='w-full h-full flex items-center justify-center'>
            <span className='loading loading-spinner loading-lg' />
          </div>
        ) : (
          <>
            {friendList.map((friend) => (
              <YourFriend key={friend.id} friend={friend} />
            ))}
          </>
        )}
      </div>
      <div className='join flex w-full justify-center mt-3'>
        <button className='join-item btn' onClick={() => setPage(Math.max(1, page - 1))}>
          «
        </button>
        <button className='join-item flex-1 btn'>Page {page}</button>
        <button className='join-item btn' onClick={() => setPage(Math.min(maxPage, page + 1))}>
          »
        </button>
      </div>
    </>
  )
}
