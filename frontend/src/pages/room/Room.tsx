import { useParams } from 'react-router-dom'
import GameProfile from '../gamev2/components/GameProfile'
import Chessground from '@react-chess/chessground'
import classNames from 'classnames'
import { getProfileFromLS } from 'src/utils/auth'
import { useContext, useEffect, useMemo, useState } from 'react'
import { FaLink } from 'react-icons/fa'
import InvatationModal from './components/InvatationModal'
import FriendList from './components/FriendList'
import { useQuery } from 'react-query'
import profileApi from 'src/apis/profile.api'
import { HumanPlayer } from 'src/types/player.type'
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import { faChessKing, faQuestionCircle } from '@fortawesome/free-solid-svg-icons'
import rulesetApi from 'src/apis/ruleset.api'
import { AppContext } from 'src/contexts/app.context'

export default function Room() {
  const { id } = useParams()
  const user = getProfileFromLS()
  const [side, setSide] = useState<'white' | 'black' | 'random'>('white')
  const [keyword, setKeyword] = useState('')

  const userPlayer = useMemo(() => {
    const player = { ...user, side: side == 'random' ? 'white' : side, role: 'You' }
    return player
  }, [user, side])

  const [opponentId, setOpponentId] = useState<string>('')
  const opponent = useQuery(['opponent', opponentId], () => profileApi.getProfile(opponentId)).data?.data

  const opponentPlayer = useMemo(() => {
    const player = {
      ...opponent,
      side: side == 'random' ? 'black' : side == 'white' ? 'black' : ('white' as 'white' | 'black'),
      role: 'Opponent'
    }
    return player
  }, [opponent])

  const gamerules = useQuery('gamerules', () => rulesetApi.getRulesets()).data?.data
  if (gamerules) {
    gamerules.sort((a, b) => {
      if (a.id < b.id) {
        return -1
      }
      if (a.id > b.id) {
        return 1
      }
      return 0
    })
  }

  useEffect(() => {
    if (gamerules) setGameRuleId(gamerules[0].id + '')
  }, [gamerules])
  const app = useContext(AppContext)

  const [gameRuleId, setGameRuleId] = useState<string>('')

  const [rated, setRated] = useState(false)

  const [inviting, setInviting] = useState(false)

  const calcChessGroundSize = () => Math.min(window.innerHeight * 0.67, window.innerHeight * 0.67)

  return (
    <>
      <div className='w-full h-screen pt-32 pb-16 px-32 flex items-center justify-between'>
        <div
          style={{ width: `${calcChessGroundSize()}px` }}
          className={classNames(`flex flex-col rounded-lg items-center justify-center`)}
        >
          {opponent && (
            <div className='w-full flex items-center justify-between'>
              <GameProfile profile={opponentPlayer} role='Opponent' />
              <div className='rounded-lg border-base-300 border-2 bg-base-200'>
                <p className='text-lg m-2'>10:00</p>
              </div>
            </div>
          )}
          <div
            className={classNames(
              `min-w-[${Math.min(window.innerWidth, window.innerHeight) * 0.8}px] min-h-[${
                Math.min(window.innerWidth, window.innerHeight) * 0.8
              }px] pointer-events-none`,
              'hidden md:block'
            )}
          >
            <Chessground
              width={calcChessGroundSize()}
              height={calcChessGroundSize()}
              config={
                {
                  // orientation: game.me?.side === 'white' ? 'white' : 'black',
                }
              }
            />
          </div>
          <div className='w-full flex items-center justify-between'>
            <GameProfile profile={userPlayer} role='You' />
            <div className='rounded-lg border-base-300 border-2 bg-base-200'>
              <p className='text-lg m-2'>10:00</p>
            </div>
          </div>
        </div>
        <div className='rounded-lg bg-base-300 h-full w-1/2 flex flex-col p-6 gap-3'>
          {!opponentId && (
            <>
              <span className='text-base-content font-bold text-xl'>Find a friend</span>
              <input
                className='input'
                type='text'
                placeholder='Search'
                value={keyword}
                onChange={(e) => setKeyword(e.target.value)}
              />
              <div className='overflow-y-auto flex-1'>
                <FriendList
                  userId={user.id.toString()}
                  keyword={keyword}
                  selectCallback={(opponentId) => setOpponentId(opponentId)}
                />
              </div>
            </>
          )}
          {opponentId && (
            <>
              <div className='flex-1 flex flex-col w-full items-center gap-10'>
                <div>
                  <div>
                    <span className='text-base-content font-bold text-lg'>Your side to move</span>
                  </div>
                  <div className='flex gap-2'>
                    <div
                      className={classNames(
                        'cursor-pointer rounded-md w-12 h-12 flex justify-center items-center bg-gray-800',
                        {
                          'outline outline-green-600': side === 'white'
                        }
                      )}
                      onClick={() => setSide('white')}
                    >
                      <FontAwesomeIcon size='2x' icon={faChessKing} color='white'></FontAwesomeIcon>
                    </div>
                    <div
                      className={classNames('cursor-pointer rounded-md w-12 h-12 flex justify-center items-center', {
                        'outline outline-green-600': side === 'random'
                      })}
                      onClick={() => setSide('random')}
                    >
                      <FontAwesomeIcon size='2x' icon={faQuestionCircle}></FontAwesomeIcon>
                    </div>
                    <div
                      className={classNames(
                        'cursor-pointer rounded-md w-12 h-12 flex justify-center items-center bg-white',
                        {
                          'outline outline-green-600': side === 'black'
                        }
                      )}
                      onClick={() => setSide('black')}
                    >
                      <FontAwesomeIcon size='2x' icon={faChessKing} color='black'></FontAwesomeIcon>
                    </div>
                  </div>
                </div>
                <div>
                  <span className='text-base-content font-bold text-lg'>Gamerule</span>
                  <div>
                    <select className='select select-bordered' onChange={(e) => setGameRuleId(e.target.value)}>
                      {gamerules?.map((rule) => (
                        <option key={rule.id} value={rule.id} selected={rule.id === gamerules[0].id}>
                          {rule.name}
                        </option>
                      ))}
                    </select>
                  </div>
                </div>
                <div className='form-control'>
                  <label className='label cursor-pointer flex items-center gap-5'>
                    <span className='label-text font-bold'>Rated</span>
                    <input type='checkbox' className='toggle' checked={rated} onChange={(e) => setRated(!rated)} />
                  </label>
                </div>
              </div>

              <button
                className='btn btn-primary'
                onClick={() => {
                  app.inviteOpponent(
                    opponentId,
                    gamerules?.find((rule) => rule.id.toString() === gameRuleId)!,
                    side,
                    rated
                  )
                  setInviting(true)
                }}
              >
                {inviting ? <span className='loading loading-spinner' /> : 'Play'}
              </button>
            </>
          )}
          <InvatationModal content={`http://localhost:3000/room/${id}`} />
        </div>
      </div>
    </>
  )
}
