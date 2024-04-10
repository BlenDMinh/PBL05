package modules.game_chesslib.custom;

public enum Color {
    WHITE("w"),
    BLACK("b");

    private final String value;

    Color(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}