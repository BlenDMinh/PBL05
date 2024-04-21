import { createContext, useEffect, useState } from 'react'
import authApi from 'src/apis/auth.api'
import HttpStatusCode from 'src/constants/httpStatusCode.enum'
import { ReactWithChild } from 'src/interface/app'
import { User } from 'src/types/users.type'
import { clearLS, getAccessTokenFromLS, getProfileFromLS, getSessionIdFromLS } from 'src/utils/auth'
import http from 'src/utils/http'

export enum AuthenticateState {
  UNKNOWN,
  NOT_AUTHENTICATED,
  AUTHENTICATED
}
export interface AppContextType {
  isAuthenticated: AuthenticateState,
  certAuthenticated: boolean | undefined
  setIsAuthenticated: React.Dispatch<React.SetStateAction<AuthenticateState>>
  showSidebar: boolean
  setShowSidebar: React.Dispatch<React.SetStateAction<boolean>>
  user: User | null
  setUser: React.Dispatch<React.SetStateAction<User | null>>
}

const initAppContext: AppContextType = {
  isAuthenticated: AuthenticateState.UNKNOWN,
  certAuthenticated: undefined,
  setIsAuthenticated: () => null,
  showSidebar: false,
  setShowSidebar: () => null,
  user: getProfileFromLS(),
  setUser: () => null
}

export const AppContext = createContext<AppContextType>(initAppContext)

const AppContextProvider = ({ children }: ReactWithChild) => {
  const [certAuthenticated, setCertAuthenticated] = useState<boolean | undefined>(undefined)
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

    http.get('/').then((res) => {
      if (res.status === HttpStatusCode.Ok) {
        setCertAuthenticated(true)
      } else {
        setCertAuthenticated(false)
      }
    }).catch((e) => {
      setCertAuthenticated(false)
    })
  }, [])

  return (
    <AppContext.Provider value={{ isAuthenticated, certAuthenticated, setIsAuthenticated, showSidebar, setShowSidebar, user, setUser }}>
      {children}
    </AppContext.Provider>
  )
}

export default AppContextProvider
