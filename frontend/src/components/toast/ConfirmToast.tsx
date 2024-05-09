import { useContext } from 'react'
import { IconType } from 'react-icons'
import { useQuery } from 'react-query'
import profileApi from 'src/apis/profile.api'
import { AppContext } from 'src/contexts/app.context'
import { User } from 'src/types/users.type'
import { twMerge } from 'tailwind-merge'

export interface ToastProps extends React.ButtonHTMLAttributes<HTMLButtonElement> {
  avatar?: string
  name?: string
  side?: string
  rated?: boolean
  onConfirm?: () => void
  onCancel?: () => void
}

export default function ConfirmToast(props: ToastProps) {
  const app = useContext(AppContext)
  const opponent = useQuery(['user', app.invitationMessage], () =>
    profileApi.getProfile(app.invitationMessage?.data.opponentId + '')
  ).data?.data as User

  if (!opponent) {
    return (
      <div className='toast'>
        <div className='w-96 h-96 bg-base-300 rounded-lg shadow-2xl flex flex-col p-8'>
          <span className='loading loading-spinner loading-lg'></span>
        </div>
      </div>
    )
  }

  return (
    <div className='toast'>
      <div className='w-96 h-96 bg-base-300 rounded-lg shadow-2xl flex flex-col p-8'>
        <div className='flex flex-col flex-1 gap-5'>
          <div className='flex gap-5 items-center'>
            <div className='avatar'>
              <div className='rounded-full w-16 h-16'>
                <img src={opponent?.avatarUrl ?? ''} alt='' />
              </div>
            </div>
            <span className='font-bold text-xl'>{opponent.displayName}</span>
          </div>
          <span className='text-xl'>Invited you to play a game</span>
          {
            app.invitationMessage?.data.gameRule &&
            <span>Rule: {app.invitationMessage?.data.gameRule.ruleName}</span>
          }
          <span>Play as: {app.invitationMessage?.data.meSide}</span>
        </div>
        <div className='flex w-full items-stretch justify-between gap-5'>
          <button className='btn btn-success' onClick={props.onConfirm}>
            Confirm
          </button>
          <button className='btn btn-error' onClick={props.onCancel}>
            Cancel
          </button>
        </div>
      </div>
    </div>
  )
}
