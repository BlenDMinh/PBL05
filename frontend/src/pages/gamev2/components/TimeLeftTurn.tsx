import { useEffect, useState } from 'react'

export interface TimeLeftTurnProps {
  milliseconds: number
}

function TimeLeftTurn(props: TimeLeftTurnProps) {
  const [timeLeft, setTimeLeft] = useState<number>(props.milliseconds)

  useEffect(() => {
    const interval = setInterval(() => {
      setTimeLeft((prevTimeLeft) => {
        if (prevTimeLeft <= 0) {
          clearInterval(interval)
          return 0
        }
        return prevTimeLeft - 1000
      })
    }, 1000)

    return () => clearInterval(interval)
  }, [props.milliseconds])

  // Convert milliseconds to minutes and seconds
  const minutes: number = Math.floor(timeLeft / 60000)
  const seconds: string = Math.floor((timeLeft % 60000) / 1000).toString().padStart(2, '0');

  return (
    <div className='border rounded-full bg-base-100 flex justify-center items-center'>
      <p className='text-base-content text-xs p-1'>
        {minutes}:{seconds}
      </p>
    </div>
  )
}

export default TimeLeftTurn
