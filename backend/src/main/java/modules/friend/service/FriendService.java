package modules.friend.service;

import modules.friend.FriendRepository;
import modules.friend.dto.PaginationFriendDto;
import modules.friend.dto.PaginationFullFriendRequestDto;

public class FriendService {
    final FriendRepository friendRepository = new FriendRepository();

    public boolean createFriendRequest(int senderId, int receiverId) {
        return friendRepository.createFriendRequest(senderId, receiverId);
    }

    public PaginationFullFriendRequestDto getPaginationFriendRequestOfReceiver(int receiverId, int page, int size) {
        return friendRepository.getPaginationFriendRequestOfReceiver(receiverId, page, size);
    }

    public boolean acceptFriend(int senderId, int receiverId) {
        return friendRepository.deleteFriendRequestAndCreateFriendShip(senderId, receiverId);
    }

    public boolean rejectFriend(int senderId, int receiverId) {
        return friendRepository.deleteFriendRequest(senderId, receiverId);
    }

    public boolean unfriend(int userId, int friendId) {
        return friendRepository.deleteFriendShip(userId, friendId);
    }

    public PaginationFriendDto getPaginationFriendDto(int userId, int page, int size, String keyword) {
        return friendRepository.getPaginationFriendDto(userId, page, size, keyword);
    }
}
