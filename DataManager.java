import java.io.*;
import java.util.*;

// Main class for managing account data
public class DataManager {
    // Map to store account data of different types
    private static Map<Class<?>, List<AccountIdData>> accountIdsMap = new HashMap<>();

    // Method to add data to the map
    public static void addData(Class<?> accountType, AccountIdData data) {
        List<AccountIdData> accountIds = accountIdsMap.getOrDefault(accountType, new ArrayList<>());
        accountIds.add(data);
        accountIdsMap.put(accountType, accountIds);
    }

    // Method to get data from the map
    public static List<AccountIdData> getData(Class<?> accountType) {
        return accountIdsMap.getOrDefault(accountType, new ArrayList<>());
    }

    // Method to save data to a file
    public static void saveData(Class<?> accountType, AccountType type) {
        List<AccountIdData> accountIds = accountIdsMap.getOrDefault(accountType, new ArrayList<>());
        saveAccountIds(accountIds, type);
    }

    // Method to load data from a file
    public static void loadData(Class<?> accountType, AccountType type) {
        List<AccountIdData> accountIds = loadAccountIds(type);
        accountIdsMap.put(accountType, accountIds);
    }

    // Method to save account IDs to a file
    private static void saveAccountIds(List<AccountIdData> accountIds, AccountType type) {
        String filename = type.toString().toLowerCase() + "_ids.dat";
        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(filename))) {
            outputStream.writeObject(accountIds);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to load account IDs from a file
    private static List<AccountIdData> loadAccountIds(AccountType type) {
        String filename = type.toString().toLowerCase() + "_ids.dat";
        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(filename))) {
            return (List<AccountIdData>) inputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new ArrayList<>();
        }
    }
}
