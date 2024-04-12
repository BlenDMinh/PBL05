package modules.chat;

import java.io.IOException;

import javax.websocket.DecodeException;
import javax.websocket.EndpointConfig;

import com.fasterxml.jackson.databind.ObjectMapper;

import modules.chat.dto.MessageRequestDto;


public class MessageDecoder implements javax.websocket.Decoder.Text<MessageRequestDto> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public MessageRequestDto decode(String jsonMessage) throws DecodeException {
        try {
            return objectMapper.readValue(jsonMessage, MessageRequestDto.class);
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

