import { GameType } from 'src/contexts/gamev2.context'
import BaseGame from './BaseGame'

export default function BotGameV2() {
    return <BaseGame gameType={GameType.BOT} />
}