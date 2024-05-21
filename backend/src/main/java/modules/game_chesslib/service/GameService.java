package modules.game_chesslib.service;

import com.google.gson.Gson;

import java.sql.SQLException;
import java.util.List;

import common.GameStatus;
import common.socket.SocketMessageDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import modules.game_chesslib.GameRepository;
import modules.game_chesslib.dto.FullGameLogResponse;
import modules.game_chesslib.dto.GameDto;
import modules.game_chesslib.dto.GameLogDto;
import modules.profile.dto.PlayerDto;
import modules.profile.service.ProfileService;

public class GameService {
    private final GameRepository gameRepository = new GameRepository();
    final ProfileService profileService = new ProfileService();

    public String createGame(int firstId, int secondId, int rulesetId) throws Exception {
        return gameRepository.createOne(firstId, secondId, rulesetId);
    }

    public String createGame(int firstId, int secondId) throws Exception {
        return gameRepository.createOne(firstId, secondId, 1);
    }

    public boolean isValidGame(GameDto gameDto) throws SQLException, Exception {
        return gameDto != null
                && (gameDto.getStatus().equals(GameStatus.WAITING) || gameDto.getStatus().equals(GameStatus.PLAYING));
    }

    public GameDto getById(String id) throws SQLException, Exception {
        return gameRepository.getById(id);
    }

    public boolean insertGameLog(SocketMessageDto messageDtos, String gameId, int playerId, String fen) {
        Gson gson = new Gson();
        return gameRepository.insertGameLog(gson.toJson(messageDtos), gameId, playerId, fen,
                new java.util.Date(System.currentTimeMillis()));
    }

    public FullGameLogResponse getGameLog(String gameId) {
        GameDto gameDto = gameRepository.getById(gameId);
        PlayerDto whitePlayer = profileService.getPlayerById(gameDto.getWhiteId());
        PlayerDto blackPlayer = profileService.getPlayerById(gameDto.getBlackId());
        List<GameLogDto> gameLogDtos = gameRepository.getGameLogs(gameId);
        return new FullGameLogResponse(gameDto, whitePlayer, blackPlayer, gameLogDtos);
    }

    public void endGame(String gameId, GameStatus gameStatus) {
        GameDto gameDto = gameRepository.getById(gameId);
        if (gameDto.getStatus().equals(GameStatus.BLACK_WIN) || gameDto.getStatus().equals(GameStatus.WHITE_WIN)
                || gameDto.getStatus().equals(GameStatus.DRAW)) {
            return;
        }
        PlayerDto whitePlayer = profileService.getPlayerById(gameDto.getWhiteId());
        PlayerDto blackPlayer = profileService.getPlayerById(gameDto.getBlackId());
        EloModify eloModify = new EloModify(whitePlayer.getElo(), blackPlayer.getElo(), gameStatus);
        PairElo pairElo = eloModify.calculate();
        gameRepository.updateElo(whitePlayer.getId(), pairElo.getWhiteElo(), blackPlayer.getId(),
                pairElo.getBlackElo());
        gameRepository.updateGameStatus(gameId, gameStatus);
    }
}

class EloModify {
    int R_white, R_black;
    GameStatus gameStatus;

    public EloModify(int whiteElo, int blackElo, GameStatus gameStatus) {
        this.R_white = whiteElo;
        this.R_black = blackElo;
        this.gameStatus = gameStatus;
    }

    int getK(int elo) {
        if (elo < 1600) {
            return 25;
        } else if (elo < 2000) {
            return 20;
        } else if (elo < 2400) {
            return 15;
        }
        return 10;
    }

    public PairElo calculate() {
        double Q_white = Math.pow(10, R_white / 400.0), Q_black = Math.pow(10, R_black / 400.0);
        double E_white = Q_white / (Q_white + Q_black);
        double E_black = 1 - E_white;

        double A_white = 0, A_black = 0;
        switch (gameStatus) {
            case WHITE_WIN:
                A_white = 1;
                break;
            case BLACK_WIN:
                A_black = 1;
                break;
            case DRAW:
                A_white = A_black = 0.5;
                break;
            default:
                break;
        }
        int R_white_n = (int) Math.round(R_white + getK(R_white) * (A_white - E_white));
        int R_black_n = (int) Math.round(R_black + getK(R_black) * (A_black - E_black));
        return new PairElo(R_white_n, R_black_n);
    }
}

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
class PairElo {
    int whiteElo;
    int blackElo;
}