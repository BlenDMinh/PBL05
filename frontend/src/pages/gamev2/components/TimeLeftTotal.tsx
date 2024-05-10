import { useEffect, useState } from 'react'

export interface TimeLeftTotalProps {
  milliseconds: number
  running: boolean
}

function TimeLeftTotal(props: TimeLeftTotalProps) {
  const [timeLeft, setTimeLeft] = useState<number>(props.milliseconds)

  useEffect(() => {
    let interval: NodeJS.Timeout | undefined
    if (props.running === true)
      interval = setInterval(() => {
        setTimeLeft((prevTimeLeft) => {
          if (prevTimeLeft <= 0) {
            clearInterval(interval)
            return 0
          }
          return prevTimeLeft - 1000
        })
      }, 1000)

    return () => {
      if (props.running === true) {
        clearInterval(interval!)
      }
    }
  }, [props.milliseconds, props.running])

  // Convert milliseconds to minutes and seconds
  const minutes: number = Math.floor(timeLeft / 60000)
  const seconds: string = Math.floor((timeLeft % 60000) / 1000)
    .toString()
    .padStart(2, '0')

  return (
    <div className='border-base-300 border-2 bg-base-200'>
      <span className='font-mono text-2xl p-2'>
        {minutes}:{seconds}
      </span>
    </div>
  )
}

export default TimeLeftTotal
