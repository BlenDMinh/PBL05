import classNames from 'classnames'
import { useContext } from 'react'
import { Link, useLocation, useNavigate } from 'react-router-dom'
import { path } from 'src/constants/path'
import { AppContext, AppContextType, AuthenticateState } from 'src/contexts/app.context'
import { sidebarOption } from 'src/data/layout'
import { HiBars3, HiArrowRightOnRectangle } from 'react-icons/hi2'
import { clearLS } from 'src/utils/auth'
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import { faChessKing } from '@fortawesome/free-solid-svg-icons'
import { matchPath } from 'react-router-dom'

export interface SidebarProps {}

export default function Sidebar(props: SidebarProps) {
  const { showSidebar, setIsAuthenticated, setUser } = useContext<AppContextType>(AppContext)
  const { pathname } = useLocation()
  const navigate = useNavigate()

  const handleSignOut = () => {
    clearLS()
    setIsAuthenticated(AuthenticateState.NOT_AUTHENTICATED)
    setUser(null)
    navigate(path.login)
  }
  return (
    <div className='drawer fixed z-10 w-max bg-base-200 lg:drawer-open'>
      <input id='my-drawer-2' type='checkbox' className='drawer-toggle' />
      <div className='drawer-content flex h-16 flex-col items-center justify-center pl-2  lg:hidden'>
        <label htmlFor='my-drawer-2' aria-label='open sidebar' className='btn drawer-button h-3/5'>
          <HiBars3 className='h-6 w-6' />
        </label>
      </div>

      <div
        className={classNames('overflow-y-clip h-[100vh]', {
          'drawer-side': window.innerWidth < 640
        })}
      >
        <label htmlFor='my-drawer-2' aria-label='close sidebar' className='drawer-overlay'></label>
        <ul
          className={classNames(
            'menu min-h-full justify-between shadow-inner bg-base-200 p-4 pt-0 text-base font-semibold text-black !transition-all',
            {
              'lg:w-60': showSidebar,
              'lg:w-[5.5rem]': !showSidebar
            }
          )}
        >
          <div className='flex w-full flex-col gap-8'>
            <div className='flex h-16 items-center justify-center'>
              <Link to='/' className='flex·w-max·items-center·px-0'>
                {showSidebar ? (
                  <div className='flex items-center'>
                    <FontAwesomeIcon icon={faChessKing} size='4x' className='text-primary' />
                    <h3 className='w-max pt-4 text-4xl font-semibold'>
                      <span className='text-primary'>CHESS</span>
                    </h3>
                  </div>
                ) : (
                  <h3 className='text-primary'>CHESS</h3>
                )}
              </Link>
            </div>
            <div className='flex flex-col gap-1'>
              {sidebarOption.map(({ icon: Icon, id, title, to }) => (
                <li key={id}>
                  <Link
                    className={classNames(
                      'text-base-content text-nowrap hover:bg-primary hover:text-white focus:!bg-primary focus:!text-white active:!bg-primary active:!text-white',
                      {
                        'tooltip tooltip-right': !showSidebar,
                        'bg-primary text-white': matchPath(pathname, to)
                      }
                    )}
                    to={to}
                    data-tip={title}
                  >
                    <Icon className='h-6 w-6' />
                    {showSidebar && title}
                  </Link>
                </li>
              ))}
            </div>
          </div>

          <li>
            <button
              className={`text-base-content hover:bg-primary hover:text-white focus:!bg-primary focus:!text-white active:!bg-primary active:!text-white ${
                !showSidebar && 'tooltip tooltip-right'
              }`}
              data-tip={'Sign out'}
              onClick={handleSignOut}
            >
              <HiArrowRightOnRectangle className='h-6 w-6' />
              {showSidebar && 'Sign out'}
            </button>
          </li>
        </ul>
      </div>
    </div>
  )
}
