import { GameRuleset } from "src/types/game.type"
import http from "src/utils/http"

export const URL_RULESETS = '/rulesets'

const rulesetApi = {
    getRulesets() {
        return http.get<GameRuleset[]>(URL_RULESETS)
    },
    getRuleset(id: number) {
        return http.get<GameRuleset | null>(`${URL_RULESETS}/${id}`)
    }
}

export default rulesetApi