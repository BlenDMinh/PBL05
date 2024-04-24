import classNames from "classnames";
import { ChangeEvent, useState } from "react";
import { FaEnvelopeOpen } from "react-icons/fa";
import { useNavigate, useParams } from "react-router-dom";
import { toast } from "react-toastify";
import authApi from "src/apis/auth.api";

export default function Verify() {
    const { id } = useParams()
    const [verifying, setVerifying] = useState(false)
    const navigate = useNavigate()

    const verify = (e: ChangeEvent<HTMLInputElement>) => {
        if (e.target.value.length === 6) {
            setVerifying(true)
            // Verify the code
            authApi.verifyEmail({ 
                registerId: id ?? "",
                code: e.target.value
             }).then(() => {
                setVerifying(false)
                toast.success("Email verified successfully")
                navigate("/auth/login")
             })
        }
    }

    return (
        <div className="flex flex-col h-full w-full items-center gap-5">
            <h1 className="font-bold text-3xl text-primary">Verify your email</h1>
            <FaEnvelopeOpen className="text-3xl" />
            <h2 className="text-lg">You are almost there. We have sent a <span className="font-bold">6-digits code</span> to your registed email. Visit your mail and complete the sign up</h2>
            <h2 className="text-lg">If you don't see it, try checking your spam folder</h2>
            <input className={classNames("input input-lg border-2 bg-base-200 font-bold text-lg focus:ring-0 focus:border-0", {
                "border-primary focus:border-primary focus:ring-primary": verifying,
                "border-base-300": !verifying
            })} name="code" type="text" inputMode="numeric" maxLength={6} pattern="[0-9\s]" 
            onChange={verify}
            />
            <div className="h-36">
                {verifying && <span className="loading loading-spinner loading-lg"></span>}
            </div>
            <h2>Still can't find the email? Don't worry, wait for 2 months until we get a Resend Button</h2>
            <div className="tooltip" data-tip='Currently not available'>
                <button className="btn">
                    Resend verification code
                </button>
            </div>
        </div>
    )
}