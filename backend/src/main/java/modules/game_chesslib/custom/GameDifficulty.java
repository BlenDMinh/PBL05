package modules.game_chesslib.custom;

public enum GameDifficulty {
    EASIEST(5), EASY(10), MEDIUM(15), HARD(20), HARDEST(25);

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
