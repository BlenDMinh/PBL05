import classNames from "classnames"
import { useState } from "react"
import AdminActiveAccount from "./components/AdminActiveAccount"
import AdminLockedAccount from "./components/AdminLockedAccount"

export default function AdminAccount() {
    const [tab, setTab] = useState(0)

    return <>
        <div className="w-full h-full flex flex-col px-10 gap-10">
            <div role="tablist" className="tabs tabs-lg tabs-bordered w-1/2">
                <button role="tab" className={classNames("tab", 
                    { 'tab-active': tab === 0 }
                )} onClick={() => setTab(0)}>Active</button>
                <button role="tab" className={classNames("tab", 
                    { 'tab-active': tab === 1 }
                )} onClick={() => setTab(1)}>Locked</button>
            </div>
            {
                tab === 0 && <AdminActiveAccount />
            }
            {
                tab === 1 && <AdminLockedAccount />
            }
        </div>
    </>
}