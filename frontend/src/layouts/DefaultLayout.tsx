import Navbar from 'src/layouts/components/navbar'
import Sidebar from 'src/layouts/components/sidebar'
import classNames from 'classnames'
import { ReactWithChild } from 'src/interface/app'
import { useContext } from 'react'
import { AppContext, AppContextType } from 'src/contexts/app.context'

export default function DefaultLayout({ children }: ReactWithChild) {
  const { showSidebar, setShowSidebar } = useContext<AppContextType>(AppContext)
  return (
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
          className={classNames('flex min-h-screen h-screen w-full flex-col items-center pt-16 lg:pl-[88px]', {
            'pointer-events-none': showSidebar
          })}
        >
          {children}
        </div>
      </div>
    </div>
  )
}
