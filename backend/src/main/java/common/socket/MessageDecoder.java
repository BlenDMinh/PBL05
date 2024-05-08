package common.socket;

import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

import com.google.gson.Gson;

public class MessageDecoder implements Decoder.Text<SocketMessageDto> {

    private final Gson gson = new Gson();

    @Override
    public SocketMessageDto decode(String jsonMessage) throws DecodeException {
        try {
            return gson.fromJson(jsonMessage, SocketMessageDto.class);
        } catch (Exception e) {
            throw new DecodeException(jsonMessage, "Error decoding JSON", e);
        }
    }

    @Override
    public boolean willDecode(String jsonMessage) {
        // Attempt to deserialize the JSON message
        try {
            gson.fromJson(jsonMessage, SocketMessageDto.class);
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
