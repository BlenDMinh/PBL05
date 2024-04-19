import classNames from 'classnames'
import { GameResult, useGameV2 } from 'src/contexts/gamev2.context'

export interface ResultModalPropss {
  onReplay: () => void
  onExit: () => void
}

export default function ResultModal(props: ResultModalPropss) {
  const game = useGameV2()
  return (
    <dialog id='resultModal' className='modal'>
      <div className='modal-box'>
        <h3 className='font-bold text-lg text-center'>Game ended</h3>
        <p
          className={classNames('text-center text-6xl font-bold py-12', {
            'text-info': game.result == GameResult.WIN,
            'text-error': game.result == GameResult.LOSE,
            'text-accent': game.result == GameResult.DRAW,
            'text-base-content': game.result == GameResult.UNKNOWN
          })}
        >
          {game.result == GameResult.WIN
            ? 'WIN'
            : game.result == GameResult.LOSE
            ? 'LOSE'
            : game.result == GameResult.DRAW
            ? 'DRAW'
            : 'IDK'}
        </p>
        <form method='dialog'>
          <div className='w-full flex justify-evenly gap-5'>
            <button className='btn btn-primary flex-1 text-lg' onClick={props.onReplay}>
              Play again
            </button>
            <button className='btn flex-1 text-lg' onClick={props.onExit}>
              Exit
            </button>
          </div>
        </form>
      </div>
    </dialog>
  )
}
