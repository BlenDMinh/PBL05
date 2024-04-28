import { useState } from "react";
import { FaCamera } from "react-icons/fa";
import { toast } from "react-toastify";
import profileApi from "src/apis/profile.api";
import { blankAvatar } from "src/assets/images";

export interface AvatarModalProps {}

export default function AvatarModal(props: AvatarModalProps) {
    const [image, setImage] = useState(blankAvatar)
    const openModal = () => {
        const modal = document.getElementById('avatarModal') as HTMLDialogElement
        modal.showModal()
    }
    const upload = () => {
        if(image === blankAvatar) {
            toast.error('Please select an image')
            return
        }

        profileApi.uploadAvatar(image).then(res => {
            console.log(res)
            window.location.reload()
        })
        .catch(err => {
            console.log(err)
            toast.error('Upload failed')
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
                    }
                }} />
                <div className="modal-action w-full">
                    <form method="dialog" className="flex w-full justify-between">
                        <button className="btn btn-primary" type="button" onClick={upload}>Upload</button>
                        <button className="btn">Cancel</button>
                    </form>
                </div>
            </div>
        </dialog>
    </>
}