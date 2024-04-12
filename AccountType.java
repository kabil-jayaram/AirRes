import java.util.HashMap;
import java.util.Map;

enum AccountType {
   CUSTOMER,
   STAFF,
   ADMIN;

   // Cache the values array to avoid array cloning on every call to values()
   private static final AccountType[] VALUES_CACHE = values();

   // Create a map for efficient string to enum conversion
   private static final Map<String, AccountType> STRING_TO_ENUM_MAP = new HashMap<>();
   static {
      for (AccountType accountType : VALUES_CACHE) {
         STRING_TO_ENUM_MAP.put(accountType.name(), accountType);
      }
   }

   // Method to get AccountType by name efficiently
   public static AccountType fromString(String name) {
      return STRING_TO_ENUM_MAP.get(name);
   }

   // Private constructor to prevent instantiation
   private AccountType() {
   }
}