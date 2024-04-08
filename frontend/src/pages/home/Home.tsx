import { useContext } from "react"
import { AppContext, AppContextType } from "src/contexts/app.context"
import chessBoard from "src/assets/images/chessboard.png"
import { Link } from "react-router-dom"

export default function Home() {
  const { user } = useContext<AppContextType>(AppContext)
  return (
    <div className="flex flex-col w-full items-center py-10 px-32">
      <div className="w-full flex gap-5">
        <div className='avatar btn btn-circle btn-ghost'>
          <div className='w-20 rounded-full'>
            <img
              alt='Tailwind CSS Navbar component'
              src='https://daisyui.com/images/stock/photo-1534528741775-53994a69daeb.jpg'
            />
          </div>
        </div>
        <span className="text-base-content text-3xl font-bold">{user?.displayName}</span>
      </div>
      <div className="w-full flex gap-5">
        <span className="text-base-content text-3xl font-bold">ELO: {" "}</span>
        <span className="text-info text-3xl font-bold">{user?.elo}</span>
      </div>
      <div className="flex w-full gap-10 h-96">
        <div className="flex flex-col h-full justify-evenly">
          <Link className="btn bg-base-200 h-24 w-64 border-b-8 border-base-300" to="/game/quickmatch">
            <span className="text-base-content font-bold text-xl">Quick Match</span>
          </Link>
          <div className="btn bg-base-200 h-24 w-64 border-b-8 border-base-300">

          </div>
          <div className="btn bg-base-200 h-24 w-64 border-b-8 border-base-300">

          </div>
        </div>
        <div className="btn bg-base-200 h-full w-96 border-b-8 border-base-300">

        </div>
        <div className="btn bg-base-200 h-full w-96 border-b-8 border-base-300">

        </div>
      </div>
    </div>
  )
}
