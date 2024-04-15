package common;

public enum Role {
    ADMIN(0),
    PLAYER(1);

    private final int value;

    Role(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
