package modules.game.common;

import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class MessageDecoder implements Decoder.Text<GameMessageDto> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public GameMessageDto decode(String jsonMessage) throws DecodeException {
        try {
            return objectMapper.readValue(jsonMessage, GameMessageDto.class);
        } catch (IOException e) {
            throw new DecodeException(jsonMessage, "Error decoding JSON", e);
        }
    }

    @Override
    public boolean willDecode(String jsonMessage) {
        // Attempt to deserialize the JSON message
        try {
            objectMapper.readTree(jsonMessage);
            return true; // If deserialization succeeds, return true
        } catch (IOException e) {
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
