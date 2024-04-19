import { useGameV2 } from 'src/contexts/gamev2.context'

export default function PromotionModal() {
  const game = useGameV2()
  return (
    <dialog id='promotionModal' className='modal'>
      <div className='modal-box'>
        <h3 className='font-bold text-lg'>Promotion</h3>
        <p className='py-4'>Choose one of the following piece</p>
        <form method='dialog'>
          <div className='w-full flex justify-evenly gap-5'>
            <button className='btn btn-primary flex-1' onClick={() => game.promote('q')}>
              Queen
            </button>
            <button className='btn btn-primary flex-1' onClick={() => game.promote('n')}>
              Knight
            </button>
            <button className='btn btn-primary flex-1' onClick={() => game.promote('b')}>
              Bishop
            </button>
            <button className='btn btn-primary flex-1' onClick={() => game.promote('r')}>
              Rook
            </button>
          </div>
          <button className='btn modal-action' onClick={() => game.promote()}>
            Cancel
          </button>
        </form>
      </div>
    </dialog>
  )
}
