export const path = {
  home: '/',
  auth: '/auth',
  login: '/auth/login',
  register: '/auth/register',
  patients: '/patients',
  patientDetail: '/patients/:patientId',
  patientDetailExamination: '/patients/:patientId/medical-records/:examinationId',
  schedules: '/schedules',
  surveys: '/surveys',
  personals: '/personals',
  game: '/game',
  gamev2: '/game/v2',
  pvp: '/game/:gameId',
  pvpv2: '/game/v2/:gameId',
  quickMatch: '/game/quickmatch',
  chat: '/chat'
} as const
