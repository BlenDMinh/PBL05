import { authImage1 } from 'src/assets/images'
import { path } from 'src/constants/path'

export type AuthSlide = {
  id: number
  img: string
}
export type PoliciesOption = {
  id: number
  title: string
  to: string
}

export const authSlides: AuthSlide[] = [
  {
    id: 1,
    img: authImage1
  }
]

export const policiesOptions: PoliciesOption[] = [
  {
    id: 1,
    title: 'Terms & Condition',
    to: path.home
  },
  {
    id: 2,
    title: 'Privacy Policy',
    to: path.home
  },
  {
    id: 3,
    title: 'Help',
    to: path.home
  },
  {
    id: 4,
    title: 'English',
    to: path.home
  }
]
