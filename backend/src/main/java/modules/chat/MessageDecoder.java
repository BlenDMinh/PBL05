package modules.chat;

import javax.websocket.DecodeException;
import javax.websocket.EndpointConfig;

import com.google.gson.Gson;

import modules.chat.dto.MessageRequestDto;

public class MessageDecoder implements javax.websocket.Decoder.Text<MessageRequestDto> {

    private final Gson gson = new Gson();

    @Override
    public MessageRequestDto decode(String jsonMessage) throws DecodeException {
        try {
            return gson.fromJson(jsonMessage, MessageRequestDto.class);
        } catch (Exception e) {
            throw new DecodeException(jsonMessage, "Error decoding JSON", e);
        }
    }

    @Override
    public boolean willDecode(String jsonMessage) {
        // Attempt to deserialize the JSON message
        try {
            gson.fromJson(jsonMessage, MessageRequestDto.class);
            return true; // If deserialization succeeds, return true
        } catch (Exception e) {
            return false; // If deserialization fails, return false
        }
    }

    @Override
    public void init(EndpointConfig config) {
        // Initialization logic, if needed
    }

    @Override
    public void destroy() {
        // Cleanup logic, if needed
    }
}
