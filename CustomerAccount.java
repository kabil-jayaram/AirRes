import java.io.*;
import java.util.*;

class CustomerAccount implements Serializable {
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
        int nextId = getNextCustomerId();
        String customerId = String.format("cus%04d", nextId);
        return customerId;
    }

    private int getNextCustomerId() {
        int nextId = 1;

        if (!main.customerAccounts.isEmpty()) {
            CustomerAccount lastCustomer = main.customerAccounts.get(main.customerAccounts.size() - 1);
            String lastId = lastCustomer.getId().substring(3);
            nextId = Integer.parseInt(lastId) + 1;
        }

        return nextId;
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