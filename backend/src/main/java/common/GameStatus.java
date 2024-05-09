package common;

public enum GameStatus {
    WAITING(0),
    PLAYING(1),
    WHITE_WIN(2),
    BLACK_WIN(3),
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
        throw new IllegalArgumentException("Invalid integer value for AccountStatus: " + value);
    }
}
