import Home from 'src/pages/home/Home'
import Login from 'src/pages/login/Login'
import Personal from 'src/pages/personal/Personal'
import Register from 'src/pages/register/Register'
import AppointmentSchedule from 'src/pages/schedule/Schedule'
import MedicalSurvey from 'src/pages/survey/Survey'
import { RouteObject } from 'react-router-dom'
import { path } from 'src/constants/path'
import QuickMatch from 'src/pages/quick-match/QuickMatch'
import ChessGame from 'src/game/ChessGame'
import Game from 'src/pages/game/Game'
import GameV2 from 'src/pages/gamev2/GameV2'
import Chat from 'src/pages/chat/Chat'
import BotGameV2 from 'src/pages/gamev2/BotGameV2'

// private routes (path, component)
export const AUTH_ROUTER: RouteObject[] = [
  {
    path: path.login,
    element: <Login />
  },
  {
    path: path.register,
    element: <Register />
  }
]

export const PRIVATE_ROUTER: RouteObject[] = [
  {
    path: path.home,
    element: <Home />
  },
  {
    path: path.chat,
    element: <Chat />
  },
  {
    path: path.chatbox,
    element: <Chat />
  },
  // {
  //   path: path.patients,
  //   element: <Patient />
  // },
  // {
  //   path: path.patientDetail,
  //   element: <PatientDetails />,
  //   children: [
  //     {
  //       path: path.patientDetail,
  //       element: <PatientListRecords />
  //     },
  //     {
  //       path: path.patientDetailExamination,
  //       element: <Examination />
  //     }
  //   ]
  // },
  {
    path: path.schedules,
    element: <AppointmentSchedule />
  },
  {
    path: path.surveys,
    element: <MedicalSurvey />
  },
  {
    path: path.personals,
    element: <Personal />
  }
]

export const GAME_ROUTES: RouteObject[] = [
  {
    path: path.pvp,
    element: <Game />
  },
  {
    path: path.quickMatch,
    element: <QuickMatch />
  }
]

export const GAME_V2_ROUTES: RouteObject[] = [
  {
    path: path.pvpv2,
    element: <GameV2 />
  },
  {
    path: path.botv2,
    element: <BotGameV2 />
  }
]
