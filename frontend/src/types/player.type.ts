export interface Player {
  side: 'white' | 'black'
}

export interface BotPlayer extends Player {
  difficulty: BotDifficulty
}

export interface HumanPlayer extends Player {
  id: number
  displayName: string
  avatarUrl?: string
  elo: number,
  remainMillis?: number
}

export enum BotDifficulty {
  EASIEST = 'EASIEST',
  EASY = 'EASY',
  MEDIUM = 'MEDIUM',
  HARD = 'HARD',
  HARDEST = 'HARDEST'
}
