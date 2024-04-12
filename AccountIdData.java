import java.io.Serializable;

public final class AccountIdData implements Serializable {
    private final String id;
    private final AccountType type;

    public AccountIdData(String id, AccountType type) {
        this.id = id;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public AccountType getType() {
        return type;
    }
}
