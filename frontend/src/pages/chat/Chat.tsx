import ChatBox from "./components/ChatBox";
import ChatList from "./components/ChatList";

export default function Chat() {
    return (<div className="flex w-full h-full">
        <div className="w-1/4 h-full">
            <ChatList />
        </div>
        <div className="w-3/4 h-full">
            <ChatBox />
        </div>
    </div>)
}