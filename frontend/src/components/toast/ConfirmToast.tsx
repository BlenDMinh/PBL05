import { IconType } from 'react-icons'
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
  return (
    <div className='toast'>
      <div className='alert alert-info'>
        <div className='flex'>
          <img src={props.avatar} alt='' />
          <p>{props.name}</p>
          <p>{props.side}</p>
          {props.rated && <p>rated</p>}
        </div>
        <div className='flex gap-5'>
          <button type='button' onClick={props.onConfirm}>
            Confirm
          </button>
          <button type='button' onClick={props.onCancel}>
            Cancel
          </button>
        </div>
      </div>
    </div>
  )
}
