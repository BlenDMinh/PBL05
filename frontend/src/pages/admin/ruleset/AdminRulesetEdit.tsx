import { useEffect, useMemo, useState } from "react"
import { useNavigate, useParams, useSearchParams } from "react-router-dom"
import rulesetAdminApi from "src/apis/admin/ruleset.admin.api"
import { CKEditor } from '@ckeditor/ckeditor5-react';
import ClassicEditor from '@ckeditor/ckeditor5-build-classic';
import { GameRuleset } from "src/types/game.type";
import { path } from "src/constants/path";
import { toast } from "react-toastify";

export default function AdminRulesetEdit() {
    const [searchParams, setSearchParams] = useSearchParams()
    const id = searchParams.get('id')
    const copyFrom = searchParams.get('copy-from')

    const [ruleset, setRuleset] = useState<GameRuleset>()
    const [isDoing, setIsDoing] = useState(false)

    const navigate = useNavigate()

    useEffect(() => {
        if (id) {
            // fetch ruleset by id
            rulesetAdminApi.getRuleset(parseInt(id)).then(ruleset => setRuleset(ruleset.data))
            return
        }
        if (copyFrom) {
            // fetch ruleset by id
            rulesetAdminApi.getRuleset(parseInt(copyFrom)).then(ruleset => setRuleset({...ruleset.data, id: -1, name: ''}))
            return
        }
        setRuleset({
            id: 0,
            name: '',
            detail: {
                minute_per_turn: -1,
                total_minute_per_player: -1,
                turn_around_steps: -1,
                turn_around_time_plus: -1
            },
            published: false,
            createdAt: '',
        })
    }, [id, copyFrom])

    if (!ruleset) {
        return <div>Loading...</div>
    }

    return <>
        <div className="w-full h-full px-16 py-10">
            <div className="flex justify-end">
                <button className="btn btn-primary w-24" onClick={() => {
                    setIsDoing(true)
                    if(id) {
                        rulesetAdminApi.updateRuleset(parseInt(id), ruleset).then((res) => {
                            toast(res.data.message)
                            navigate(path.adminRuleset)
                        })
                    } else {
                        rulesetAdminApi.addRuleset(ruleset).then((res) => {
                            toast(res.data.message)
                            navigate(path.adminRuleset)
                        })
                    }
                }}>

                    {isDoing ? <span className="loading" /> : id ? 'Update' : 'Create'}
                </button>
            </div>
            {id && <span>ID: {id}</span>}
            <h3 className="text-base-content font-bold text-xl my-5">Basic</h3>
            <label className="form-control">
                <span className="label label-text">Name</span>
                <input className="input input-bordered" type="text" value={ruleset.name} onChange={(e) => {
                    setRuleset({...ruleset, name: e.target.value})
                }} />
            </label>
            <div className="flex w-full justify-evenly gap-10">
                <label className="form-control w-1/2">
                    <span className="label label-text">Minute per turn</span>
                    <input className="input input-bordered" type="number" value={ruleset.detail.minute_per_turn} onChange={(e) => {
                        setRuleset({...ruleset, detail: {...ruleset.detail, minute_per_turn: parseInt(e.target.value)}})
                    }} />
                </label>
                <label className="form-control w-1/2">
                    <span className="label label-text">Total Minute per Player</span>
                    <input className="input input-bordered" type="number" value={ruleset.detail.total_minute_per_player} onChange={(e) => {
                        setRuleset({...ruleset, detail: {...ruleset.detail, total_minute_per_player: parseInt(e.target.value)}})
                    }} />
                </label>
            </div>
            <div className="flex w-full justify-evenly gap-10">
                <label className="form-control w-1/2">
                    <span className="label label-text">Turn around time steps</span>
                    <input className="input input-bordered" type="number" value={ruleset.detail.turn_around_steps} onChange={(e) => {
                        setRuleset({...ruleset, detail: {...ruleset.detail, turn_around_steps: parseInt(e.target.value)}})
                    }} />
                </label>
                <label className="form-control w-1/2">
                    <span className="label label-text">Time plus</span>
                    <input className="input input-bordered" type="number" value={ruleset.detail.turn_around_time_plus} onChange={(e) => {
                        setRuleset({...ruleset, detail: {...ruleset.detail, turn_around_time_plus: parseInt(e.target.value)}})
                    }}/>
                </label>
            </div>
            <h3 className="text-base-content font-bold text-xl my-5">Description</h3>
            <div className="min-h-screen">
                <CKEditor
                    data={ruleset.description?.content ?? ''}
                    editor={ClassicEditor}
                    onChange={(event, editor) => {
                        console.log(editor.getData())
                    }}
                    onReady={(editor) => {
                        editor.editing.view.change((writer) => {
                            writer.setStyle(
                                "min-height",
                                "400px",
                                editor.editing.view.document.getRoot()!
                            );
                        });
                    }}
                >
                    
                </CKEditor>
            </div>
        </div>
    </>
}