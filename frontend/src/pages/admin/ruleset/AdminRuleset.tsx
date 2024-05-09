import { ReactNode, useEffect, useMemo, useState } from 'react'
import { FaEllipsisVertical } from 'react-icons/fa6'
import { useMutation, useQuery } from 'react-query'
import { Link } from 'react-router-dom'
import rulesetAdminApi from 'src/apis/admin/ruleset.admin.api'
import { path } from 'src/constants/path'
import { GameRuleset } from 'src/types/game.type'

interface RulesetRowProps {
  ruleset: GameRuleset
  isPublished?: boolean
  onChange?: (ruleset: GameRuleset) => void
  openConfirmModal?: (message: ReactNode | string, onConfirm: () => void, onCancel?: () => void) => void
}

function RulesetRow(props: RulesetRowProps) {
  const ruleset = props.ruleset
  const detail = ruleset.detail

  const handlePublish = () => {
    props.openConfirmModal?.('Are you sure you want to publish this ruleset?', () => {
      const newRuleset = { ...ruleset, published: true }
      rulesetAdminApi.updateRuleset(ruleset.id, newRuleset).then(() => {
        props.onChange?.(newRuleset)
      })
    })
  }

  const handleUnpublish = () => {
    props.openConfirmModal?.('Are you sure you want to unpublish this ruleset?', () => {
      const newRuleset = { ...ruleset, published: false }
      rulesetAdminApi.updateRuleset(ruleset.id, newRuleset).then(() => {
        props.onChange?.(newRuleset)
      })
    })
  }

  const handleDelete = () => {
    props.openConfirmModal?.(
      <>
        Are you sure you want to delete this ruleset <b>forever</b>?
      </>,
      () => {
        rulesetAdminApi.deleteRuleset(ruleset.id).then(() => {
          props.onChange?.(ruleset)
        })
      }
    )
  }

  return (
    <>
      <tr>
        <th>{ruleset.id}</th>
        <td>{ruleset.name}</td>
        <td>{detail.minute_per_turn != -1 ? detail.minute_per_turn : 'N/A'}</td>
        <td>{detail.total_minute_per_player != -1 ? detail.total_minute_per_player : 'N/A'}</td>
        <td>{detail.turn_around_steps != -1 ? detail.turn_around_steps : 'N/A'}</td>
        <td className='flex justify-between items-center'>
          <span>{detail.turn_around_time_plus != -1 ? detail.turn_around_time_plus : 'N/A'}</span>
          {!props.isPublished && (
            <div className='dropdown dropdown-end'>
              <div tabIndex={0} role='button' className='btn btn-ghost'>
                <FaEllipsisVertical />
              </div>
              <ul tabIndex={0} className='dropdown-content z-[1] menu p-2 shadow bg-base-100 rounded-box'>
                <li>
                  <button className='btn btn-ghost' onClick={handlePublish}>
                    Publish
                  </button>
                </li>
                <li>
                  <Link to={`${path.adminRulesetEdit}?id=${ruleset.id}`} className='btn btn-ghost'>
                    Edit
                  </Link>
                </li>
                <li>
                  <button className='btn btn-error' onClick={handleDelete}>
                    Delete
                  </button>
                </li>
              </ul>
            </div>
          )}
          {props.isPublished && (
            <div className='dropdown dropdown-end'>
              <div tabIndex={0} role='button' className='btn btn-ghost'>
                <FaEllipsisVertical />
              </div>
              <ul tabIndex={0} className='dropdown-content z-[1] menu p-2 shadow bg-base-100 rounded-box'>
                <li>
                  <button className='btn btn-error' onClick={handleUnpublish}>
                    Unpublish
                  </button>
                </li>
              </ul>
            </div>
          )}
        </td>
      </tr>
    </>
  )
}

