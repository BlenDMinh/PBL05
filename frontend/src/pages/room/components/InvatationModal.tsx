import classNames from "classnames"
import { useState } from "react"
import { FaLink } from "react-icons/fa"

interface InvatationModalProps {
    content: string
}

export default function InvatationModal(props: InvatationModalProps) {
    const [copied, setCopied] = useState(false)
    const openLinkModal = () => {
        const modal = document.getElementById('link-modal') as HTMLDialogElement
        if (modal) {
            setCopied(false)
            modal.showModal()
        }
    }
    return <>
        <dialog id="link-modal" className="modal">
            <div className="modal-box">
                <h3 className="font-bold text-lg mb-5">Invitation link</h3>
                <div className="flex gap-5">
                    <input className="input input-bordered grow" type="text" value={props.content} />
                    <button className={classNames('btn', {
                        "btn-primary": !copied,
                        "btn-outline": copied
                    })} onClick={() => {
                        navigator.clipboard.writeText(props.content)
                        setCopied(true)
                    }}
                    >Copy</button>
                </div>
            </div>
            <form method="dialog" className="modal-backdrop">
                <button>close</button>
            </form>
        </dialog>
        <button className="btn btn-outline" onClick={openLinkModal}>
            <FaLink />
            Create Invatation Link
        </button>
    </>
}