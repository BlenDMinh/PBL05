import { GameRuleset } from "src/types/game.type"
import http from "src/utils/http"

export const URL_ADMIN_RULESETS = '/admin/rulesets'

const rulesetAdminApi = {
    getRulesets(published: boolean | null = null) {
        const url = published === null ? URL_ADMIN_RULESETS : `${URL_ADMIN_RULESETS}?published=${published}`
        return http.get<GameRuleset[]>(url)
    },
    getRuleset(id: number) {
        return http.get<GameRuleset>(`${URL_ADMIN_RULESETS}/${id}`)
    },
    addRuleset(ruleset: GameRuleset) {
        return http.post(URL_ADMIN_RULESETS, ruleset)
    },
    updateRuleset(id: number, ruleset: GameRuleset) {
        return http.put(`${URL_ADMIN_RULESETS}/${id}`, ruleset)
    },
    delateRuleset(id: number) {
        return http.delete(`${URL_ADMIN_RULESETS}/${id}`)
    }
}

export default rulesetAdminApi