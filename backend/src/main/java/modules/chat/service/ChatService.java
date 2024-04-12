package modules.chat.service;

import java.sql.SQLException;
import java.util.List;

import modules.chat.ChatRepository;
import modules.chat.dto.MessageResponseDto;
import modules.chat.dto.UserInChatDto;

public class ChatService {
    final ChatRepository chatRepository = new ChatRepository();

    public MessageResponseDto addMessage(String content, int senderId, int receiverId) throws Exception {
        return chatRepository.createOne(content, senderId, receiverId);
    }

    public List<UserInChatDto> getUserInChatOfSender(int senderId) throws RuntimeException{
        return chatRepository.getUserInChatOfSender(senderId);
    }
}
