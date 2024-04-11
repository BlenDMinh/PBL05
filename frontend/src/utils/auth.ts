import authApi from 'src/apis/auth.api'
import HttpStatusCode from 'src/constants/httpStatusCode.enum'
import { User } from 'src/types/users.type'

export const setAccessTokenToLS = (accessToken: string) => {
  localStorage.setItem('accessToken', accessToken)
}

export const setRefreshTokenToLS = (refreshToken: string) => {
  localStorage.setItem('refreshToken', refreshToken)
}

export const setSessionIdToLS = (sessionId: string) => {
  localStorage.setItem('jsessionid', sessionId)
}

export const setProfileToLS = (user: User) => {
  localStorage.setItem('profile', JSON.stringify(user))
}

export const clearLS = () => {
  localStorage.removeItem('jsessionid')
  localStorage.removeItem('accessToken')
  localStorage.removeItem('refreshToken')
  localStorage.removeItem('profile')
}

export const getAccessTokenFromLS = () => localStorage.getItem('accessToken') || ''

export const getRefreshTokenFromLS = () => localStorage.getItem('refreshToken') || ''

export const getSessionIdFromLS = () => localStorage.getItem('jsessionid') || ''

export const getProfileFromLS: () => User = () => {
  const result = localStorage.getItem('profile')
  if(!result)
    return null
  return JSON.parse(result)
}
