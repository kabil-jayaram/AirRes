import java.io.*;
import java.util.*;

class StaffAccount implements Serializable {
    Main main = new Main();
    private String id;
    private String username;
    private String password;

    public StaffAccount(String username, String password) {
        this.id = generateUniqueId();
        this.username = username;
        this.password = password;
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

    private String generateUniqueId() {
        int nextId = getNextStaffId();
        String staffId = String.format("stf%04d", nextId);
        return staffId;
    }

    private int getNextStaffId() {
        int nextId = 1;

        if (!main.staffAccounts.isEmpty()) {
            StaffAccount lastStaff = main.staffAccounts.get(main.staffAccounts.size() - 1); 
            String lastId = lastStaff.getId().substring(3);
            nextId = Integer.parseInt(lastId) + 1;
        }
        return nextId;
    }

    public static List<AccountIdData> getStaffIds() {
        return DataManager.getData(StaffAccount.class);
    }

    public static void saveStaffIds() {
        DataManager.saveData(StaffAccount.class, AccountType.STAFF);
    }

    public static void loadStaffIds() {
        DataManager.loadData(StaffAccount.class, AccountType.STAFF);
    }
}