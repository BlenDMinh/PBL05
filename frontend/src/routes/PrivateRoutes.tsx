import { useContext, useEffect } from 'react'
import { Outlet, useLocation, useNavigate } from 'react-router-dom'
import { path } from 'src/constants/path'
import { AppContext, AppContextType, AuthenticateState } from 'src/contexts/app.context'
import DefaultLayout from 'src/layouts/DefaultLayout'

function PrivateRoutes() {
  const { isAuthenticated } = useContext<AppContextType>(AppContext)
  const navigate = useNavigate()
  const loc = useLocation()

  useEffect(() => {
    if (isAuthenticated == AuthenticateState.UNKNOWN) return
    if (isAuthenticated != AuthenticateState.AUTHENTICATED) {
      if (loc.pathname !== path.home) navigate(path.login)
    }
  }, [isAuthenticated])
  // TODO useRoutes
  // if (!isAuthenticated) {
  //   return null
  // }

  return (
    <DefaultLayout>
      <Outlet />
    </DefaultLayout>
  )
}

export default PrivateRoutes
