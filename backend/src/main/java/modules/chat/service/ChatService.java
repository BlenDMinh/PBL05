package modules.chat.service;

import java.util.List;

import modules.chat.ChatRepository;
import modules.chat.dto.MessageResponseDto;
import modules.chat.dto.UserWithLastMessageDto;

public class ChatService {
    final ChatRepository chatRepository = new ChatRepository();

    public MessageResponseDto addMessage(String content, int senderId, int receiverId) throws Exception {
        return chatRepository.createOne(content, senderId, receiverId);
    }

    public List<UserWithLastMessageDto> getUserWithLastMessageOfUser(int userId) throws RuntimeException {
        return chatRepository.getUserWithLastMessageDtoOfUser(userId);
    }

    public List<MessageResponseDto> getMessageOfPair(int user1, int user2) {
        return chatRepository.getListMessageOfPair(user1, user2);
    }
}
