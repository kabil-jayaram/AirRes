import java.io.*;

class AccountIdData implements Serializable {
    private String id;
    private AccountType type;

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
