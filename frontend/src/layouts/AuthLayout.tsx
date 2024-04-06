import { Link } from 'react-router-dom'
import { path } from 'src/constants/path'
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import { faChessKing } from '@fortawesome/free-solid-svg-icons'
import { ReactWithChild } from 'src/interface/app'

export default function AuthLayout({ children }: ReactWithChild) {
  return (
    <div className='grid min-h-screen'>
      <section className='col-span-full flex flex-col justify-between lg:p-2 bg-gray-100'>
        <div className='ml-2 md:ml-0 flex items-center'>
          <Link to={path.home}>
            <FontAwesomeIcon icon={faChessKing} size='6x' style={{ color: '#0066CC' }} />
          </Link>
          <div className='ml-4 flex flex-col pt-10'>
            <h3 className='pl-0 text-4xl font-semibold md:pl-8 xl:pl-0'>
              CH<span className='text-primary'>ESS</span>
            </h3>
            <p className='text-gray-600 text-sm md:text-base'>Welcome to our platform</p>
          </div>
        </div>
        {children}
      </section>
    </div>
  )
}