export default function AdminRuleset() {
  const rulesetsMutation = useMutation({
    mutationFn: () => rulesetAdminApi.getRulesets().then((res) => res.data)
  })

  const rulesets = rulesetsMutation.data

  const publishedRulesets = useMemo(() => rulesets?.filter((ruleset) => ruleset.published), [rulesets])
  const unpublishedRulesets = useMemo(() => rulesets?.filter((ruleset) => !ruleset.published), [rulesets])
  if (publishedRulesets)
    publishedRulesets.sort((a, b) => {
      if (a.id < b.id) {
        return -1
      }
      if (a.id > b.id) {
        return 1
      }
      return 0
    })

  if (unpublishedRulesets)
    unpublishedRulesets.sort((a, b) => {
      if (a.id < b.id) {
        return -1
      }
      if (a.id > b.id) {
        return 1
      }
      return 0
    })
  const [modalMessage, setModalMessage] = useState<ReactNode | string>('')
  const [onConfirm, setOnConfirm] = useState<() => void>(() => () => {})
  const [onCancel, setOnCancel] = useState<() => void>(() => () => {})

  const [copyFrom, setCopyFrom] = useState<number | null>(null)

  const openConfirmModal = (message: ReactNode | string, onConfirm: () => void, onCancel?: () => void) => {
    setModalMessage(message)
    setOnConfirm(() => onConfirm)
    setOnCancel(
      () =>
        onCancel ??
        (() => {
          console.log('Cancel')
        })
    )

    const modal = document.getElementById('confirm-modal') as HTMLDialogElement
    modal.showModal()
  }

  const openAddModal = () => {
    const modal = document.getElementById('add-modal') as HTMLDialogElement
    modal.showModal()
  }

  useEffect(() => {
    rulesetsMutation.mutate()
  }, [])

  if (!rulesets) return <div className='loading loading-lg'></div>

  return (
    <>
      <dialog id='add-modal' className='modal'>
        <div className='modal-box'>
          <h3 className='font-bold text-lg'>Add ruleset</h3>
          <div className='flex w-full gap-5 h-28 justify-evenly'>
            <div className='w-1/2 flex items-center justify-center'>
              <Link to={path.adminRulesetEdit} className='btn btn-primary'>
                Add new
              </Link>
            </div>
            <div className='divider divider-horizontal'>OR</div>
            <div className='h-full w-1/2 flex flex-col justify-evenly'>
              <span>Copy from</span>
              <select className='select select-bordered' onChange={(e) => setCopyFrom(parseInt(e.target.value))}>
                <option value={-1}>Select ruleset</option>
                {rulesets.map((ruleset) => (
                  <option key={ruleset.id} value={ruleset.id}>
                    {ruleset.name}
                  </option>
                ))}
              </select>
            </div>
          </div>
          <div className='modal-action'>
            <form method='dialog' className='w-1/2 flex justify-evenly gap-5'>
              <button className='btn grow'>Cancel</button>
              {copyFrom !== null && (
                <Link to={`${path.adminRulesetEdit}?copy-from=${copyFrom}`} className='btn btn-success grow'>
                  Confirm
                </Link>
              )}
            </form>
          </div>
        </div>
      </dialog>
      <dialog id='confirm-modal' className='modal'>
        <div className='modal-box'>
          <h3 className='font-bold text-lg'>Confirmation</h3>
          <p className='py-4'>{modalMessage}</p>
          <div className='modal-action'>
            <form method='dialog' className='w-1/2 flex justify-evenly gap-5'>
              <button className='btn grow' onClick={onCancel}>
                Cancel
              </button>
              <button className='btn btn-success grow' onClick={onConfirm}>
                Confirm
              </button>
            </form>
          </div>
        </div>
      </dialog>
      <div className='w-full h-full px-16 flex flex-col gap-10'>
        <div className='bg-gradient-to-b rounded-xl from-secondary to-transparent p-px'>
          <div className='h-full w-full rounded-xl bg-base-200 p-5'>
            <span className='text-xl font-bold'>Published Ruleset</span>
            <table className='table'>
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Name</th>
                  <th>Minutes/turn</th>
                  <th>Total minutes/player</th>
                  <th>Turn around steps</th>
                  <th>Time plus (second)</th>
                </tr>
              </thead>
              <tbody>
                {publishedRulesets ? (
                  publishedRulesets.map((ruleset) => (
                    <RulesetRow
                      key={ruleset.id}
                      ruleset={ruleset}
                      isPublished
                      openConfirmModal={openConfirmModal}
                      onChange={() => rulesetsMutation.mutate()}
                    />
                  ))
                ) : (
                  <span className='loading loading-lg' />
                )}
              </tbody>
            </table>
          </div>
        </div>
        <div className='bg-gradient-to-b rounded-xl from-secondary to-transparent p-px'>
          <div className='h-full w-full rounded-xl bg-base-200 p-5'>
            <div className='flex justify-between'>
              <span className='text-xl font-bold'>Ruleset</span>
              <button className='btn btn-primary' onClick={openAddModal}>
                Add
              </button>
            </div>
            <table className='table'>
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Name</th>
                  <th>Minutes/turn</th>
                  <th>Total minutes/player</th>
                  <th>Turn around steps</th>
                  <th>Time plus (second)</th>
                </tr>
              </thead>
              <tbody>
                {unpublishedRulesets ? (
                  unpublishedRulesets.map((ruleset) => (
                    <RulesetRow
                      key={ruleset.id}
                      ruleset={ruleset}
                      openConfirmModal={openConfirmModal}
                      onChange={() => rulesetsMutation.mutate()}
                    />
                  ))
                ) : (
                  <span className='loading loading-lg' />
                )}
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </>
  )
}
