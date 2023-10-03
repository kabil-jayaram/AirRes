import java.io.*;
import java.util.*;

class CustomerAccount implements Serializable {
    // Static field to store deleted customer account IDs
    private static List<String> deletedIds = new ArrayList<>();
    Main main = new Main();
    private String id;
    private String username;
    private String password;
    private AccountType accountType;

    public CustomerAccount(String username, String password) {
        this.id = generateUniqueId();
        this.username = username;
        this.password = password;
        this.accountType = AccountType.CUSTOMER;
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
        int nextId;

        if (!deletedIds.isEmpty()) {
            // Reuse a deleted ID if available
            nextId = Integer.parseInt(deletedIds.remove(0).substring(3));
        } else {
            // Generate a new ID if the deleted IDs list is empty
            nextId = getNextCustomerId();
        }

        String customerId = String.format("cus%04d", nextId);
        return customerId;
    }

    private int getNextCustomerId() {
        int nextId = 1;
    
        if (!main.customerAccounts.isEmpty()) {
            // Create a set to store all customer IDs, including deleted ones
            Set<String> allCustomerIds = new HashSet<>();
    
            for (CustomerAccount customer : main.customerAccounts) {
                allCustomerIds.add(customer.getId());
            }
    
            // Find the first available ID by incrementing from 1
            while (allCustomerIds.contains(String.format("cus%04d", nextId))) {
                nextId++;
            }
        }
    
        return nextId;
    }

    public static void addDeletedId(String id) {
        deletedIds.add(id);
    }

    public static List<AccountIdData> getCustomerIds() {
        return DataManager.getData(CustomerAccount.class);
    }

    public static void saveCustomerIds() {
        DataManager.saveData(CustomerAccount.class, AccountType.CUSTOMER);
    }

    public static void loadCustomerIds() {
        DataManager.loadData(CustomerAccount.class, AccountType.CUSTOMER);
    }
}