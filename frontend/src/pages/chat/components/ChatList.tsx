import { useChat } from 'src/contexts/chat.context'
import Conversation from './Conversation'
import { useEffect } from 'react'
import { getSessionIdFromLS } from 'src/utils/auth'
import { ReadyState } from 'react-use-websocket'
import { useParams } from 'react-router-dom'

export default function ChatList() {
  const { id } = useParams()
  const chat = useChat()
  useEffect(() => {
    chat.startChat(getSessionIdFromLS())
  }, [])
  if (chat.state != ReadyState.OPEN) {
    return (
      <div className='bg-base h-full w-full flex items-center justify-center'>
        <span className='loading loading-lg loading-ring'></span>
      </div>
    )
  }
  return (
    <div className='bg-base h-full w-full overflow-y-auto p-1 gap-1'>
      {chat.conversations.map((c) => (
        <Conversation key={c.id} conversation={c} active={id == c.id.toString()} />
      ))}
    </div>
  )
}
