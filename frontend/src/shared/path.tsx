import Home from 'src/pages/home/Home'
import Login from 'src/pages/login/Login'
import Personal from 'src/pages/personal/Personal'
import Register from 'src/pages/register/Register'
import AppointmentSchedule from 'src/pages/schedule/Schedule'
import MedicalSurvey from 'src/pages/survey/Survey'
import { RouteObject } from 'react-router-dom'
import { path } from 'src/constants/path'

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
