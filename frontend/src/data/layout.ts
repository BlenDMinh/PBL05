import { path } from 'src/constants/path'
import { PoliciesOption } from 'src/data/auth'
import { FaChessBishop } from 'react-icons/fa';

import { IconType } from 'react-icons'

interface QuickOptionNavbar extends PoliciesOption {
}
interface SidebarOption extends QuickOptionNavbar {
  icon: IconType
}

export const quickOptionsNavbar: QuickOptionNavbar[] = [
  {
    id: 1,
    title: 'Profile',
    to: '/'
  },
  {
    id: 2,
    title: 'Settings',
    to: '/'
  },
]

export const sidebarOption: SidebarOption[] = [
  {
    id: 1,
    title: 'Game',
    to: path.game,
    icon: FaChessBishop
  },
  {
    id: 2,
    title: 'Game',
    to: path.patients,
    icon: FaChessBishop
  }
]
