package modules.game.custom;

import lombok.Getter;
import lombok.Setter;
import modules.game.custom.piece.Bishop;
import modules.game.custom.piece.ChessPiece;
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

    public ChessGame(int id, int player1Id, int player2Id) {
        this.id = id;
        this.player1Turn = true;
        this.player1 = new UserPlayer(player1Id, this);
        this.player2 = new UserPlayer(player2Id, this);
        // Initialize the board with ChessPiece objects
        this.board = new ChessPiece[8][8];

        // White pieces (top of the board)
        board[0][0] = new Rook(new Position(0, 0), true, board);
        board[0][1] = new Knight(new Position(0, 1), true, board);
        board[0][2] = new Bishop(new Position(0, 2), true, board);
        board[0][3] = new Queen(new Position(0, 3), true, board);
        board[0][4] = new King(new Position(0, 4), true, board);
        board[0][5] = new Bishop(new Position(0, 5), true, board);
        board[0][6] = new Knight(new Position(0, 6), true, board);
        board[0][7] = new Rook(new Position(0, 7), true, board);
        // Set up white pawns
        for (int col = 0; col < 8; col++) {
            board[1][col] = new Pawn(new Position(1, col), true, board);
        }

        // Black pieces (bottom of the board)
        board[7][0] = new Rook(new Position(7, 0), false, board);
        board[7][1] = new Knight(new Position(7, 1), false, board);
        board[7][2] = new Bishop(new Position(7, 2), false, board);
        board[7][3] = new Queen(new Position(7, 3), false, board);
        board[7][4] = new King(new Position(7, 4), false, board);
        board[7][5] = new Bishop(new Position(7, 5), false, board);
        board[7][6] = new Knight(new Position(7, 6), false, board);
        board[7][7] = new Rook(new Position(7, 7), false, board);
        // Set up black pawns
        for (int col = 0; col < 8; col++) {
            board[6][col] = new Pawn(new Position(6, col), false, board);
        }

        // Initialize empty spaces on the board
        for (int row = 2; row < 6; row++) {
            for (int col = 0; col < 8; col++) {
                board[row][col] = null;
            }
        }
    }

    public void nextTurn() {
        player1Turn = !player1Turn;
    }
}
