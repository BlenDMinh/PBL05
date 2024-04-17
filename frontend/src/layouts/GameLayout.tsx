import { ReactWithChild } from 'src/interface/app'
import Navbar from './components/navbar'
import Sidebar from './components/sidebar'
import { AppContext, AppContextType } from 'src/contexts/app.context'
import { useContext } from 'react'
import classNames from 'classnames'

export interface GameLayoutProps {}

export default function GameLayout({ children }: ReactWithChild) {
  const { showSidebar, setShowSidebar } = useContext<AppContextType>(AppContext)
  return (
    <>
      <div className='bg-base'>
        <Navbar />
        <Sidebar />
        <div
          className={classNames({
            'brightness-50': showSidebar
          })}
          onClick={(e) => {
            setShowSidebar(false)
          }}
        >
          <div className='flex flex-col min-h-screen w-full items-center justify-center'>{children}</div>
        </div>
      </div>
    </>
  )
}
