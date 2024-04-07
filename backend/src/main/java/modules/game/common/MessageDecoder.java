package modules.game.common;

import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

import com.google.gson.Gson;

public class MessageDecoder implements Decoder.Text<GameMessageDto> {

    @Override
    public GameMessageDto decode(String jsonMessage) throws DecodeException {
        Gson gson = new Gson();
        GameMessageDto messageDto = gson.fromJson(jsonMessage, GameMessageDto.class);
        return messageDto;
    }

    @Override
    public boolean willDecode(String jsonMessage) {
        // This method can remain unchanged
        try {
            Gson gson = new Gson();
            gson.fromJson(jsonMessage, GameMessageDto.class);
            return true; // If parsing succeeds, return true
        } catch (Exception e) {
            return false; // If parsing fails, return false
        }
    }

    @Override
    public void init(EndpointConfig config) {
    }

    @Override
    public void destroy() {
    }

}
