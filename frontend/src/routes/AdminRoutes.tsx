import { Outlet } from "react-router-dom"
import AdminDefaultLayout from "src/layouts/AdminDefaultLayout"

export default function AdminRoutes() {
    // const { isAuthenticated } = useContext<AppContextType>(AppContext)
    // const navigate = useNavigate()
    // const loc = useLocation()
  
    // useEffect(() => {
    //   if (isAuthenticated == AuthenticateState.UNKNOWN) return
    //   if (isAuthenticated != AuthenticateState.AUTHENTICATED) {
    //     if (loc.pathname !== path.home) navigate(path.login)
    //   }
    // }, [isAuthenticated])
    // TODO useRoutes
    // if (!isAuthenticated) {
    //   return null
    // }
  
    return (
      <AdminDefaultLayout>
        <Outlet />
      </AdminDefaultLayout>
    )
}