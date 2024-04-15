package common;

public enum AccountStatus {
    ACTIVE(0),
    INACTIVE(1),
    BANNED(2);

    private final int value;

    AccountStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static AccountStatus fromInt(int value) {
        for (AccountStatus status : AccountStatus.values()) {
            if (status.getValue() == value) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid integer value for AccountStatus: " + value);
    }
}
