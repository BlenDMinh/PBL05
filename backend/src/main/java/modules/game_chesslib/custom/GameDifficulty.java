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
}
