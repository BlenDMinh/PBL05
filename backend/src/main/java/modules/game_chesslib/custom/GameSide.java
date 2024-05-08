package modules.game_chesslib.custom;

public enum GameSide {
    WHITE(0), RANDOM(1), BLACK(2);

    private final int value;

    GameSide(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static GameSide fromValue(int value) {
        for (GameSide side : GameSide.values()) {
            if (side.value == value) {
                return side;
            }
        }
        throw new IllegalArgumentException("Invalid value for GameSide: " + value);
    }
}

