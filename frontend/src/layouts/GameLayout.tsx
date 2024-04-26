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
          className={classNames('max-h-max overflow-y-auto', {
            'backdrop-blur-sm bg-white/30 brightness-50': showSidebar
          })}
          onClick={(e) => {
            setShowSidebar(false)
          }}
        >
          <div
            className={classNames('flex flex-col min-h-screen w-full items-center justify-center', {
              'pointer-events-none': showSidebar
            })}
          >
            {children}
          </div>
        </div>
      </div>
    </>
  )
}
