import { FaEllipsisVertical } from "react-icons/fa6"
import { GameRuleset } from "src/types/game.type"

interface RulesetRowProps {
    ruleset: GameRuleset,
    isPublished?: boolean
}

function RulesetRow(props: RulesetRowProps) {
    const ruleset = props.ruleset
    return <>
        <tr>
            <th>{ruleset.id}</th>
            <td>{ruleset.name}</td>
            <td>{ruleset.minutePerTurn ?? "N/A"}</td>
            <td>{ruleset.totalMinutePerPlayer}</td>
            <td>{ruleset.extraTimeSteps ?? "N/A"}</td>
            <td className="flex justify-between items-center">
                <span>{ruleset.extraTime ?? "N/A"}</span>
                {
                    !props.isPublished && <div className="dropdown dropdown-end">
                        <div tabIndex={0} role="button" className="btn btn-ghost">
                            <FaEllipsisVertical />
                        </div>
                        <ul tabIndex={0} className="dropdown-content z-[1] menu p-2 shadow bg-base-100 rounded-box">
                            <li><button className="btn btn-ghost">Publish</button></li>
                            <li><button className="btn btn-ghost">Edit</button></li>
                            <li><button className="btn btn-error">Delete</button></li>
                        </ul>
                    </div>
                }
                {
                    props.isPublished && <div className="dropdown dropdown-end">
                        <div tabIndex={0} role="button" className="btn btn-ghost">
                            <FaEllipsisVertical />
                        </div>
                        <ul tabIndex={0} className="dropdown-content z-[1] menu p-2 shadow bg-base-100 rounded-box">
                            <li><button className="btn btn-error">Unpublish</button></li>
                        </ul>
                    </div>
                }
            </td>
        </tr>
    </>
}

export default function AdminRuleset() {
    const publishedRulesets: GameRuleset[] = [
        {
            id: 0,
            name: 'Chơi 10 phút',
            minutePerTurn: null,
            totalMinutePerPlayer: 10,
            extraTimeSteps: null,
            extraTime: null
        },
        {
            id: 1,
            name: 'Tiêu chuẩn',
            minutePerTurn: null,
            totalMinutePerPlayer: 90,
            extraTimeSteps: 1,
            extraTime: 30
        }
    ]
    const rulesets: GameRuleset[] = [
        {
            id: 2,
            name: 'Chơi 5 phút',
            minutePerTurn: null,
            totalMinutePerPlayer: 5,
            extraTimeSteps: null,
            extraTime: null
        },
        {
            id: 3,
            name: 'Chơi 15 phút',
            minutePerTurn: null,
            totalMinutePerPlayer: 15,
            extraTimeSteps: null,
            extraTime: null
        },
        {
            id: 4,
            name: 'Chơi 30 phút',
            minutePerTurn: null,
            totalMinutePerPlayer: 30,
            extraTimeSteps: null,
            extraTime: null
        }
    ]
    return <>
        <div className="w-full h-full px-16 flex flex-col gap-10">
            <div className="bg-gradient-to-b rounded-xl from-secondary to-transparent p-px">
                <div className="h-full w-full rounded-xl bg-base-200 p-5">
                    <span className="text-xl font-bold">Published Ruleset</span>
                    <table className="table">
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Name</th>
                                <th>Minutes/turn</th>
                                <th>Total minutes/player</th>
                                <th>Extra time steps</th>
                                <th>Extra time (second)</th>
                            </tr>
                        </thead>
                        <tbody>
                            {publishedRulesets.map(ruleset => <RulesetRow ruleset={ruleset} isPublished />)}
                        </tbody>
                    </table>
                </div>
            </div>
            <div className="bg-gradient-to-b rounded-xl from-secondary to-transparent p-px">
                <div className="h-full w-full rounded-xl bg-base-200 p-5">
                    <div className="flex justify-between">
                        <span className="text-xl font-bold">Ruleset</span>
                        <button className="btn btn-primary">Add</button>
                    </div>
                    <table className="table">
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Name</th>
                                <th>Minutes/turn</th>
                                <th>Total minutes/player</th>
                                <th>Extra time steps</th>
                                <th>Extra time (second)</th>
                            </tr>
                        </thead>
                        <tbody>
                            {rulesets.map(ruleset => <RulesetRow ruleset={ruleset} />)}
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </>
}