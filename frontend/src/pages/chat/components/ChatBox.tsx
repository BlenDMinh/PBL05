import { FaPaperPlane } from "react-icons/fa";
import Input from "src/components/input/Input";
import Message from "./Message";
import { useState } from "react";

export default function ChatBox() {
    const [message, setMessage] = useState("")
    const send = () => {
        console.log(message)
        setMessage("")
    }
    return <>
        <div className="w-full h-full flex flex-col">
            <div className="flex-1 flex flex-col-reverse gap-2 overflow-y-auto p-5">
                <Message side="left" />
                <Message side="right" />
                <Message side="left" />
                <Message side="left" />
                <Message side="right" />
                <Message side="left" />
                <Message side="left" />
                <Message side="right" />
                <Message side="left" />
                <Message side="left" />
                <Message side="right" />
            </div>
            <div className="w-full flex p-2 gap-2">
                <input className="input input-bordered input-primary w-full" type="text" value={message} onChange={(e) => {
                    setMessage(e.target.value)
                }} />
                <button className="btn btn-circle btn-active btn-primary text-xl text-white"
                    onClick={send}
                >
                    <FaPaperPlane />
                </button>
            </div>
        </div>
    </>
}