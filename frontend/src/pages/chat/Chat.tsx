import { ChatContextProvider } from "src/contexts/chat.context";
import ChatBox from "./components/ChatBox";
import ChatList from "./components/ChatList";

export default function Chat() {
    return (
        <ChatContextProvider>
            <div className="flex w-full h-full">
                <div className="w-1/5 h-full">
                    <ChatList />
                </div>
                <div className="flex-1 h-full">
                    <ChatBox />
                </div>
            </div>
        </ChatContextProvider>
    )
}