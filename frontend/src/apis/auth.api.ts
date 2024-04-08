import { LoginResponse, LoginReqBody, RegisterReqBody, RegisterResponse } from 'src/types/auth.type'
import http from 'src/utils/http'

export const URL_LOGIN = 'auth/login'
export const URL_REGISTER = 'auth/register/doctor'
export const URL_LOGOUT = 'logout'
export const URL_REFRESH_TOKEN = 'refresh-access-token'

const authApi = {
  register(body: RegisterReqBody) {
    return http.post<RegisterResponse>(URL_REGISTER, body)
  },
  login(body: LoginReqBody) {
    return http.post<LoginResponse>(URL_LOGIN, body)
  },
  loginFromSessionId() {
    return http.get<LoginResponse>(URL_LOGIN)
  },
  logout() {
    return http.post(URL_LOGOUT)
  }
}

export default authApi
