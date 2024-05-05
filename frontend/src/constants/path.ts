export const path = {
  home: '/',
  auth: '/auth',
  login: '/auth/login',
  register: '/auth/register',
  verify: '/auth/verify/:id',
  patients: '/patients',
  patientDetail: '/patients/:patientId',
  patientDetailExamination: '/patients/:patientId/medical-records/:examinationId',
  personals: '/personals',
  game: '/game',
  gamev2: '/game/v2',
  pvp: '/game/:gameId',
  pvpv2: '/game/v2/:gameId',
  botv2: '/game/v2/bot/:gameId',
  quickMatch: '/game/v2/quickmatch',
  playWithBot: '/game/v2/play-with-bot',
  chat: '/chat',
  chatbox: '/chat/:id',
  friend: '/friend',
  profile: '/profile/:id',
  admin: '/admin',
  adminRuleset: '/admin/ruleset',
  adminAccount: '/admin/account',
} as const
