import axios, { AxiosError } from 'axios'
import dayjs from 'dayjs'
import HttpStatusCode from 'src/constants/httpStatusCode.enum'
import { ErrorResponse } from 'src/types/utils.type'

export function isAxiosError<T>(error: unknown): error is AxiosError<T> {
  // eslint-disable-next-line import/no-named-as-default-member
  return axios.isAxiosError(error)
}

export function isAxiosUnprocessableEntityError<FormError>(error: unknown): error is AxiosError<FormError> {
  return isAxiosError(error) && error.response?.status === HttpStatusCode.UnprocessableEntity
}

export function isAxiosUnauthorizedError<UnauthorizedError>(error: unknown): error is AxiosError<UnauthorizedError> {
  return isAxiosError(error) && error.response?.status === HttpStatusCode.Unauthorized
}

export function isAxiosExpiredTokenError<UnauthorizedError>(error: unknown): error is AxiosError<UnauthorizedError> {
  return (
    isAxiosUnauthorizedError<ErrorResponse<{ name: string; message: string }>>(error) &&
    error.response?.data?.data?.name === 'EXPIRED_TOKEN'
  )
}

export const formatDate = (date: string, formatType: string) => {
  return dayjs(date).format(formatType)
}

export const calcTimeDif = (from: any, to: any) => {
  // console.log(from, to)
  const fromDate = new Date(from).getTime()
  const toDate = new Date(to).getTime()
  // console.log(fromDate, toDate)
  const dif = toDate - fromDate
  const seconds = dif / 1000;
  const minutes = seconds / 60;
  if(minutes < 60)
      return `${Math.max(Math.floor(minutes), 1)} m`
  const hours = minutes / 60;
  if(hours < 24)
      return `${Math.floor(hours)} h`
  const days = hours / 24;
  if(days <= 60)
      return `${Math.floor(days)} D`
  const months = days / 30;
  if(months <= 24)
      return `${Math.floor(months)} M`
  const years = months / 12;
  return `${Math.floor(years)} Y`
}