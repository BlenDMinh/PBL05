package modules.game.service;

import modules.game.GameRepository;

public class GameService {
    private final GameRepository gameRepository = new GameRepository();

    public Integer createGame(int firstId, int secondId, int rulesetId) throws Exception {
        return gameRepository.createOne(firstId, secondId, rulesetId);
    }

    public Integer createGame(int firstId, int secondId) throws Exception {
        return gameRepository.createOne(firstId, secondId, 1);
    }
}
