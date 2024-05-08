import { useMemo } from "react"
import { useParams, useSearchParams } from "react-router-dom"
import rulesetAdminApi from "src/apis/admin/ruleset.admin.api"
import { CKEditor } from '@ckeditor/ckeditor5-react';
import ClassicEditor from '@ckeditor/ckeditor5-build-classic';

export default function AdminRulesetEdit() {
    const [searchParams, setSearchParams] = useSearchParams()
    const id = searchParams.get('id')
    const copyFrom = searchParams.get('copy-from')

    console.log(id, copyFrom)

    const ruleset = useMemo(async () => {
        if (id) {
            // fetch ruleset by id
            const ruleset = await rulesetAdminApi.getRuleset(parseInt(id))
            return ruleset
        }
        if (copyFrom) {
            // fetch ruleset by id
            const ruleset = await rulesetAdminApi.getRuleset(parseInt(copyFrom))
            return { ...ruleset, id: null, name: '' }
        }
        return {
            id: 0,
            name: '',
            detail: {
                minute_per_turn: -1,
                total_minute_per_player: -1,
                turn_around_steps: -1,
                turn_around_time_plus: -1
            },
            published: false,
            createdAt: null,
        }
    }, [id, copyFrom])

    return <>
        <div className="w-full h-full px-16 py-10">
            {id && <span>ID: {id}</span>}
            <h3 className="text-base-content font-bold text-xl my-5">Basic</h3>
            <label className="form-control">
                <span className="label label-text">Name</span>
                <input className="input input-bordered" type="text" />
            </label>
            <div className="flex w-full justify-evenly gap-10">
                <label className="form-control w-1/2">
                    <span className="label label-text">Minute per turn</span>
                    <input className="input input-bordered" type="number" />
                </label>
                <label className="form-control w-1/2">
                    <span className="label label-text">Total Minute per Player</span>
                    <input className="input input-bordered" type="number" />
                </label>
            </div>
            <div className="flex w-full justify-evenly gap-10">
                <label className="form-control w-1/2">
                    <span className="label label-text">Turn around time steps</span>
                    <input className="input input-bordered" type="number" />
                </label>
                <label className="form-control w-1/2">
                    <span className="label label-text">Time plus</span>
                    <input className="input input-bordered" type="number" />
                </label>
            </div>
            <h3 className="text-base-content font-bold text-xl my-5">Description</h3>
            <div className="min-h-screen">
                <CKEditor
                    editor={ClassicEditor}
                    onChange={(event, editor) => {
                        console.log(editor.getData())
                    }}
                    onReady={(editor) => {
                        editor.editing.view.change((writer) => {
                            writer.setStyle(
                                "height",
                                "700px",
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