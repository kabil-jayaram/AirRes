import java.io.*;
import java.util.*;

class StaffAccount implements Serializable {
    Main main = new Main();
    private static List<String> deletedIds = new ArrayList<>();
    private String id;
    private String username;
    private String password;

    public StaffAccount(String username, String password) {
        // Modify the constructor to use generateUniqueId
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
        int nextId;

        if (!deletedIds.isEmpty()) {
            // Reuse a deleted ID if available
            nextId = Integer.parseInt(deletedIds.remove(0).substring(3));
        } else {
            // Generate a new ID if the deleted IDs list is empty
            nextId = getNextStaffId();
        }

        String staffId = String.format("stf%04d", nextId);
        return staffId;
    }

    public static void addDeletedId(String id) {
        deletedIds.add(id);
    }

    private int getNextStaffId() {
        int nextId = 1;
    
        if (!main.staffAccounts.isEmpty()) {
            // Create a set to store all staff IDs, including deleted ones
            Set<String> allStaffIds = new HashSet<>();
    
            for (StaffAccount staff : main.staffAccounts) {
                allStaffIds.add(staff.getId());
            }
     
            // Find the first available ID by incrementing from 1
            while (allStaffIds.contains(String.format("stf%04d", nextId))) {
                nextId++;
            }
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