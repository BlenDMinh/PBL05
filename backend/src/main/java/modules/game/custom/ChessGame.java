package modules.game.custom;

import lombok.Getter;
import lombok.Setter;
import modules.game.custom.piece.Bishop;
import modules.game.custom.piece.ChessPiece;
import modules.game.custom.piece.ChessPieceType;
import modules.game.custom.piece.King;
import modules.game.custom.piece.Knight;
import modules.game.custom.piece.Pawn;
import modules.game.custom.piece.Queen;
import modules.game.custom.piece.Rook;
import modules.game.custom.player.GamePlayer;
import modules.game.custom.player.UserPlayer;

@Getter
@Setter
public class ChessGame {
    int id;
    GamePlayer player1;
    GamePlayer player2;
    boolean player1Turn;
    GameRule gameRule;
    ChessPiece[][] board;
    King whiteKing;
    King blackKing;

    public ChessGame(int id, int player1Id, int player2Id) {
        this.id = id;
        this.player1Turn = true;
        this.player1 = new UserPlayer(player1Id, this, true);
        this.player2 = new UserPlayer(player2Id, this, false);
        // Initialize the board with ChessPiece objects
        this.board = new ChessPiece[8][8];

        // White pieces (top of the board)
        board[0][0] = new Rook(new Position(0, 0), true, this);
        board[0][1] = new Knight(new Position(0, 1), true, this);
        board[0][2] = new Bishop(new Position(0, 2), true, this);
        board[0][3] = new Queen(new Position(0, 3), true, this);
        board[0][4] = new King(new Position(0, 4), true, this);
        board[0][5] = new Bishop(new Position(0, 5), true, this);
        board[0][6] = new Knight(new Position(0, 6), true, this);
        board[0][7] = new Rook(new Position(0, 7), true, this);
        // Set up white pawns
        for (int col = 0; col < 8; col++) {
            board[1][col] = new Pawn(new Position(1, col), true, this);
        }

        // Black pieces (bottom of the board)
        board[7][0] = new Rook(new Position(7, 0), false, this);
        board[7][1] = new Knight(new Position(7, 1), false, this);
        board[7][2] = new Bishop(new Position(7, 2), false, this);
        board[7][3] = new Queen(new Position(7, 3), false, this);
        board[7][4] = new King(new Position(7, 4), false, this);
        board[7][5] = new Bishop(new Position(7, 5), false, this);
        board[7][6] = new Knight(new Position(7, 6), false, this);
        board[7][7] = new Rook(new Position(7, 7), false, this);
        // Set up black pawns
        for (int col = 0; col < 8; col++) {
            board[6][col] = new Pawn(new Position(6, col), false, this);
        }

        // Initialize empty spaces on the board
        for (int row = 2; row < 6; row++) {
            for (int col = 0; col < 8; col++) {
                board[row][col] = null;
            }
        }

        whiteKing = (King) board[0][4];
        blackKing = (King) board[7][4];
        whiteKing.setRookInCastle((Rook) board[0][0]);
        blackKing.setRookInCastle((Rook) board[0][0]);
    }

    public void nextTurn() {
        setPlayer1Turn(!player1Turn);
    }

    public String getRawBoard() {
        StringBuilder builder = new StringBuilder();
        for (int row = 7; row >= 0; row--) {
            for (int col = 0; col <= 7; col++) {
                if (board[row][col] != null) {
                    builder.append(board[row][col].toString());
                } else {
                    builder.append(ChessPieceType.Empty);
                }
            }
        }
        return builder.toString();
    }
}
