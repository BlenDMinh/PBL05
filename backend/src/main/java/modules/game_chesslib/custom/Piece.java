package modules.game_chesslib.custom;

public enum Piece {
    PAWN("p"),
    KNIGHT("n"),
    BISHOP("b"),
    ROOK("r"),
    QUEEN("q"),
    KING("k");

    private final String value;

    Piece(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}