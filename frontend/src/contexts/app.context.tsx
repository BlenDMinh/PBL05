import { createContext, useEffect, useState } from 'react'
import authApi from 'src/apis/auth.api'
import HttpStatusCode from 'src/constants/httpStatusCode.enum'
import { ReactWithChild } from 'src/interface/app'
import { User } from 'src/types/users.type'
import { clearLS, getAccessTokenFromLS, getProfileFromLS, getSessionIdFromLS } from 'src/utils/auth'

export interface AppContextType {
  isAuthenticated: boolean
  setIsAuthenticated: React.Dispatch<React.SetStateAction<boolean>>
  showSidebar: boolean
  setShowSidebar: React.Dispatch<React.SetStateAction<boolean>>
  user: User | null
  setUser: React.Dispatch<React.SetStateAction<User | null>>
}

const initAppContext: AppContextType = {
  isAuthenticated: Boolean(getSessionIdFromLS()),
  setIsAuthenticated: () => null,
  showSidebar: true,
  setShowSidebar: () => null,
  user: getProfileFromLS(),
  setUser: () => null
}

export const AppContext = createContext<AppContextType>(initAppContext)

const AppContextProvider = ({ children }: ReactWithChild) => {
  const [isAuthenticated, setIsAuthenticated] = useState<boolean>(initAppContext.isAuthenticated)
  const [showSidebar, setShowSidebar] = useState<boolean>(initAppContext.showSidebar)
  const [user, setUser] = useState<User | null>(initAppContext.user)

  useEffect(() => {
    authApi.loginFromSessionId().then((res) => {
      if (res.status != HttpStatusCode.Ok) {
        // setIsAuthenticated(false)
        // setUser(null)
        // clearLS()
      }
    })
  }, [])

  return (
    <AppContext.Provider value={{ isAuthenticated, setIsAuthenticated, showSidebar, setShowSidebar, user, setUser }}>
      {children}
    </AppContext.Provider>
  )
}

export default AppContextProvider
