package modules.game_chesslib.custom;

public enum GameDifficulty {
    EASIEST(1), EASY(2), MEDIUM(3), HARD(4), HARDEST(5);

    private final int value;

    GameDifficulty(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static GameDifficulty fromValue(int value) {
        for (GameDifficulty difficulty : GameDifficulty.values()) {
            if (difficulty.value == value) {
                return difficulty;
            }
        }
        throw new IllegalArgumentException("Invalid value for GameDifficulty: " + value);
    }
}
