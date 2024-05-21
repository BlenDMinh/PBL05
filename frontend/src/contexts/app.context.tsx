import { createContext, useEffect, useMemo, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { toast } from 'react-toastify'
import useWebSocket from 'react-use-websocket'
import authApi from 'src/apis/auth.api'
import HttpStatusCode from 'src/constants/httpStatusCode.enum'
import { ws } from 'src/constants/ws'
import { ReactWithChild } from 'src/interface/app'
import { GameInvitationRequestMessage, GameRuleset, GameSocketMessage } from 'src/types/game.type'
import { User } from 'src/types/users.type'
import { clearLS, getAccessTokenFromLS, getSessionIdFromLS } from 'src/utils/auth'
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
  invitationMessage: GameInvitationRequestMessage | null
  invitationResponse: (response: 'accept' | 'reject', invitationId?: string) => void
  openConfirmToast: boolean
  setOpenConfirmToast: (value: boolean) => void
  inviting: boolean
  setInviting: (value: boolean) => void
}

const initAppContext: AppContextType = {
  isAuthenticated: AuthenticateState.UNKNOWN,
  certAuthenticated: undefined,
  setIsAuthenticated: () => null,
  showSidebar: false,
  setShowSidebar: () => null,
  user: null,
  setUser: () => null,
  inviteOpponent: () => null,
  invitationMessage: null,
  invitationResponse: () => null,
  openConfirmToast: false,
  setOpenConfirmToast: () => null,
  inviting: false,
  setInviting: () => null
}

export const AppContext = createContext<AppContextType>(initAppContext)

const AppContextProvider = ({ children }: ReactWithChild) => {
  const [certAuthenticated, setCertAuthenticated] = useState<boolean | undefined>(undefined)
  const [isAuthenticated, setIsAuthenticated] = useState<AuthenticateState>(initAppContext.isAuthenticated)
  const [showSidebar, setShowSidebar] = useState<boolean>(initAppContext.showSidebar)
  const [user, setUser] = useState<User | null>(initAppContext.user)
  const [openConfirmToast, setOpenConfirmToast] = useState<boolean>(initAppContext.openConfirmToast)
  const [invitationMessage, setInvitationMessage] = useState<GameInvitationRequestMessage | null>(
    initAppContext.invitationMessage
  )
  const [inviting, setInviting] = useState(false)
  const navigate = useNavigate()

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
          setUser(res.data.user)
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
        gameRule: {
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

  const invitationResponse = (response: 'accept' | 'reject', invitationId?: string) => {
    const message = {
      message: 'Invite to game response',
      data: {
        accept: response === 'accept',
        invitationId: invitationId ?? invitationMessage?.data.invitationId
      }
    }
    setInvitationMessage(null)
    setOpenConfirmToast(false)
    socket.sendJsonMessage(message)
  }

  useEffect(() => {
    if (socket.lastJsonMessage) {
      const data = socket.lastJsonMessage as GameSocketMessage
      if (data.message === 'Invite to game request') {
        setInvitationMessage(data as GameInvitationRequestMessage)
        setOpenConfirmToast(true)
      } else if (data.message === 'Game created') {
        const gameId = data.data
        navigate(`/game/v2/${gameId}`)
      } else if (data.message === 'Invitation rejected') {
        toast.error('Opponent rejected your invitation')
        setInviting(false)
      }
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
        invitationMessage,
        invitationResponse,
        openConfirmToast,
        setOpenConfirmToast,
        inviting,
        setInviting
      }}
    >
      {children}
    </AppContext.Provider>
  )
}

export default AppContextProvider
