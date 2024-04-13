import { createContext, useEffect, useState } from 'react'
import authApi from 'src/apis/auth.api'
import HttpStatusCode from 'src/constants/httpStatusCode.enum'
import { ReactWithChild } from 'src/interface/app'
import { User } from 'src/types/users.type'
import { clearLS, getAccessTokenFromLS, getProfileFromLS, getSessionIdFromLS } from 'src/utils/auth'

export enum AuthenticateState {
  UNKNOWN,
  NOT_AUTHENTICATED,
  AUTHENTICATED
}
export interface AppContextType {
  isAuthenticated: AuthenticateState
  setIsAuthenticated: React.Dispatch<React.SetStateAction<AuthenticateState>>
  showSidebar: boolean
  setShowSidebar: React.Dispatch<React.SetStateAction<boolean>>
  user: User | null
  setUser: React.Dispatch<React.SetStateAction<User | null>>
}

const initAppContext: AppContextType = {
  isAuthenticated: AuthenticateState.UNKNOWN,
  setIsAuthenticated: () => null,
  showSidebar: true,
  setShowSidebar: () => null,
  user: getProfileFromLS(),
  setUser: () => null
}

export const AppContext = createContext<AppContextType>(initAppContext)

const AppContextProvider = ({ children }: ReactWithChild) => {
  const [isAuthenticated, setIsAuthenticated] = useState<AuthenticateState>(initAppContext.isAuthenticated)
  const [showSidebar, setShowSidebar] = useState<boolean>(initAppContext.showSidebar)
  const [user, setUser] = useState<User | null>(initAppContext.user)

  useEffect(() => {
    authApi.loginFromSessionId().then((res) => {
      if (res.status != HttpStatusCode.Ok) {
        setIsAuthenticated(AuthenticateState.NOT_AUTHENTICATED)
        setUser(null)
        clearLS()
      } else {
        setIsAuthenticated(AuthenticateState.AUTHENTICATED)
      }
    }).catch(e => {
      setIsAuthenticated(AuthenticateState.NOT_AUTHENTICATED)
      setUser(null)
      clearLS()
    })
  }, [])

  return (
    <AppContext.Provider value={{ isAuthenticated, setIsAuthenticated, showSidebar, setShowSidebar, user, setUser }}>
      {children}
    </AppContext.Provider>
  )
}

export default AppContextProvider
