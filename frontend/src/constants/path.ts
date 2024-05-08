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
  room: '/game/v2/room/:id',
  chat: '/chat',
  chatbox: '/chat/:id',
  friend: '/friend',
  profile: '/profile/:id',
  admin: '/admin',
  adminRuleset: '/admin/ruleset',
  adminRulesetEdit: '/admin/ruleset/edit',
  adminAccount: '/admin/account',
} as const
