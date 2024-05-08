import classNames from "classnames"
import { useContext, useState, ChangeEvent, useEffect } from "react"
import { FaCog, FaSearch } from "react-icons/fa"
import { HiChevronDoubleLeft, HiOutlineBell } from "react-icons/hi2"
import { useNavigate, Link } from "react-router-dom"
import { blankAvatar } from "src/assets/images"
import Button from "src/components/button/Button"
import { path } from "src/constants/path"
import { useAdminApp } from "src/contexts/app.admin.context"
import { AppContextType, AppContext, AuthenticateState } from "src/contexts/app.context"
import { clearLS } from "src/utils/auth"

export default function AdminNavbar() {
    const { showSidebar, setShowSidebar } = useAdminApp()
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

  useEffect(() => {

  }, [user])

  return (
    <div
      className={classNames('navbar bg-base-100 fixed z-10 h-28', {
        'lg:pl-96': showSidebar,
        'lg:pl-24': !showSidebar
      })}
    >
      <div className="flex justify-between w-full py-2 px-10">
        <span className="text-base-content text-3xl">Dashboard</span>
        <div className="flex w-1/2 gap-8">
          <label className="input h-16 grow rounded-xl bg-base-200 flex items-center gap-2">
            <span className="text-xl">
              <FaSearch />
            </span>
            <input type="text" className="grow text-xl" placeholder="Search" />
          </label>
          <button className="btn w-16 h-16 rounded-xl">
            <FaCog />
          </button>
        </div>
      </div>
    </div>
  )
}