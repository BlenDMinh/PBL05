package modules.game.service;

import java.sql.SQLException;

import modules.game.GameRepository;
import modules.game.common.GameStatus;
import modules.game.dto.GameDto;

public class GameService {
    private final GameRepository gameRepository = new GameRepository();

    public Integer createGame(int firstId, int secondId, int rulesetId) throws Exception {
        return gameRepository.createOne(firstId, secondId, rulesetId);
    }

    public Integer createGame(int firstId, int secondId) throws Exception {
        return gameRepository.createOne(firstId, secondId, 1);
    }

    public boolean isValidGame(GameDto gameDto) throws SQLException, Exception {
        return gameDto != null
                && (gameDto.getStatus() == GameStatus.PLAYER1_TURN || gameDto.getStatus() == GameStatus.PLAYER2_TURN);
    }

    public GameDto getById(int id) throws SQLException, Exception {
        return gameRepository.getById(id);
    }
}
