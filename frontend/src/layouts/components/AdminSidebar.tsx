import classNames from 'classnames'
import { useContext, useEffect, useState } from 'react'
import { FaGear } from "react-icons/fa6";
import { Link, useLocation, useNavigate } from 'react-router-dom'
import { path } from 'src/constants/path'
import { AppContext, AppContextType, AuthenticateState } from 'src/contexts/app.context'
import { SidebarOption, publicSidebarOption, sidebarOption } from 'src/data/layout'
import { HiBars3, HiArrowRightOnRectangle } from 'react-icons/hi2'
import { clearLS } from 'src/utils/auth'
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import { faChessKing, faGear } from '@fortawesome/free-solid-svg-icons'
import { matchPath } from 'react-router-dom'
import { useAdminApp } from 'src/contexts/app.admin.context'
import { FaAddressCard, FaBell, FaCalendar, FaChartLine, FaEnvelope, FaHome, FaList, FaListUl, FaNewspaper, FaQuestionCircle, FaSignOutAlt, FaUserCircle } from 'react-icons/fa'
import { blankAvatar } from 'src/assets/images'

export interface AdminSidebarProps {}

function SidebarElement({ id, icon: Icon, title, to }: SidebarOption) {
  const { showSidebar } = useAdminApp()
  const { pathname } = useLocation()
  return (
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
        <Icon className='h-4 w-4' />
        {showSidebar && title}
      </Link>
    </li>
  )
}

export default function AdminSidebar(props: AdminSidebarProps) {
  const { showSidebar } = useAdminApp()
  const { pathname } = useLocation()
  const navigate = useNavigate()

  const handleSignOut = () => {
    clearLS()
    // setIsAuthenticated(AuthenticateState.NOT_AUTHENTICATED)
    // setUser(null)
    navigate(path.home)
  }
  const [windowWidth, setWindowWidth] = useState<number>(window.innerWidth)

  useEffect(() => {
    const handleResize = () => {
      setWindowWidth(window.innerWidth)
    }

    window.addEventListener('resize', handleResize)

    return () => {
      window.removeEventListener('resize', handleResize)
    }
  }, [])

  const generalSideBarOptions : SidebarOption[] = [
    {
      id: 0,
      title: 'Dashboard',
      icon: FaHome,
      to: path.admin
    },
    {
      id: 1,
      title: 'Message',
      icon: FaEnvelope,
      to: ''
    },
    {
      id: 2,
      title: 'Schedule',
      icon: FaCalendar,
      to: ''
    },
    {
      id: 3,
      title: 'Analytics',
      icon: FaChartLine,
      to: ''
    },
    {
      id: 4,
      title: 'News',
      icon: FaNewspaper,
      to: ''
    },
  ]

  const jobSideBarOptions : SidebarOption[] = [
    {
      id: 0,
      title: 'Ruleset',
      icon: FaList,
      to: path.adminRuleset
    },
    {
      id: 1,
      title: 'Account',
      icon: FaUserCircle,
      to: path.adminAccount
    },
    {
      id: 2,
      title: 'Hack report',
      icon: FaAddressCard,
      to: ''
    },
  ]

  const supportSidebarOptions : SidebarOption[] = [
    {
      id: 0,
      title: "Setting",
      icon: FaGear,
      to: ''
    },
    {
      id: 1,
      title: 'help',
      icon: FaQuestionCircle,
      to: ''
    }
  ]

  return (
    <div className='drawer fixed z-10 w-max lg:drawer-open'>
      <input id='my-drawer-2' type='checkbox' className='drawer-toggle' />
      <div className='drawer-content flex h-16 flex-col items-center justify-center pl-2  lg:hidden'>
        <label htmlFor='my-drawer-2' aria-label='open sidebar' className='btn drawer-button h-3/5'>
          <HiBars3 className='h-6 w-6' />
        </label>
      </div>

      <div
        className={classNames('overflow-y-auto h-[100vh]', {
          'drawer-side': windowWidth < 1024
        })}
      >
        <label htmlFor='my-drawer-2' aria-label='close sidebar' className='drawer-overlay'></label>
        <ul
          className={classNames(
            'menu min-h-full justify-between shadow-inner bg-base-200 p-4 pt-0 text-base font-semibold text-black !transition-all',
            {
              'lg:w-96': showSidebar,
              'lg:w-[5.5rem]': !showSidebar
            }
          )}
          style={{
              padding: '45px 0px 43px 0px',
              gap: '40px',
              opacity: '0px',
            }}
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
            <div className='flex flex-col gap-1 px-5 max-h-screen'>
              <div className='grow overflow-y-auto flex flex-col'>
                <span className='text-sm text-neutral-content'>General</span>
                {generalSideBarOptions.map(
                  ({ icon: Icon, id, title, to }) =>
                    <SidebarElement id={id} icon={Icon} title={title} to={to} />
                )}
                <div className="divider"></div> 
                <span className='text-sm text-neutral-content'>Jobs</span>
                {
                  jobSideBarOptions.map(
                    ({ icon: Icon, id, title, to }) =>
                      <SidebarElement id={id} icon={Icon} title={title} to={to} />
                  )
                }
                <div className="divider"></div> 
                <span className='text-sm text-neutral-content'>Support</span>
                {
                  supportSidebarOptions.map(
                    ({ icon: Icon, id, title, to }) =>
                      <SidebarElement id={id} icon={Icon} title={title} to={to} />
                  )
                }
                <div className="divider"></div>
              </div>
              <div>
                <label className='label cursor-pointer'>
                  <span className='text-base-content'>Dark mode</span>
                  <input name='theme' type="checkbox" className='toggle' />
                </label>
                <div className='flex justify-between min-h-fit'>
                  <div className='flex items-center gap-5'>
                    <div className='avatar'>
                      <div className='rounded-full w-12 h-12'>
                        <img src={blankAvatar} alt="" className='object-cover' />
                      </div>
                    </div>
                    <span className='text-base-content text-lg'>Name</span>
                  </div>
                  <div className='flex gap-3 text-base-content'>
                    <button className='btn btn-ghost w-12 h-12 text-xl'>
                      <FaBell />
                    </button>
                    <button className='btn btn-ghost w-12 h-12 text-xl'>
                      <FaSignOutAlt />
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>
          {/* {isAuthenticated === AuthenticateState.AUTHENTICATED && (
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
          )} */}
        </ul>
      </div>
    </div>
  )
}
