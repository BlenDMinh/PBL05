import { ReactWithChild } from "src/interface/app";
import Navbar from "./components/navbar";

export interface GameLayoutProps {

}

export default function GameLayout({ children }: ReactWithChild) {
    return <>
        <div className="bg-base">
            <Navbar />
            <div className="flex flex-col min-h-screen w-full items-center justify-center">
                {children}
            </div>
        </div>
    </>
}