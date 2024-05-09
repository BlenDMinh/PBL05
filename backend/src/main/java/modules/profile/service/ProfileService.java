package modules.profile.service;

import modules.profile.dto.PaginationGameHistoryDto;
import modules.profile.dto.PlayerDto;

import java.util.List;

import modules.profile.ProfileRepository;

public class ProfileService {
    final ProfileRepository profileRepository = new ProfileRepository();

    public PlayerDto getPlayerById(int id) {
        return profileRepository.getPlayerById(id);
    }

    public PaginationGameHistoryDto getPaginationGameHistoryByPlayerId(int playerId, int page, int size) {
        return profileRepository.getPaginationGameHistoryByPlayerId(playerId, page, size);
    }

    public boolean updateAvatarUrl(int userId, String url) {
        return profileRepository.updateAvartarUrl(userId, url);
    }

    public List<PlayerDto> getTopNPlayerByDisplaynameOrEmail(int n, String keyword) {
        return profileRepository.getTopNPlayerByDisplaynameOrEmail(n, keyword);
    }
}
