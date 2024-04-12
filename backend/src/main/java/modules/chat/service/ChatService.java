package modules.chat.service;

import modules.chat.ChatRepository;
import modules.chat.dto.MessageResponseDto;

public class ChatService {
    final ChatRepository chatRepository = new ChatRepository();

    public MessageResponseDto addMessage(String content, int senderId, int receiverId) throws Exception {
        return chatRepository.createOne(content, senderId, receiverId);
    }
}
