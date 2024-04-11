import { FaPaperPlane } from "react-icons/fa";
import Input from "src/components/input/Input";

export default function ChatBox() {
    return <>
        <div className="w-full flex flex-col">
            <div className="flex-grow">
                aa
            </div>
            <div className="w-full flex p-2 gap-2 flex-grow-0">
                <input className="input input-bordered input-primary w-full" type="text" />
                <button className="btn btn-circle btn-active btn-primary text-xl text-white">
                    <FaPaperPlane />
                </button>
            </div>
        </div>
    </>
}