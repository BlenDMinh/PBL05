package modules.game_chesslib.service;

import java.sql.SQLException;

import common.GameStatus;
import modules.game_chesslib.GameRepository;
import modules.game_chesslib.dto.GameDto;

public class GameService {
    private final GameRepository gameRepository = new GameRepository();

    public String createGame(int firstId, int secondId, int rulesetId) throws Exception {
        return gameRepository.createOne(firstId, secondId, rulesetId);
    }

    public String createGame(int firstId, int secondId) throws Exception {
        return gameRepository.createOne(firstId, secondId, 1);
    }

    public boolean isValidGame(GameDto gameDto) throws SQLException, Exception {
        return gameDto != null
                && (gameDto.getStatus() == GameStatus.WAITING || gameDto.getStatus() == GameStatus.PLAYER1_WIN);
    }

    public GameDto getById(String id) throws SQLException, Exception {
        return gameRepository.getById(id);
    }
}
