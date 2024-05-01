import { useContext, useState } from "react";
import { FaCamera } from "react-icons/fa";
import { toast } from "react-toastify";
import profileApi from "src/apis/profile.api";
import { blankAvatar } from "src/assets/images";
import { AppContext, AppContextType } from "src/contexts/app.context";

export interface AvatarModalProps {}

export default function AvatarModal(props: AvatarModalProps) {
    const [imageData, setImageData] = useState<File | null>(null);
    const [image, setImage] = useState(blankAvatar)
    const [isUploading, setIsUploading] = useState(false)
    const { user, setUser } = useContext<AppContextType>(AppContext)
    const openModal = () => {
        const modal = document.getElementById('avatarModal') as HTMLDialogElement
        modal.showModal()
    }
    const upload = () => {
        if(imageData === null) {
            toast.error('Please select an image')
            return
        }

        setIsUploading(true)

        profileApi.uploadAvatar(imageData).then(res => {
            if(user) {
                user.avatarUrl = res.data.url
            }
            setUser(user)
            window.location.reload()
        })
        .catch(err => {
            toast.error('Upload failed')
        })
        .finally(() => {
            setIsUploading(false)
        })
    }
    return <>
        <button className='h-full w-full btn btn-primary btn-circle text-primary-content text-xl' onClick={openModal}>
            <FaCamera />
        </button>
        <dialog id="avatarModal" className="modal">
            <div className="modal-box flex flex-col items-center gap-5">
                <img className="avatar rounded-lg h-96 w-96 object-cover" src={image} alt="" />
                <input className="file-input file-input-bordered w-full" type="file" name="image" accept="image/*" onChange={(event) => {
                    if (event.target.files) {
                        console.log(event.target.files[0]);
                        setImage(URL.createObjectURL(event.target.files[0]));
                        setImageData(event.target.files[0]);
                    }
                }} />
                <div className="modal-action w-full">
                    <form method="dialog" className="flex w-full justify-between">
                        <button className="btn btn-primary" type="button" onClick={upload}>{isUploading ? <span className="loading loading-spinner" /> : 'Upload'}</button>
                        <button className="btn">Cancel</button>
                    </form>
                </div>
            </div>
        </dialog>
    </>
}