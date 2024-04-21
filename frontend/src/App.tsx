import { useContext, useEffect } from 'react'
import useRouteElements from './shared/hook/useRoutesElement'
import { ToastContainer } from 'react-toastify'
import 'react-toastify/dist/ReactToastify.css'
import { AppContext } from './contexts/app.context'

function App() {
  const app = useContext(AppContext)
  const routeElements = useRouteElements()
  const openModal = () => {
    const modal = document.getElementById('certModal') as HTMLDialogElement
    modal.showModal()
  }
  useEffect(() => {
    if(app.certAuthenticated === undefined) {
      return
    }
    if(app.certAuthenticated) {
      return
    }
    openModal()
  }, [app.certAuthenticated])
  return (
    <>
      <dialog id="certModal" className="modal">
        <div className="modal-box w-11/12 max-w-5xl">
          <h3 className="font-bold text-xl">Opps</h3>
          <p>It seems that our developers forgot to get a SSL Certificate</p>
          <br />
          <p className='font-bold'>Please manually accept our server by following these steps:</p>
          <div className='flex flex-col justify-evenly'>
            <div>
              1. Go to <a href={import.meta.env.VITE_API_URL}>{import.meta.env.VITE_API_URL}</a> and click on Advanced (or More)
              <img className='rounded-lg border-2 border-base-300' src="/cert-step-1.png" alt="" />
            </div>
            <div>
              2. Proceed to the following link
              <img className='rounded-lg border-2 border-base-300' src="/cert-step-2.png" alt="" />
            </div>
            <div>
              3. If you're seeing this page, go back to our website and Refresh
              <img className='rounded-lg border-2 border-base-300' src="/cert-step-3.png" alt="" />
            </div>
          </div>
          <div className="modal-action">
            <form method="dialog">
              <button className="btn" onClick={() => location.reload()}>Refresh</button>
            </form>
          </div>
        </div>
      </dialog>
      {routeElements}
      <ToastContainer
        position='top-right'
        autoClose={5000}
        newestOnTop={false}
        closeOnClick
        rtl={false}
        draggable
        pauseOnHover
        theme='light'
      />
    </>
  )
}

export default App
