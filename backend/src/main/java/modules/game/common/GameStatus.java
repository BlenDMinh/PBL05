package modules.game.common;

public enum GameStatus {
    PLAYER1_TURN(0),
    PLAYER2_TURN(1),
    PLAYER1_WIN(2),
    PLAYER2_WIN(3),
    DRAW(4);

    private final int value;

    GameStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static GameStatus fromInt(int value) {
        for (GameStatus status : GameStatus.values()) {
            if (status.getValue() == value) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid integer value for GameStatus: " + value);
    }
}
