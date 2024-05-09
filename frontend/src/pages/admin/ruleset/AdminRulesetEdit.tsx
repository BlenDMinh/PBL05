import { useEffect, useMemo, useState } from 'react'
import { useNavigate, useParams, useSearchParams } from 'react-router-dom'
import rulesetAdminApi from 'src/apis/admin/ruleset.admin.api'
import { CKEditor } from '@ckeditor/ckeditor5-react'
import ClassicEditor from '@ckeditor/ckeditor5-build-classic'
import { GameRuleset } from 'src/types/game.type'
import { path } from 'src/constants/path'
import { toast } from 'react-toastify'

export default function AdminRulesetEdit() {
  const [searchParams, setSearchParams] = useSearchParams()
  const id = searchParams.get('id')
  const copyFrom = searchParams.get('copy-from')

  const [ruleset, setRuleset] = useState<GameRuleset>()
  const [isDoing, setIsDoing] = useState(false)
  const [isMinutePerTurnCheck, setIsMinutePerTurnCheck] = useState(true)
  const [isTotalMinutePerPlayerCheck, setIsTotalMinutePerPlayerCheck] = useState(true)
  const [isExtraStepCheck, setIsExtraStepCheck] = useState(true)
  const [isExtraTimeCheck, setIsExtraTimeCheck] = useState(true)

  useEffect(() => {
    setRuleset((ruleset) => {
      if (ruleset && ruleset.detail) {
        const minute_per_turn = isMinutePerTurnCheck ? Math.max(0, ruleset.detail.minute_per_turn) : -1
        const total_minute_per_player = isTotalMinutePerPlayerCheck
          ? Math.max(0, ruleset.detail.total_minute_per_player)
          : -1
        const turn_around_steps = isExtraStepCheck ? Math.max(0, ruleset.detail.turn_around_steps) : -1
        const turn_around_time_plus = isExtraTimeCheck ? Math.max(0, ruleset.detail.turn_around_time_plus) : -1
        return {
          ...ruleset,
          detail: { minute_per_turn, total_minute_per_player, turn_around_steps, turn_around_time_plus }
        } as GameRuleset
      }
    })
  }, [isMinutePerTurnCheck, isTotalMinutePerPlayerCheck, isExtraStepCheck, isExtraTimeCheck])

  const navigate = useNavigate()

  useEffect(() => {
    if (ruleset && ruleset.detail) {
      setIsMinutePerTurnCheck(ruleset.detail.minute_per_turn != -1)
      setIsTotalMinutePerPlayerCheck(ruleset.detail.total_minute_per_player != -1)
      setIsExtraStepCheck(ruleset.detail.turn_around_steps != -1)
      setIsExtraTimeCheck(ruleset.detail.turn_around_time_plus != -1)
    }
  }, [ruleset])

  useEffect(() => {
    if (id) {
      // fetch ruleset by id
      rulesetAdminApi.getRuleset(parseInt(id)).then((ruleset) => setRuleset(ruleset.data))
      return
    }
    if (copyFrom) {
      // fetch ruleset by id
      rulesetAdminApi
        .getRuleset(parseInt(copyFrom))
        .then((ruleset) => setRuleset({ ...ruleset.data, id: -1, name: '' }))
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
      createdAt: ''
    })
  }, [id, copyFrom])

  if (!ruleset) {
    return <div>Loading...</div>
  }

  return (
    <>
      <div className='w-full h-full px-16 py-10'>
        <div className='flex justify-end'>
          <button
            className='btn btn-primary w-24'
            onClick={() => {
              setIsDoing(true)
              if (id) {
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
            }}
          >
            {isDoing ? <span className='loading' /> : id ? 'Update' : 'Create'}
          </button>
        </div>
        {id && <span>ID: {id}</span>}
        <h3 className='text-base-content font-bold text-xl my-5'>Basic</h3>
        <label className='form-control'>
          <span className='label label-text'>Name</span>
          <input
            className='input input-bordered'
            type='text'
            value={ruleset.name}
            onChange={(e) => {
              setRuleset({ ...ruleset, name: e.target.value })
            }}
          />
        </label>
        <div className='flex w-full justify-evenly gap-10'>
          <label className='form-control w-1/2'>
            <span className='label label-text'>Minute per turn</span>
            <div className='flex justify-start items-center'>
              <input
                className='input input-bordered'
                readOnly={!isMinutePerTurnCheck}
                type='number'
                value={isMinutePerTurnCheck ? ruleset.detail.minute_per_turn : ''}
                onChange={(e) => {
                  if (isMinutePerTurnCheck)
                    setRuleset({
                      ...ruleset,
                      detail: { ...ruleset.detail, minute_per_turn: Math.max(parseFloat(e.target.value), 0) }
                    })
                }}
              />
              <input
                type='checkbox'
                checked={isMinutePerTurnCheck}
                onChange={() => setIsMinutePerTurnCheck((s) => !s)}
                className='checkbox checkbox-primary'
              />
            </div>
          </label>
          <label className='form-control w-1/2'>
            <span className='label label-text'>Total Minute per Player</span>
            <div className='flex justify-start items-center'>
              <input
                className='input input-bordered'
                readOnly={!isMinutePerTurnCheck}
                type='number'
                value={isTotalMinutePerPlayerCheck ? ruleset.detail.total_minute_per_player : ''}
                onChange={(e) => {
                  if (isTotalMinutePerPlayerCheck)
                    setRuleset({
                      ...ruleset,
                      detail: { ...ruleset.detail, total_minute_per_player: Math.max(parseFloat(e.target.value), 0) }
                    })
                }}
              />{' '}
              <input
                type='checkbox'
                checked={isTotalMinutePerPlayerCheck}
                onChange={() => setIsTotalMinutePerPlayerCheck((s) => !s)}
                className='checkbox checkbox-primary'
              />
            </div>
          </label>
        </div>
        <div className='flex w-full justify-evenly gap-10'>
          <label className='form-control w-1/2'>
            <span className='label label-text'>Turn around time steps</span>{' '}
            <div className='flex justify-start items-center'>
              <input
                className='input input-bordered'
                type='number'
                readOnly={!isExtraStepCheck}
                value={isExtraStepCheck ? ruleset.detail.turn_around_steps : ''}
                onChange={(e) => {
                  if (isExtraStepCheck)
                    setRuleset({
                      ...ruleset,
                      detail: { ...ruleset.detail, turn_around_steps: Math.max(parseInt(e.target.value), 0) }
                    })
                }}
              />{' '}
              <input
                type='checkbox'
                checked={isExtraStepCheck}
                onChange={() => setIsExtraStepCheck((s) => !s)}
                className='checkbox checkbox-primary'
              />
            </div>
          </label>
          <label className='form-control w-1/2'>
            <span className='label label-text'>Time plus (second)</span>{' '}
            <div className='flex justify-start items-center'>
              <input
                className='input input-bordered'
                type='number'
                readOnly={!isExtraTimeCheck}
                value={isExtraTimeCheck ? ruleset.detail.turn_around_time_plus : ''}
                onChange={(e) => {
                  if (isExtraTimeCheck)
                    setRuleset({
                      ...ruleset,
                      detail: { ...ruleset.detail, turn_around_time_plus: Math.max(parseFloat(e.target.value), 0) }
                    })
                }}
              />{' '}
              <input
                type='checkbox'
                checked={isExtraTimeCheck}
                onChange={() => setIsExtraTimeCheck((s) => !s)}
                className='checkbox checkbox-primary'
              />
            </div>
          </label>
        </div>
        <h3 className='text-base-content font-bold text-xl my-5'>Description</h3>
        <div className='min-h-screen'>
          <CKEditor
            data={ruleset.description?.content ?? ''}
            editor={ClassicEditor}
            onChange={(event, editor) => {
              // console.log(editor.getData())
            }}
            onReady={(editor) => {
              editor.editing.view.change((writer) => {
                writer.setStyle('min-height', '400px', editor.editing.view.document.getRoot()!)
              })
            }}
          ></CKEditor>
        </div>
      </div>
    </>
  )
}
