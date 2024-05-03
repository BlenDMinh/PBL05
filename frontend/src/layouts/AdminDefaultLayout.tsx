import classNames from 'classnames'
import { ReactWithChild } from 'src/interface/app'
import { useContext, useEffect } from 'react'
import { AppContext, AppContextType } from 'src/contexts/app.context'
import AdminNavbar from './components/AdminNavbar'
import AdminSidebar from './components/AdminSidebar'
import { useAdminApp } from 'src/contexts/app.admin.context'

export default function AdminDefaultLayout({ children }: ReactWithChild) {
  const { showSidebar, setShowSidebar, theme } = useAdminApp()

  useEffect(() => {
    document.querySelector('html')?.setAttribute('data-theme', theme);
  }, [theme])

  return (
    <div className='bg-base'>
      <AdminNavbar />
      <AdminSidebar />
      {/* <div
        className={classNames('max-h-max overflow-y-auto', {
          'backdrop-blur-sm bg-white/30 brightness-50': showSidebar
        })}
        onClick={(e) => {
          setShowSidebar(false)
        }}
      > */}
        <div
          className={classNames('flex min-h-screen h-screen w-full flex-col items-center pt-28', {
            'lg:pl-96': showSidebar,
            'lg:pl-[88px]': !showSidebar
          })}
        >
          {children}
        </div>
      </div>
    // </div>
  )
}
