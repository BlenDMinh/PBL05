package modules.chat;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

import com.google.gson.Gson;

import modules.chat.dto.MessageResponseDto;

public class MessageEncoder implements Encoder.Text<MessageResponseDto> {

    @Override
    public String encode(MessageResponseDto message) throws EncodeException {
        Gson gson = new Gson();
        return gson.toJson(message);
    }

    @Override
    public void init(EndpointConfig config) {
    }

    @Override
    public void destroy() {
    }

}
