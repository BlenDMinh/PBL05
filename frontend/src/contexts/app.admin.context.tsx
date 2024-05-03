import { createContext, useContext, useState } from "react"
import { ReactWithChild } from "src/interface/app"

export interface AdminAppContextType {
    showSidebar: boolean,
    theme: 'forest' | 'pastel'
    setShowSidebar: React.Dispatch<React.SetStateAction<boolean>>
}
const initContext: AdminAppContextType = {
    showSidebar: true,
    theme: 'forest',
    setShowSidebar: () => null
}

const AdminAppContext = createContext<AdminAppContextType>(initContext)

export default AdminAppContext

export const AdminAppContextProvider = ({ children }: ReactWithChild) => {
    const [showSidebar, setShowSidebar] = useState<boolean>(initContext.showSidebar)
    const [theme, setTheme] = useState<'forest' | 'pastel'>(initContext.theme)

    return (
        <AdminAppContext.Provider value={{ showSidebar, theme, setShowSidebar }}>
            {children}
        </AdminAppContext.Provider>
    )
}

export function useAdminApp() {
    const context = useContext(AdminAppContext)

    if (context === undefined) {
        throw new Error('useAdminApp must be used within AdminAppContextProvider')
    }

    return context
}