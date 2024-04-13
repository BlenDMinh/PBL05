import { useContext, useEffect } from 'react'
import { Outlet, useNavigate } from 'react-router-dom'
import { path } from 'src/constants/path'
import { AppContext, AppContextType, AuthenticateState } from 'src/contexts/app.context'
import AuthLayout from 'src/layouts/AuthLayout'

function AuthRoutes() {
  const { isAuthenticated } = useContext<AppContextType>(AppContext)
  const navigate = useNavigate()

  useEffect(() => {
    if (isAuthenticated == AuthenticateState.AUTHENTICATED) {
      navigate(path.home)
    }
  }, [])
  // TODO useRoutes
  if (isAuthenticated == AuthenticateState.AUTHENTICATED) {
    return null
  }
  return (
    <AuthLayout>
      <Outlet />
    </AuthLayout>
  )
}

export default AuthRoutes
