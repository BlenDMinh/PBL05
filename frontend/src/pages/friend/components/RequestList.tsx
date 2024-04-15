import { useEffect, useState } from "react"
import friendApi from "src/apis/friend.api"
import { FriendRequest } from "src/types/users.type"
import YourRequest from "./YourRequest"

export default function RequestList() {
    const [page, setPage] = useState(1)
    const [requestList, setRequestList] = useState<FriendRequest[] | null>(null)
    const [maxPage, setMaxPage] = useState(1)

    useEffect(() => {
        setRequestList(null)
        friendApi.getFriendRequestList(page, 16).then(res => {
            setRequestList(res.data.requests)
            setMaxPage(res.data.totalPages)
        })
    }, [page])

    return <>
        <h2 className='text-xl font-bold mb-3'>Requests</h2>
        <div className='flex-1 flex flex-col overflow-y-auto bg-base-300 rounded-lg p-3 gap-3'>
            {
              requestList == null ?
              <div className='w-full h-full flex items-center justify-center'>
                <span className='loading loading-spinner loading-lg'/>
              </div>
              :
              <>
                {requestList.map((request) => (
                    <YourRequest request={request} />
                ))}
              </>
            }
          </div>
          <div className='join flex w-full justify-center mt-3'>
            <button className="join-item btn" onClick={() => setPage(Math.max(1, page - 1))}>«</button>
            <button className="join-item flex-1 btn">Page {page}</button>
            <button className="join-item btn" onClick={() => setPage(Math.min(maxPage, page + 1))}>»</button>
          </div>
    </>
}