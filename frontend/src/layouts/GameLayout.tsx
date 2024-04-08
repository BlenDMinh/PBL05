import { ReactWithChild } from "src/interface/app";

export interface GameLayoutProps {

}

export default function GameLayout({ children }: ReactWithChild) {
    return <>
        <div className="bg-base flex flex-col min-h-screen w-full items-center justify-center">
            {children}
        </div>
    </>
}