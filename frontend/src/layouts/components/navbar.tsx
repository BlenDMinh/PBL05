import classNames from 'classnames'
import { ChangeEvent, useContext, useEffect, useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { AppContext, AppContextType, AuthenticateState } from 'src/contexts/app.context'
import { quickOptionsNavbar } from 'src/data/layout'
import { HiChevronDoubleLeft, HiOutlineBell } from 'react-icons/hi2'
import Button from 'src/components/button/Button'
import { path } from 'src/constants/path'
import { clearLS } from 'src/utils/auth'
import blankAvatar from 'src/assets/images/blankavatar.webp'
export interface NavbarProps {}

export default function Navbar(props: NavbarProps) {
  const { showSidebar, setShowSidebar, isAuthenticated } = useContext<AppContextType>(AppContext)
  const handleShowSidebar = () => {
    setShowSidebar(!showSidebar)
  }
  const { user, setIsAuthenticated, setUser } = useContext<AppContextType>(AppContext)
  const navigate = useNavigate()

  const handleSignOut = () => {
    clearLS()
    setIsAuthenticated(AuthenticateState.NOT_AUTHENTICATED)
    setUser(null)
    navigate(path.home)
  }

  const [theme, setTheme] = useState<string>(localStorage.getItem('theme') ? localStorage.getItem('theme')! : 'light')

  // update state on toggle
  const handleToggle = (event: ChangeEvent<HTMLInputElement>) => {
    let theme = 'dark'
    if (event.target.checked) {
      theme = 'light'
    }
    localStorage.setItem('theme', theme)
    setTheme(theme)
  }

  useEffect(() => {
    document.querySelector('html')!.setAttribute('data-theme', theme)
  }, [theme])

  return (
    <div
      className={classNames('navbar fixed z-10 h-16 justify-between bg-base-200 py-2 px-16', {
        'lg:pl-[16rem]': showSidebar,
        'lg:pl-24': !showSidebar
      })}
    >
      <div className='navbar-start flex-[30%]'>
        <Button
          className={classNames('btn btn-circle btn-ghost hidden transition-all lg:flex', {
            'rotate-180': !showSidebar
          })}
          onClick={handleShowSidebar}
          Icon={HiChevronDoubleLeft}
        />
      </div>
      {isAuthenticated === AuthenticateState.AUTHENTICATED && (
        <div className='navbar-end flex flex-[30%] items-center'>
          <button className='btn btn-circle btn-ghost mt-1'>
            <div className='indicator'>
              <HiOutlineBell className='h-6 w-6' />
              <span className='badge indicator-item badge-primary badge-xs'></span>
            </div>
          </button>

          <div className='dropdown dropdown-end'>
            <div tabIndex={0} role='button' className='avatar btn btn-circle btn-ghost'>
              <div className='w-10 rounded-full'>
                <img alt='Tailwind CSS Navbar component' src={user?.avatarUrl ?? blankAvatar} />
              </div>
            </div>
            <div
              tabIndex={0}
              role='button'
              className='dropdown-content top-[120%] z-[1] flex w-56 flex-col rounded-md bg-base-300 shadow-2xl'
            >
              <div className='w-full border-b border-dashed border-black p-4 py-3'>
                <h3 className='line-clamp-1 text-base font-semibold'>{user?.displayName} &nbsp;</h3>
                <p className='line-clamp-1 text-sm'>{user?.email} &nbsp;</p>
              </div>
              <ul className='menu menu-sm gap-[1px]'>
                <li className='rounded'>
                  <Link
                    className='hover:bg-primary hover:text-white focus:!bg-primary focus:!text-white active:!bg-primary'
                    to={path.profile.replace(':id', user?.id?.toString() ?? "")}
                  >
                    Profile
                  </Link>
                </li>
                <li className='rounded'>
                  <button
                    className='hover:bg-primary hover:text-white focus:!bg-primary focus:!text-white active:!bg-primary'
                    onClick={handleSignOut}
                  >
                    Sign out
                  </button>
                </li>
              </ul>
            </div>
          </div>
        </div>
      )}

      {isAuthenticated === AuthenticateState.NOT_AUTHENTICATED && (
        <div className='flex gap-1'>
          <button className='btn btn-primary flex-1' onClick={() => navigate(path.login)}>
            Login
          </button>
          <button className='btn btn-neutral flex-1' onClick={() => navigate(path.register)}>
            Sign up
          </button>
        </div>
      )}

      <label className='swap swap-rotate ml-4'>
        {/* this hidden checkbox controls the state */}
        <input
          type='checkbox'
          id='nav-toggle-theme'
          checked={localStorage.getItem('theme') === 'light'}
          onChange={handleToggle}
        />

        {/* sun icon */}
        <svg className='swap-on fill-current w-10 h-10' xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24'>
          <path d='M5.64,17l-.71.71a1,1,0,0,0,0,1.41,1,1,0,0,0,1.41,0l.71-.71A1,1,0,0,0,5.64,17ZM5,12a1,1,0,0,0-1-1H3a1,1,0,0,0,0,2H4A1,1,0,0,0,5,12Zm7-7a1,1,0,0,0,1-1V3a1,1,0,0,0-2,0V4A1,1,0,0,0,12,5ZM5.64,7.05a1,1,0,0,0,.7.29,1,1,0,0,0,.71-.29,1,1,0,0,0,0-1.41l-.71-.71A1,1,0,0,0,4.93,6.34Zm12,.29a1,1,0,0,0,.7-.29l.71-.71a1,1,0,1,0-1.41-1.41L17,5.64a1,1,0,0,0,0,1.41A1,1,0,0,0,17.66,7.34ZM21,11H20a1,1,0,0,0,0,2h1a1,1,0,0,0,0-2Zm-9,8a1,1,0,0,0-1,1v1a1,1,0,0,0,2,0V20A1,1,0,0,0,12,19ZM18.36,17A1,1,0,0,0,17,18.36l.71.71a1,1,0,0,0,1.41,0,1,1,0,0,0,0-1.41ZM12,6.5A5.5,5.5,0,1,0,17.5,12,5.51,5.51,0,0,0,12,6.5Zm0,9A3.5,3.5,0,1,1,15.5,12,3.5,3.5,0,0,1,12,15.5Z' />
        </svg>

        {/* moon icon */}
        <svg className='swap-off fill-current w-10 h-10' xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24'>
          <path d='M21.64,13a1,1,0,0,0-1.05-.14,8.05,8.05,0,0,1-3.37.73A8.15,8.15,0,0,1,9.08,5.49a8.59,8.59,0,0,1,.25-2A1,1,0,0,0,8,2.36,10.14,10.14,0,1,0,22,14.05,1,1,0,0,0,21.64,13Zm-9.5,6.69A8.14,8.14,0,0,1,7.08,5.22v.27A10.15,10.15,0,0,0,17.22,15.63a9.79,9.79,0,0,0,2.1-.22A8.11,8.11,0,0,1,12.14,19.73Z' />
        </svg>
      </label>
    </div>
  )
}
