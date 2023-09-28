import java.io.*;
import java.util.*;

enum AccountType {
    CUSTOMER, STAFF, ADMIN
}

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

public class DataManager {
    private static Map<Class<?>, List<AccountIdData>> accountIdsMap = new HashMap<>();

    public static void addData(Class<?> accountType, AccountIdData data) {
        List<AccountIdData> accountIds = accountIdsMap.getOrDefault(accountType, new ArrayList<>());
        accountIds.add(data);
        accountIdsMap.put(accountType, accountIds);
    }

    public static List<AccountIdData> getData(Class<?> accountType) {
        return accountIdsMap.getOrDefault(accountType, new ArrayList<>());
    }

    public static void saveData(Class<?> accountType, AccountType type) {
        List<AccountIdData> accountIds = accountIdsMap.getOrDefault(accountType, new ArrayList<>());
        saveAccountIds(accountIds, type);
    }

    public static void loadData(Class<?> accountType, AccountType type) {
        List<AccountIdData> accountIds = loadAccountIds(type);
        accountIdsMap.put(accountType, accountIds);
    }

    private static void saveAccountIds(List<AccountIdData> accountIds, AccountType type) {
        String filename = type.toString().toLowerCase() + "_ids.dat";
        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(filename))) {
            outputStream.writeObject(accountIds);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static List<AccountIdData> loadAccountIds(AccountType type) {
        String filename = type.toString().toLowerCase() + "_ids.dat";
        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(filename))) {
            return (List<AccountIdData>) inputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new ArrayList<>();
        }
    }
}