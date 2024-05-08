import { createContext, useEffect, useMemo, useState } from 'react'
import { toast } from 'react-toastify'
import useWebSocket from 'react-use-websocket'
import authApi from 'src/apis/auth.api'
import HttpStatusCode from 'src/constants/httpStatusCode.enum'
import { ws } from 'src/constants/ws'
import { ReactWithChild } from 'src/interface/app'
import { GameRuleset } from 'src/types/game.type'
import { User } from 'src/types/users.type'
import { clearLS, getAccessTokenFromLS, getProfileFromLS, getSessionIdFromLS } from 'src/utils/auth'
import http from 'src/utils/http'

export enum AuthenticateState {
  UNKNOWN,
  NOT_AUTHENTICATED,
  AUTHENTICATED
}
export interface AppContextType {
  isAuthenticated: AuthenticateState
  certAuthenticated: boolean | undefined
  setIsAuthenticated: React.Dispatch<React.SetStateAction<AuthenticateState>>
  showSidebar: boolean
  setShowSidebar: React.Dispatch<React.SetStateAction<boolean>>
  user: User | null
  setUser: React.Dispatch<React.SetStateAction<User | null>>
  inviteOpponent: (
    opponentId: string,
    gamerule: GameRuleset,
    side: 'white' | 'black' | 'random',
    rated: boolean
  ) => void
  openConfirmToast: boolean
  setOpenConfirmToast: (value: boolean) => void
}

const initAppContext: AppContextType = {
  isAuthenticated: AuthenticateState.UNKNOWN,
  certAuthenticated: undefined,
  setIsAuthenticated: () => null,
  showSidebar: false,
  setShowSidebar: () => null,
  user: getProfileFromLS(),
  setUser: () => null,
  inviteOpponent: () => null,
  openConfirmToast: false,
  setOpenConfirmToast: () => null
}

export const AppContext = createContext<AppContextType>(initAppContext)

const AppContextProvider = ({ children }: ReactWithChild) => {
  const [certAuthenticated, setCertAuthenticated] = useState<boolean | undefined>(undefined)
  const [isAuthenticated, setIsAuthenticated] = useState<AuthenticateState>(initAppContext.isAuthenticated)
  const [showSidebar, setShowSidebar] = useState<boolean>(initAppContext.showSidebar)
  const [user, setUser] = useState<User | null>(initAppContext.user)
  const [openConfirmToast, setOpenConfirmToast] = useState<boolean>(initAppContext.openConfirmToast)

  useEffect(() => {
    authApi
      .loginFromSessionId()
      .then((res) => {
        if (res.status != HttpStatusCode.Ok) {
          setIsAuthenticated(AuthenticateState.NOT_AUTHENTICATED)
          setUser(null)
          clearLS()
        } else {
          setIsAuthenticated(AuthenticateState.AUTHENTICATED)
        }
      })
      .catch((e) => {
        setIsAuthenticated(AuthenticateState.NOT_AUTHENTICATED)
        setUser(null)
        clearLS()
      })

    http
      .get('/')
      .then((res) => {
        if (res.status === HttpStatusCode.Ok) {
          setCertAuthenticated(true)
        } else {
          setCertAuthenticated(false)
        }
      })
      .catch((e) => {
        setCertAuthenticated(false)
      })
  }, [])

  const wsUrl = useMemo(() => ws.general(getSessionIdFromLS()), [isAuthenticated])
  const socket = useWebSocket(wsUrl)

  const inviteOpponent = (
    opponentId: string,
    gamerule: GameRuleset,
    side: 'white' | 'black' | 'random',
    rated: boolean
  ) => {
    const message = {
      message: 'Invite to game request',
      data: {
        opponentId: opponentId,
        gamerule: {
          id: gamerule.id,
          ruleName: gamerule.name,
          minutePerTurn: gamerule.detail.minute_per_turn,
          totalMinutePerPlayer: gamerule.detail.total_minute_per_player,
          turnAroundStep: gamerule.detail.turn_around_steps,
          turnAroundPlusTime: gamerule.detail.turn_around_time_plus
        },
        meSide: side,
        rated: rated
      }
    }
    socket.sendJsonMessage(message)
  }

  useEffect(() => {
    if (socket.lastJsonMessage) {
      const data = socket.lastJsonMessage
      setOpenConfirmToast(true)
    }
  }, [socket.lastJsonMessage])

  return (
    <AppContext.Provider
      value={{
        isAuthenticated,
        certAuthenticated,
        setIsAuthenticated,
        showSidebar,
        setShowSidebar,
        user,
        setUser,
        inviteOpponent,
        openConfirmToast,
        setOpenConfirmToast
      }}
    >
      {children}
    </AppContext.Provider>
  )
}

export default AppContextProvider
