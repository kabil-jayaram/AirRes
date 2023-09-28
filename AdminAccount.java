import java.io.*;
import java.util.*;

class AdminAccount implements Serializable {
    Main main = new Main();
    private String id;
    private String username;
    private String password;
    private AccountType accountType;

    public AdminAccount(String username, String password) {
        this.id = generateUniqueId();
        this.username = username;
        this.password = password;
        this.accountType = AccountType.ADMIN;
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    private String generateUniqueId() {
        int nextId = getNextAdminId();
        String adminId = String.format("adm%04d", nextId);
        return adminId;
    }

    private int getNextAdminId() {
        int nextId = 1;

        if (!main.adminAccounts.isEmpty()) {
            AdminAccount lastAdmin = main.adminAccounts.get(main.adminAccounts.size() - 1);
            String lastId = lastAdmin.getId().substring(3);
            nextId = Integer.parseInt(lastId) + 1;
        }

        return nextId;
    }

    public static List<AccountIdData> getAdminIds() {
        return DataManager.getData(AdminAccount.class);
    }

    public static void saveAdminIds() {
        DataManager.saveData(AdminAccount.class, AccountType.ADMIN);
    }

    public static void loadAdminIds() {
        DataManager.loadData(AdminAccount.class, AccountType.ADMIN);
    }
}