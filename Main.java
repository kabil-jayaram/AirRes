import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

class Main {
    private static Scanner sc = new Scanner(System.in);
    private static HashMap<String, Flight> flights = new HashMap<>();
    private static List<Booking> bookings = new ArrayList<>();
    private static List<StaffAccount> staffAccounts = new ArrayList<>();
    private static List<AdminAccount> adminAccounts = new ArrayList<>();
    private static List<CustomerAccount> customerAccounts = new ArrayList<>();
    private static int flag = 0;
    private static CustomerAccount currentCustomer = null;

    public static void main(String[] args) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            saveFlightsAndBookings();
            saveStaffAccounts();
            saveAdminAccounts();
            saveCustomerAccounts();
        }));

        loadFlightsAndBookings();
        loadStaffAccounts();
        loadAdminAccounts();
        loadCustomerAccounts();
        Scanner sc = new Scanner(System.in);
        Map<Integer, Runnable> menuOptions = new HashMap<>();

        // Initialize menu options
        menuOptions.put(1, () -> adminSignUp(sc));
        menuOptions.put(2, () -> adminLogin(sc));
        menuOptions.put(3, () -> staffLogin(sc));
        menuOptions.put(4, () -> staffRecovery(sc));
        menuOptions.put(5, () -> customerSignUp(sc));
        menuOptions.put(6, () -> customerLogin(sc));
        menuOptions.put(7, () -> customerRecovery(sc));
        menuOptions.put(8, () -> deleteCustomer(sc));
        menuOptions.put(9, () -> System.out.println("Thank You for using our services"));

        int ch = 0;
        do {
            System.out.println("Login Menu");
            System.out.println("1 - Admin SignUp");
            System.out.println("2 - Admin Login");
            System.out.println("3 - Staff Login");
            System.out.println("4 - Staff Account Recovery");
            System.out.println("5 - Customer SignUp");
            System.out.println("6 - Customer Login");
            System.out.println("7 - Customer Account Recovery");
            System.out.println("8 - Delete Customer Account");
            System.out.println("9 - Exit");

            System.out.print("Enter your choice: ");
            String input = sc.nextLine().trim();

            // Validate input before parsing
            if (!input.isEmpty() && input.matches("\\d+")) {
                ch = Integer.parseInt(input);
                Runnable action = menuOptions.get(ch);
                if (action != null) {
                    action.run();
                } else {
                    System.out.println("Invalid choice. Please try again.");
                }
            } else {
                System.out.println("Invalid input. Please enter a valid index.");
            }
        } while (ch != 9);

        sc.close();
    }

    public static String getInputField(String inputField, Scanner sc) {
        return getInput(inputField, sc, true);
    }

    public static int parseIntWithValidation(String fieldName, Scanner sc) {
        while (true) {
            try {
                String input = getInput(fieldName, sc, false);
                int value = Integer.parseInt(input);
                if (value < 0) {
                    System.out.println(fieldName + " cannot be negative\n");
                } else {
                    return value;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid " + fieldName + ". Please enter a valid integer.\n");
            }
        }
    }

    public static String getFormattedFlightID(String inputField, Scanner sc) {
        while (true) {
            String input = getInput(inputField, sc, true);
            if (input.isBlank()) {
                System.out.println(inputField + " cannot be blank\n");
                continue;
            }
            try {
                int id = Integer.parseInt(input);
                if (id >= 0 && id <= 999) {
                    return String.format("%03d", id);
                } else {
                    System.out.println(inputField + " must be between 0 and 999\n");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid " + inputField + ". Please enter a valid integer.\n");
            }
        }
    }

    private static String getInput(String fieldName, Scanner sc, boolean allowBlank) {
        System.out.print("Enter the " + fieldName + ": ");
        String input = sc.nextLine();
        while (input.isBlank() && !allowBlank) {
            System.out.println(fieldName + " cannot be blank\n");
            input = sc.nextLine();
        }
        return input;
    }

    // Function to handle Admin sign-up
    public static void adminSignUp(Scanner sc) {
        try {
            String username = getInputField("Admin Username", sc);

            // Check if an admin account with the same username already exists
            if (isAdminUsernameDuplicate(username)) {
                System.out.println("Username is already in use. Please choose a different username.\n");
                return; // Exit the method without signing up the admin
            }

            String password = getInputField("Admin Password", sc);

            // Create a new AdminAccount object and add it to the list
            AdminAccount admin = new AdminAccount(username, password);
            adminAccounts.add(admin); // Add the new account to the list
            System.out.println("Admin Signed Up Successfully\n");
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter valid data.\n");
            sc.nextLine(); // Consume the invalid input
        }
    }

    // Helper method to check if an admin account with the same username exists
    private static boolean isAdminUsernameDuplicate(String username) {
        for (AdminAccount admin : adminAccounts) {
            if (admin.getUsername().equalsIgnoreCase(username)) {
                return true; // Username is already in use
            }
        }
        return false; // Username is not a duplicate
    }

    // Function to handle Staff Account Recovery
    public static void staffRecovery(Scanner sc) {
        if (staffAccounts.isEmpty()) {
            System.out.println("No Staff available.\n");
        } else {
            try {
                String username = getInputField("Staff Username", sc);
                for (StaffAccount staff : staffAccounts) {
                    flag = 0;
                    if (staff.getUsername().equalsIgnoreCase(username)) {
                        flag = 1;
                        String password = getInputField("Staff Password", sc);
                        staff.setPassword(password);
                        System.out.println("Staff Password Updated\n");
                    }
                }
                if (flag == 0)
                    System.out.println("Invalid Username\n");
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid option.\n");
                sc.nextLine(); // Consume the invalid input
            }
        }
    }

    // Method to delete a Customer Account
    public static void deleteCustomer(Scanner sc) {
        if (customerAccounts.isEmpty()) {
            System.out.println("No Customer available.\n");
        } else {
            try {
                String username = getInputField("Customer Username", sc);

                boolean customerAccountFound = false;
                Iterator<CustomerAccount> iterator = customerAccounts.iterator();
                while (iterator.hasNext()) {
                    CustomerAccount customerAccount = iterator.next();
                    if (customerAccount.getUsername().equalsIgnoreCase(username)) {
                        // Add the deleted ID to the deletedIds list
                        CustomerAccount.addDeletedId(customerAccount.getId());
                        iterator.remove();
                        customerAccountFound = true;
                        System.out.println("Customer Account Deleted Successfully\n");
                        break;
                    }
                }

                if (!customerAccountFound) {
                    System.out.println("Customer Username does not exist\n");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid Customer Username.\n");
                sc.nextLine(); // Consume the invalid input
            }
        }
    }

    // Function to handle Customer Account Recovery
    public static void customerRecovery(Scanner sc) {
        if (customerAccounts.isEmpty()) {
            System.out.println("No Customer available.\n");
        } else {
            try {
                String username = getInputField("Customer Username", sc);
                for (CustomerAccount customer : customerAccounts) {
                    flag = 0;
                    if (customer.getUsername().equalsIgnoreCase(username)) {
                        flag = 1;
                        String password = getInputField("Customer Password", sc);
                        customer.setPassword(password);
                        System.out.println("Customer Password Updated\n");
                        break;
                    }
                }
                if (flag == 0)
                    System.out.println("Invalid Username\n");
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid option.\n");
                sc.nextLine(); // Consume the invalid input
            }
        }
    }

    // Inner class for masking password input
    static class ThreadDisappear implements Runnable {
        private boolean end;

        public void run() {
            end = true;
            while (end) {
                System.out.print("\010*"); // Mask the input with asterisks
                try {
                    Thread.currentThread().sleep(1);
                } catch (InterruptedException ie) {
                    ie.printStackTrace();
                }
            }
        }

        public void maskEnd() {
            end = false;
        }
    }

    // Function to mask password input and return it as a String
    public static String maskPassword() {
        String password = "";
        ThreadDisappear td = new ThreadDisappear();
        Thread t = new Thread(td);
        t.start();
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        try {
            password = br.readLine();
            td.maskEnd();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return password;
    }

    // Function to handle Admin login
    public static void adminLogin(Scanner sc) {
        String username = "";
        String password = "";

        if (adminAccounts.isEmpty()) {
            System.out.println("No Admin available.\n");
        } else {
            try {
                Console console = System.console();
                if (console == null) {
                    // Console is not available, handle accordingly
                    username = getInputField("Admin Username", sc);
                    password = getInputField("Admin Password", sc);
                } else {
                    username = getInputField("Admin Username", sc);
                    System.out.print("Enter the Customer Password: ");
                    password = maskPassword();
                    if (password.isBlank())
                    {
                        System.out.println("Password cannot be blank\n");
                        return;
                    }
                }

                for (AdminAccount admin : adminAccounts) {
                    flag = 0;
                    if (admin.getUsername().equalsIgnoreCase(username) && admin.getPassword().equals(password)) {
                        System.out.println("Admin Logged In\n");
                        flag = 1;
                        System.out.println("Welcome " + admin.getId() + " " + admin.getUsername());
                        Admin.admin_menu(sc);
                        break;
                    }
                }
                if (flag == 0)
                    System.out.println("Invalid Username or Password\n");
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid option.\n");
                sc.nextLine(); // Consume the invalid input
            }
        }
    }

    // Function to read a password with masking
    public static char[] readPasswordWithMask() {
        Console console = System.console();
        if (console != null) {
            // If the console is available, use it for masked input
            return console.readPassword();
        } else {
            // If the console is not available, fallback to regular input
            String password = sc.nextLine();
            return password.toCharArray();
        }
    }

    // Function to handle Customer sign-up
    public static void customerSignUp(Scanner sc) {
        try {
            String username = getInputField("Customer Username", sc);

            // Check if a customer account with the same username already exists
            if (isCustomerUsernameDuplicate(username)) {
                System.out.println("Username is already in use. Please choose a different username.\n");
                return; // Exit the method without signing up the customer
            }
            
            String password = getInputField("Customer Password", sc);

            // Create a new CustomerAccount object and add it to the list
            CustomerAccount customer = new CustomerAccount(username, password);
            customerAccounts.add(customer); // Add the new account to the list
            System.out.println("Customer Signed Up Successfully\n");
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter valid data.\n");
            sc.nextLine(); // Consume the invalid input
        }
    }

    // Helper method to check if a customer account with the same username exists
    private static boolean isCustomerUsernameDuplicate(String username) {
        for (CustomerAccount customer : customerAccounts) {
            if (customer.getUsername().equalsIgnoreCase(username)) {
                return true; // Username is already in use
            }
        }
        return false; // Username is not a duplicate
    }


    // Function to handle Customer login
    public static void customerLogin(Scanner sc) {
        String username = "";
        String password = "";

        if (customerAccounts.isEmpty()) {
            System.out.println("No Customer available.\n");
        } else {
            try {
                Console console = System.console();
                if (console == null) {
                    username = getInputField("Customer Username", sc);
                    password = getInputField("Customer Password", sc);
                } else {
                    username = getInputField("Customer Username", sc);
                    System.out.print("Enter the Customer Password: ");
                    password = maskPassword();
                    if (password.isBlank())
                    {
                        System.out.println("Password cannot be blank\n");
                        return;
                    }
                }

                for (CustomerAccount customer : customerAccounts) {
                    flag = 0;
                    if (customer.getUsername().equalsIgnoreCase(username) && customer.getPassword().equals(password)) {
                        System.out.println("Customer Logged In\n");
                        flag = 1;
                        currentCustomer = customer;
                        System.out.println("Welcome " + currentCustomer.getId() + " " + currentCustomer.getUsername());
                        Customer.customer_menu(sc);
                        break;
                    }
                }
                if (flag == 0)
                    System.out.println("Invalid Username or Password\n");
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid option.\n");
                sc.nextLine(); // Consume the invalid input
            }
        }
    }

    // Function to handle Staff login
    public static void staffLogin(Scanner sc) {
        String username = "";
        String password = "";

        if (staffAccounts.isEmpty()) {
            System.out.println("No Staff available.\n");
        } else {
            try {
                Console console = System.console();
                if (console == null) {
                    username = getInputField("Staff Username", sc);
                    password = getInputField("Staff Password", sc);
                } else {
                    username = getInputField("Staff Username", sc);
                    System.out.print("Enter the Staff Password: ");
                    password = maskPassword();
                    if (password.isBlank())
                    {
                        System.out.println("Password cannot be blank\n");
                        return;
                    }
                }

                for (StaffAccount staff : staffAccounts) {
                    flag = 0;
                    if (staff.getUsername().equalsIgnoreCase(username) && staff.getPassword().equals(password)) {
                        System.out.println("Staff Logged In\n");
                        flag = 1;
                        System.out.println("Welcome " + staff.getId() + " " + staff.getUsername());
                        Staff.staff_menu(sc);
                        break;
                    }
                }
                if (flag == 0)
                    System.out.println("Invalid Username or Password\n");
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid option.\n");
                sc.nextLine(); // Consume the invalid input
            }
        }
    }

    // Function to load admin accounts from a file
    public static void loadAdminAccounts() {
        try (Scanner scanner = new Scanner(new File("admin_accounts.dat"))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(" ");
                if (parts.length == 3) {
                    String username = parts[1];
                    String password = parts[2];
                    AdminAccount admin = new AdminAccount(username, password);
                    adminAccounts.add(admin);
                }
            }
        } catch (FileNotFoundException e) {
            // Handle file not found exception
        }
    }

    // Function to load customer accounts from a file
    public static void loadCustomerAccounts() {
        try (Scanner scanner = new Scanner(new File("customer_accounts.dat"))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(" ");
                if (parts.length == 3) {
                    String username = parts[1];
                    String password = parts[2];
                    CustomerAccount customer = new CustomerAccount(username, password);
                    customerAccounts.add(customer);
                }
            }
        } catch (FileNotFoundException e) {
            // Handle file not found exception
        }
    }

    // Function to load staff accounts from a file
    public static void loadStaffAccounts() {
        try (Scanner scanner = new Scanner(new File("staff_accounts.dat"))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(" ");
                if (parts.length == 3) {
                    String username = parts[1];
                    String password = parts[2];
                    StaffAccount staff = new StaffAccount(username, password);
                    staffAccounts.add(staff);
                }
            }        
        } catch (FileNotFoundException e) {
            // Handle file not found exception
        }
    }

    // Function to save staff accounts to a file
    public static void saveStaffAccounts() {
        try (PrintWriter writer = new PrintWriter(new File("staff_accounts.dat"))) {
            for (StaffAccount staff : staffAccounts) {
                writer.println(staff.getId() + " " + staff.getUsername() + " " + staff.getPassword());
            }
        } catch (IOException e) {
            // Handle file not found exception
        }
    }

    // Function to save customer accounts to a file
    public static void saveCustomerAccounts() {
        try (PrintWriter writer = new PrintWriter(new File("customer_accounts.dat"))) {
            for (CustomerAccount customer : customerAccounts) {
                writer.println(customer.getId() + " " + customer.getUsername() + " " + customer.getPassword());
            }
        } catch (FileNotFoundException e) {
            // Handle file not found exception
        }
    }

    // Function to save admin accounts to a file
    public static void saveAdminAccounts() {
        try (PrintWriter writer = new PrintWriter(new File("admin_accounts.dat"))) {
            for (AdminAccount admin : adminAccounts) {
                writer.println(admin.getId() + " " + admin.getUsername() + " " + admin.getPassword());
            }
        } catch (FileNotFoundException e) {
            // Handle file not found exception
        }
    }

    // Function to check if any of the specified files are non-empty
    public static boolean areFilesNonEmpty(String... filenames) {
        for (String filename : filenames) {
            File file = new File(filename);
            if (file.exists() && file.length() > 0) {
                return true;
            }
        }
        return false;
    }

    // Function to load flights and bookings from files
    public static void loadFlightsAndBookings() {
        try (ObjectInputStream flightsInput = new ObjectInputStream(new FileInputStream("flights.dat"));
             ObjectInputStream bookingsInput = new ObjectInputStream(new FileInputStream("bookings.dat"))) {
            flights = (HashMap<String, Flight>) flightsInput.readObject();
            bookings = (List<Booking>) bookingsInput.readObject();
        } catch (IOException | ClassNotFoundException e) {
            // Handle file not found exception
        }
    }

    // Function to save flights and bookings to files
    public static void saveFlightsAndBookings() {
        try (ObjectOutputStream flightsOutput = new ObjectOutputStream(new FileOutputStream("flights.dat"));
             ObjectOutputStream bookingsOutput = new ObjectOutputStream(new FileOutputStream("bookings.dat"))) {
            flightsOutput.writeObject(flights);
            bookingsOutput.writeObject(bookings);
        } catch (IOException e) {
            // Handle file not found exception
        }
    }

    // Define a static inner class 'Staff' to handle staff-related operations.
    static class Staff {
        // Method to display the staff menu and handle staff operations.
        public static void staff_menu(Scanner sc) {
            AtomicInteger ch = new AtomicInteger();
            Map<Integer, Runnable> menuOptions = new HashMap<>();

            // Initialize menu options with lambdas
            menuOptions.put(1, () -> updateFlightStatus(sc));
            menuOptions.put(2, () -> updateSeatAvailability(sc));
            menuOptions.put(3, () -> {
                String cus_id = getInputField("Customer Id", sc);
                String flight_id = getFormattedFlightID("Flight ID", sc);
                passenger_details(cus_id, flight_id);
            });
            menuOptions.put(4, Admin::display_flights);
            menuOptions.put(5, () -> {
                System.out.println("Staff Logged Out\n");
                ch.set(5); // Exit the loop
            });

            // Main menu loop
            while (ch.get() != 5) {
                System.out.println("\nStaff Menu");
                // Display menu options
                menuOptions.keySet().forEach(option -> System.out.println(option + " - " + getMenuOptionDescription(option)));
                System.out.print("Enter your choice: ");
                String input = sc.nextLine().trim();

                // Validate input
                if (input.isEmpty()) {
                    System.out.println("Invalid input. Please enter a valid index.");
                    continue;
                }

                // Execute selected menu option
                try {
                    ch.set(Integer.parseInt(input));
                    Runnable option = menuOptions.get(Integer.valueOf(String.valueOf(ch)));
                    if (option != null) {
                        option.run();
                    } else {
                        System.out.println("Invalid choice. Please try again.\n");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter a valid index.");
                }
            }
        }

        private static String getMenuOptionDescription(int option) {
            switch (option) {
                case 1: return "Update Flight Status";
                case 2: return "Update Seat Availability";
                case 3: return "Passenger Details";
                case 4: return "Display Flights";
                case 5: return "Exit";
                default: return "Unknown Option";
            }
        }

        // Method to perform passenger check-in.
        public static void check_in(Scanner sc) {
            // Check if there are any bookings available.
            if (bookings.isEmpty()) {
                System.out.println("No Bookings available.\n");
                return; // Exit early if no bookings
            }

            // Normalize input strings to lowercase for consistency.
            String id = getFormattedFlightID("Flight ID", sc).toLowerCase();
            String name = getInputField("Flight Name", sc).toLowerCase();
            String source = getInputField("Source", sc).toLowerCase();
            String destination = getInputField("Destination", sc).toLowerCase();

            // Validate that source and destination are different.
            if (source.equals(destination)) {
                System.out.println("Source and Destination cannot be the same\n");
                getInputField(destination, sc);
                return; // Exit early after error
            }

            // Retrieve flight details using the flight ID.
            Flight flight = flights.get(id);
            // Check if flight details match the inputs.
            if (flight != null && flight.getName().equals(name) && flight.getSource().equals(source) && flight.getDestination().equals(destination)) {
                // Depending on the flight's status, print the appropriate message.
                switch (flight.getStatus().toLowerCase()) {
                    case "operational":
                        System.out.println("Check-In Successful\n");
                        break;
                    case "delayed":
                        System.out.println("Flight Delayed\n");
                        break;
                    default:
                        System.out.println("Flight Cancelled\n");
                        break;
                }
            } else {
                // If flight details do not match, inform the user.
                System.out.println("Flight not found or details mismatch\n");
            }
        }

        // Method to update flight status.
        public static void updateFlightStatus(Scanner sc) {
            if (flights.isEmpty()) {
                System.out.println("No Flights available.\n");
                return; // Exit if no flights available.
            } else {
                try {
                    String id = getFormattedFlightID("Flight ID", sc);
                    String newStatus = getInputField("new status", sc);

                    Flight flight = flights.get(id);
                    if (flight != null) {
                        flight.setStatus(newStatus);
                        System.out.println("Flight Status Updated Successfully\n");
                    } else {
                        System.out.println("Flight ID does not exist\n");
                    }
                } catch (InputMismatchException e) {
                    System.out.println("Invalid input. Please enter valid data.\n");
                    sc.nextLine(); // Consume the invalid input
                }
            }
        }

        // Updates the seat availability for a specific flight category.
        public static void updateSeatAvailability(Scanner scanner) {
            if (flights.isEmpty()) {
                System.out.println("No Flights available.\n");
                return; // Exit if no flights available.
            }

            try {
                String flightId = getFormattedFlightID("Flight ID", scanner);
                String seatCategory = getInputField("seat category (Normal/Business/First)", scanner);
                int newSeatCount = parseIntWithValidation("seat count", scanner);

                Flight flight = flights.get(flightId);
                if (flight != null) {
                    switch (seatCategory.toLowerCase()) {
                        case "normal":
                            flight.setNormalSeats(newSeatCount);
                            break;
                        case "business":
                            flight.setBusinessSeats(newSeatCount);
                            break;
                        case "first":
                            flight.setFirstClassSeats(newSeatCount);
                            break;
                        default:
                            System.out.println("Invalid seat category. Please enter Normal, Business, or First.\n");
                            return;
                    }
                    System.out.println("Seat Availability Updated Successfully\n");
                } else {
                    System.out.println("Flight ID does not exist\n");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter valid data.\n");
                scanner.nextLine(); // Clear the invalid input.
            }
        }

        // Method to display passenger details.
        public static void passenger_details(String customerId, String flightId) {
            if (bookings.isEmpty()) {
                System.out.println("No Bookings available.\n");
            } else {
                System.out.println("\nPassenger Details: " + customerId);
                System.out.println("Passenger Name | Flight ID | Flight Name       | Flight Time | Source   | Destination | Seat Category | Check-In Option");

                for (Booking booking : bookings) {
                    if ((booking.getCustomerId().equals(customerId)) && (booking.getFlight().getId().equals(flightId))) {
                        System.out.printf("%-13s | %-9s | %-16s | %-8s | %-8s | %-12s | %-13s | %-15s%n", booking.getCustomerName(), booking.getFlight().getId(), booking.getFlight().getName(), booking.getFlight().getTime(), booking.getFlight().getSource(), booking.getFlight().getDestination(), booking.getSeatCategory(), booking.getCheckInOption());
                    }
                }
                System.out.println();
            }
        }
    }
        
        // Define a static inner class 'Admin' that extends 'Staff' to handle admin-specific operations.
        static class Admin extends Staff {
            // Method to display the admin menu and handle admin operations.
            public static void admin_menu(Scanner sc) {
                int ch = 0;
                while (ch != 9) {
                    try {
                        // Display admin menu options.
                        System.out.println("\nAdmin Menu");
                        System.out.println("1 - Add Flights");
                        System.out.println("2 - Update Flights");
                        System.out.println("3 - Delete Flights");
                        System.out.println("4 - Display Flights");
                        System.out.println("5 - Add Staff Account");
                        System.out.println("6 - Delete Staff Account");
                        System.out.println("7 - Update Staff Account");
                        System.out.println("8 - Display Staff Accounts");
                        System.out.println("9 - Exit");

                        System.out.print("Enter your choice: ");
                        String input = sc.nextLine().trim(); // Get the user input and remove leading/trailing spaces

                        if (input.isEmpty()) {
                            System.out.println("Invalid input. Please enter a valid index.");
                            continue; // Restart the loop if input is empty
                        }

                        try {
                            ch = Integer.parseInt(input);
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid input. Please enter a valid index.");
                            continue; // Restart the loop if input is not a valid integer
                        }

                        switch (ch) {
                            case 1:
                                add_flights(sc);
                                break;
                            case 2:
                                update_flights(sc);
                                break;
                            case 3:
                                deleteFlights(sc);
                                break;
                            case 4:
                                display_flights();
                                break;
                            case 5:
                                addStaffAccount(sc);
                                break;
                            case 6:
                                deleteStaffAccount(sc);
                                break;
                            case 7:
                                updateStaffAccount(sc);
                                break;
                            case 8:
                                displayStaffAccounts();
                                break;
                            case 9:
                                System.out.println("Admin Logged Out\n");
                                break;
                            default:
                                System.out.println("Invalid choice. Please try again.\n");
                                break;
                        }
                    } catch (InputMismatchException e) {
                        System.out.println("Invalid input. Please enter a valid option.\n");
                        sc.nextLine(); // Consume the invalid input
                    }
                }
            }

            // Method to display staff accounts.
            public static void displayStaffAccounts() {
                if (staffAccounts.isEmpty()) {
                    System.out.println("No Staff available.\n");
                } else {
                    System.out.println("\nStaff Accounts");
                    System.out.println("Staff ID | Staff Username | Staff Password");
                    for (StaffAccount staffAccount : staffAccounts)
                        System.out.printf("%-9s| %-15s| %-14s%n", staffAccount.getId(), staffAccount.getUsername(), staffAccount.getPassword());
                    System.out.println();
                }
            }

            // Method to delete a staff account
            public static void deleteStaffAccount(Scanner sc) {
                if (staffAccounts.isEmpty()) {
                    System.out.println("No Staff available.\n");
                } else {
                    try {
                        String username = getInputField("Staff Username", sc);

                        boolean staffAccountFound = false;
                        Iterator<StaffAccount> iterator = staffAccounts.iterator();
                        while (iterator.hasNext()) {
                            StaffAccount staffAccount = iterator.next();
                            if (staffAccount.getUsername().equalsIgnoreCase(username)) {
                                // Add the deleted ID to the deletedIds list
                                StaffAccount.addDeletedId(staffAccount.getId());
                                iterator.remove();
                                staffAccountFound = true;
                                System.out.println("Staff Account Deleted Successfully\n");
                                break;
                            }
                        }

                        if (!staffAccountFound) {
                            System.out.println("Staff Username does not exist\n");
                        }
                    } catch (InputMismatchException e) {
                        System.out.println("Invalid input. Please enter a valid Staff Username.\n");
                        sc.nextLine(); // Consume the invalid input
                    }
                }
            }
            
            // Method to update a staff account's password.
            public static void updateStaffAccount(Scanner sc) {
                if (staffAccounts.isEmpty()) {
                    System.out.println("No Staff available.\n");
                } else {
                    try {
                        String username = getInputField("Staff Username", sc);
                        String password = getInputField("Staff Password", sc);
                        
                        for (StaffAccount staffAccount : staffAccounts) {
                            if (staffAccount.getUsername().equalsIgnoreCase(username)) {
                                staffAccount.setPassword(password);
                                System.out.println("Staff Account Updated Successfully\n");
                                return; // Exit the method when the staff account is found and updated
                            }
                        }

                        // Staff account not found, print error message
                        System.out.println("Staff Username does not exist\n");
                    } catch (InputMismatchException e) {
                        System.out.println("Invalid input. Please enter valid data.\n");
                        sc.nextLine(); // Consume the invalid input
                    }
                }
            }

            // Method to display flight details.
            public static void display_flights() {
                if (flights.isEmpty()) {
                    System.out.println("No Flights available.\n");
                } else {
                    StringBuilder sb = new StringBuilder();
                    sb.append("\nFlight Details\n");
                    sb.append("Flight ID | Flight Name       | Source   | Destination | Time | Status      | Price | Normal | Business Class | First Class\n");

                    for (Flight flight : flights.values()) {
                        sb.append(String.format("%-10s| %-18s| %-9s| %-12s| %-6s| %-12s| %-6s| %-7s| %-15s| %-12s%n",
                                flight.getId(), flight.getName(), flight.getSource(), flight.getDestination(),
                                flight.getTime(), flight.getStatus(), flight.getPrice(),
                                flight.getNormalSeats(), flight.getBusinessSeats(), flight.getFirstClassSeats()));
                    }
                    System.out.println(sb.toString());
                }
            }

            // Method to add a new flight.
            public static void add_flights(Scanner sc) {
                String id = getFormattedFlightID("Flight ID", sc);
                String name = getValidatedInput("Flight Name", sc, input -> !input.isBlank());
                String source = getValidatedInput("Source", sc, input -> !input.isBlank());
                String destination = getValidatedInput("Destination", sc, input -> !input.isBlank());

                while (source.equalsIgnoreCase(destination)) {
                    System.out.println("Source and Destination cannot be the same\n");
                    destination = getValidatedInput("Destination", sc, input -> !input.isBlank());
                }

                String newTime = getValidatedInput("New Time (HH:mm)", sc, input -> input.matches("\\d{2}:\\d{2}"));
                String status = getValidatedInput("Status", sc, input -> !input.isBlank());
                int price = getValidatedInt("Price", sc, input -> input > 0);
                int normalSeats = getValidatedInt("Normal Seats", sc, input -> input >= 0);
                int businessSeats = getValidatedInt("Business Class Seats", sc, input -> input >= 0);
                int firstClassSeats = getValidatedInt("First Class Seats", sc, input -> input >= 0);
                String luggageLimit = getValidatedInput("Luggage Weight Limit (optional, press Enter to skip)", sc, input -> true);
                if (luggageLimit.isBlank()) {
                    luggageLimit = "0";
                }

                Flight newFlight = new Flight(id, name, source, destination, newTime, status, price, normalSeats, businessSeats, firstClassSeats, luggageLimit);
                flights.put(newFlight.getId(), newFlight);
                System.out.println("Flight Added Successfully\n");
            }

            private static String getValidatedInput(String prompt, Scanner sc, Predicate<String> validator) {
                String input;
                do {
                    System.out.println(prompt + ": ");
                    input = sc.nextLine();
                } while (!validator.test(input));
                return input;
            }

            private static int getValidatedInt(String prompt, Scanner sc, Predicate<Integer> validator) {
                int input;
                do {
                    System.out.println(prompt);
                    input = Integer.parseInt(sc.nextLine());
                } while (!validator.test(input));
                return input;
            }


            // Method to update flight details selectively.
            public static void update_flights(Scanner sc) {
                if (flights.isEmpty()) {
                    System.out.println("No Flights available.\n");
                } else {
                    try {
                        String id = getFormattedFlightID("Flight ID", sc);
                        Flight selectedFlight = flights.get(id);

                        if (selectedFlight != null) {
                            System.out.println("Select an attribute to update:");
                            System.out.println("1. Flight Name");
                            System.out.println("2. Source");
                            System.out.println("3. Destination");
                            System.out.println("4. Time");
                            System.out.println("5. Status");
                            System.out.println("6. Price");
                            System.out.println("7. Normal Seats");
                            System.out.println("8. Business Class Seats");
                            System.out.println("9. First Class Seats");
                            System.out.println("10. Luggage Weight Limit");
                            System.out.println("0. Finish Updating");

                            boolean updating = true;

                            while (updating) {
                                System.out.print("Enter the index of the attribute to update (0 to finish): ");
                                String input = sc.nextLine().trim();

                                if (input.isEmpty()) {
                                    System.out.println("Invalid input. Please enter a valid index.");
                                    continue;
                                }

                                int choice = getValidatedInt("Enter the index of the attribute to update (0 to finish)", sc, i -> i >= 0 && i <= 10);

                                switch (choice) {
                                    case 0:
                                        updating = false;
                                        break;
                                    case 1:
                                        String newName = getValidatedInput("Flight Name", sc, String::isBlank);
                                        if (!newName.isBlank()) {
                                            selectedFlight.setName(newName);
                                        }
                                        break;
                                    case 2:
                                        String newSource = getValidatedInput("Source", sc, String::isBlank);
                                        if (!newSource.isBlank()) {
                                            selectedFlight.setSource(newSource);
                                        }
                                        break;
                                    case 3:
                                        String newDestination = getValidatedInput("Destination", sc, String::isBlank);
                                        if (!newDestination.isBlank() && !newDestination.equalsIgnoreCase(selectedFlight.getSource())) {
                                            selectedFlight.setDestination(newDestination);
                                        }
                                        break;
                                    case 4:
                                        String newTime = getValidatedInput("New Time (HH:mm)", sc, s -> isValidTimeFormat(s));
                                        if (isValidTimeFormat(newTime)) {
                                            selectedFlight.setTime(newTime);
                                        }
                                        break;
                                    case 5:
                                        String newStatus = getValidatedInput("Status", sc, String::isBlank);
                                        selectedFlight.setStatus(newStatus);
                                        break;
                                    case 6:
                                        int newPrice = getValidatedInt("Price", sc, i -> i >= 0);
                                        selectedFlight.setPrice(newPrice);
                                        break;
                                    case 7:
                                        int newNormalSeats = getValidatedInt("Normal Seats", sc, i -> i >= 0);
                                        selectedFlight.setNormalSeats(newNormalSeats);
                                        break;
                                    case 8:
                                        int newBusinessSeats = getValidatedInt("Business Class Seats", sc, i -> i >= 0);
                                        selectedFlight.setBusinessSeats(newBusinessSeats);
                                        break;
                                    case 9:
                                        int newFirstClassSeats = getValidatedInt("First Class Seats", sc, i -> i >= 0);
                                        selectedFlight.setFirstClassSeats(newFirstClassSeats);
                                        break;
                                    case 10:
                                        String newLuggageLimit = getValidatedInput("Luggage Weight Limit (optional, press Enter to skip)", sc, s -> true);
                                        selectedFlight.setLuggageWeightLimit(newLuggageLimit);
                                        break;
                                    default:
                                        System.out.println("Invalid choice. Please enter a valid index.");
                                        break;
                                }
                            }

                            System.out.println("Flight Updated Successfully\n");
                        } else {
                            System.out.println("Flight ID does not exist\n");
                        }
                    } catch (InputMismatchException e) {
                        System.out.println("Invalid input. Please enter valid data.\n");
                        sc.nextLine();
                    }
                }
            }

            // Check if the input time is in HH:mm format
            private static boolean isValidTimeFormat(String time) {
                return time.matches("^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$");
            }

            // Method to delete a flight.
            public static void deleteFlights(Scanner sc) {
                if (flights.isEmpty()) {
                    System.out.println("No Flights available.\n");
                } else {
                    try {
                        String id = getFormattedFlightID("Flight ID", sc);

                        Flight flight = flights.get(id);
                        if (flight != null) {
                            flights.remove(id);
                            System.out.println("Flight Deleted Successfully\n");
                        } else {
                            System.out.println("Flight ID does not exist\n");
                        }
                    } catch (InputMismatchException e) {
                        System.out.println("Invalid input. Please enter a valid Flight ID.\n");
                        sc.nextLine(); // Consume the invalid input
                    }
                }
            }

            // Method to add a new staff account
            public static void addStaffAccount(Scanner sc) {
                try {
                    String username = getInputField("Staff UserName", sc);

                    // Check if a staff account with the same username already exists
                    if (isUsernameDuplicate(username)) {
                        System.out.println("Username is already in use. Please choose a different username.\n");
                        return; // Exit the method without adding the account
                    }

                    String password = getInputField("Staff Password", sc);

                    // Create a new StaffAccount object and add it to the list
                    StaffAccount staffAccount = new StaffAccount(username, password);
                    staffAccounts.add(staffAccount); // Add the new account to the list
                    System.out.println("Staff Account Added Successfully\n");
                } catch (InputMismatchException e) {
                    System.out.println("Invalid input. Please enter valid data.\n");
                    sc.nextLine(); // Consume the invalid input
                }
            }

            // Helper method to check if a staff account with the same username exists
            private static boolean isUsernameDuplicate(String username) {
                for (StaffAccount staff : staffAccounts) {
                    if (staff.getUsername().equalsIgnoreCase(username)) {
                        return true; // Username is already in use
                    }
                }
                return false; // Username is not a duplicate
            }
        }

        static class Customer extends Staff {
        private static ArrayList<Booking> bookings = new ArrayList<>();

        // Getter for bookings
        public static ArrayList<Booking> getBookings() {
            return bookings;
        }

        // Method to add a booking
        public static void addBooking(Booking booking) {
            bookings.add(booking);
        }

        public static void customer_menu(Scanner sc) {
            int ch = 0;
            while (ch != 5) {
                try {
                    // Display customer menu options
                    System.out.println("\nCustomer Menu");
                    System.out.println("1 - Book Flights");
                    System.out.println("2 - Check-In");
                    System.out.println("3 - View Booking History");
                    System.out.println("4 - Manage Reservations");
                    System.out.println("5 - Exit\n");

                    System.out.print("Enter your choice: ");
                    ch = sc.nextInt();
                    sc.nextLine();

                    switch (ch) {
                        case 1:
                            book(sc);
                            break;
                        case 2:
                            check_in(sc);
                            break;
                        case 3:
                            viewBookingHistory();
                            break;
                        case 4:
                            manageReservations(sc);
                            break;
                        case 5:
                            System.out.println("Customer Logged Out\n");
                            break;
                        default:
                            System.out.println("Invalid choice. Please try again.\n");
                            break;
                    }
                } catch (InputMismatchException e) {
                    System.out.println("Invalid input. Please enter a valid option.\n");
                    sc.nextLine(); // Consume the invalid input
                }
            }
        }

        public static void check_in(Scanner sc) {
            if (bookings.isEmpty()) {
                System.out.println("No Bookings available.\n");
            } else {
                try {
                    // Get booking details for check-in
                    String customerName = getInputField("Customer Name", sc);
                    String source = getInputField("Source", sc);
                    String destination = getInputField("Destination", sc);
                    String flightId = getFormattedFlightID("Flight ID", sc);
                    String flightTime = getInputField("Flight Time (HH:mm)", sc);
        
                    for (Booking booking : bookings) {
                        if (booking.getCustomerName().equalsIgnoreCase(customerName) &&
                            booking.getFlight().getSource().equalsIgnoreCase(source) &&
                            booking.getFlight().getDestination().equalsIgnoreCase(destination) &&
                            booking.getFlight().getId().equalsIgnoreCase(flightId) &&
                            booking.getFlight().getTime().equals(flightTime)) {
        
                            Flight flight = flights.get(flightId); // Get the corresponding flight
        
                            if (flight != null) {
                                if (flight.getStatus().equalsIgnoreCase("Operational")) {
                                    // Check luggage weight
                                    String luggageWeight = getInputField("Customer's Luggage Weight (in kg)", sc);
                                    if (luggageWeight.isBlank()) {
                                        luggageWeight = "0";
                                    }
        
                                    // Get the luggage limit of the specific plane
                                    double luggageLimit = Double.parseDouble(flight.getLuggageWeightLimit());
        
                                    if (Double.parseDouble(luggageWeight) <= luggageLimit) {
                                        System.out.println("Check-In Successful\n");
                                    } else {
                                        System.out.println("Luggage weight exceeds the limit for this flight!!!\nExcess Luggage Fee will be charged.\n");
                                    }
                                    break;
                                } else if (flight.getStatus().equalsIgnoreCase("Delayed")) {
                                    System.out.println("Flight Delayed\n");
                                    break;
                                } else {
                                    System.out.println("Flight Cancelled\n");
                                    break;
                                }
                            }
                        }
                    }
                } catch (InputMismatchException e) {
                    System.out.println("Invalid input. Please enter valid data.\n");
                    sc.nextLine(); // Consume the invalid input
                }
            }
        }

            public static void flight_details(String source, String destination) {
                System.out.println("\nFlight Details");
                System.out.println("Flight ID | Flight Name       | Source | Destination | Time | Status      | Price | Normal | Business Class | First Class");

                AtomicBoolean flightsFound = new AtomicBoolean(false); // Flag to track if any flights are found

                flights.entrySet().stream()
                        .filter(entry -> entry.getValue().getSource().equalsIgnoreCase(source) && entry.getValue().getDestination().equalsIgnoreCase(destination))
                        .forEach(entry -> {
                            System.out.printf("%-10s| %-18s| %-7s| %-12s| %-6s| %-12s| %-6s| %-7s| %-15s| %-12s%n",
                                    entry.getValue().getId(),
                                    entry.getValue().getName(),
                                    entry.getValue().getSource(),
                                    entry.getValue().getDestination(),
                                    entry.getValue().getTime(),
                                    entry.getValue().getStatus(),
                                    entry.getValue().getPrice(),
                                    entry.getValue().getNormalSeats(),
                                    entry.getValue().getBusinessSeats(),
                                    entry.getValue().getFirstClassSeats());
                            flightsFound.set(true); // Set the flag to true if flights are found
                        });

                if (!flightsFound.get()) {
                    System.out.println("No flights found from " + source + " to " + destination + "\n");
                } else {
                    System.out.println();
                }
            }



            public static void book(Scanner sc) {
                if (flights.isEmpty()) {
                    System.out.println("No Flights available.\n");
                    return;
                }

                try (sc) { // Use try-with-resources to ensure the scanner is closed
                    String source = getInputField("Source", sc);
                    String destination = getInputField("Destination", sc);
                    if (source.equalsIgnoreCase(destination)) {
                        System.out.println("Source and Destination cannot be the same\n");
                        return;
                    }

                    System.out.println("Available Flights from " + source + " to " + destination + ":");
                    int flightIndex = 1;
                    boolean flightsFound = false;
                    for (Flight flight : flights.values()) {
                        if (flight.getSource().equalsIgnoreCase(source) && flight.getDestination().equalsIgnoreCase(destination)) {
                            System.out.println(flightIndex + ". " + flight.getName() + " - " + flight.getTime());
                            flightIndex++;
                            flightsFound = true;
                        }
                    }

                    if (!flightsFound) {
                        System.out.println("No matching flights found.\n");
                        return;
                    }

                    System.out.print("Enter the number of the flight you want to book: ");
                    int selectedFlightIndex = sc.nextInt();
                    sc.nextLine(); // Consume newline
                    if (selectedFlightIndex < 1 || selectedFlightIndex > flightIndex - 1) {
                        System.out.println("Invalid flight number.\n");
                        return;
                    }

                    // Assuming flights are stored in a List or a similar collection for easier access
                    Flight selectedFlight = flights.values().stream()
                            .filter(flight -> flight.getSource().equalsIgnoreCase(source) && flight.getDestination().equalsIgnoreCase(destination))
                            .skip(selectedFlightIndex - 1)
                            .findFirst()
                            .orElse(null);

                    if (selectedFlight == null) {
                        System.out.println("Flight not found.\n");
                        return;
                    }

                    // Continue with the rest of the booking process...
                } catch (InputMismatchException e) {
                    System.out.println("Invalid input. Please enter valid data.\n");
                    sc.nextLine(); // Consume the invalid input
                }
            }


            public static void viewBookingHistory() {
            if (bookings.isEmpty()) {
                System.out.println("No Bookings available.\n");
            } 
            else
            {
                System.out.println("\nBooking History for Passenger: " + currentCustomer.getId());
                System.out.println("Passenger Name | Flight ID | Flight Name       | Time | Source | Destination | Seat Category | Check-In Option");

                for (Booking booking : bookings) {
                    if (booking.getCustomerId().equals(currentCustomer.getId())) {
                        System.out.printf("%-14s | %-9s | %-16s | %-8s | %-6s | %-12s | %-13s | %-15s%n", booking.getCustomerName(), booking.getFlight().getId(), booking.getFlight().getName(), booking.getFlight().getTime(), booking.getFlight().getSource(), booking.getFlight().getDestination(), booking.getSeatCategory(), booking.getCheckInOption());
                    }
                }
                System.out.println();
            }
        }

        public static void manageReservations(Scanner sc) {
            if (bookings.isEmpty()) {
                System.out.println("No Bookings available.\n");
            } else {
                try {
                    // Get flight ID and customer name for managing reservations
                    String id = getFormattedFlightID("Flight ID to manage reservation", sc);
                    String customerName = getInputField("Customer Name", sc);
                    
                    for (Booking booking : bookings) {
                        if (booking.getFlight().getId().equalsIgnoreCase(id) && booking.getCustomerName().equalsIgnoreCase(customerName)) {
                            // Display reservation management options
                            System.out.println("1 - Cancel Reservation");
                            System.out.println("2 - Update Seat Category");
                            System.out.println("3 - Exit");
                            System.out.print("Enter your choice: ");
                            String choice = sc.nextLine();
                            if (choice.isBlank())
                            {
                                System.out.println("Choice cannot be blank\n");
                                return;
                            } 

                            switch (Integer.parseInt(choice)) {
                                case 1:
                                    cancelReservation(booking);
                                    break;
                                case 2:
                                    updateSeatCategory(booking, sc);
                                    break;
                                case 3:
                                    return;
                                default:
                                    System.out.println("Invalid choice. Please try again.\n");
                                    break;
                            }
                            return;
                        }
                    }

                    // Booking not found, print error message
                    System.out.println("Booking not found for the given Flight ID and Customer Name.\n");
                } catch (InputMismatchException e) {
                    System.out.println("Invalid input. Please enter a valid option.\n");
                    sc.nextLine(); // Consume the invalid input
                }
            }
        }

        public static void cancelReservation(Booking booking) {
            if (bookings.isEmpty()) {
                System.out.println("No Bookings available.\n");
            } else {
                Flight flight = booking.getFlight();
                String seatCategory = booking.getSeatCategory();
                flight.incrementSeat(seatCategory); // Increase the seat count for the canceled seat
                bookings.remove(booking); // Remove the booking from the list
                System.out.println("Reservation Cancelled Successfully\n");
            }
        }

            public static void updateSeatCategory(Booking booking, Scanner sc) {
                if (flights.isEmpty()) {
                    System.out.println("No Flights available.\n");
                } else {
                    Flight flight = booking.getFlight();
                    String seatCategory = booking.getSeatCategory();
                    String newSeatCategory = "";
                    int newPrice = 0;

                    // Predefined list of seat categories available for booking
                    List<String> availableSeatCategories = new ArrayList<>();
                    availableSeatCategories.add("Normal Class");
                    availableSeatCategories.add("Business Class");
                    availableSeatCategories.add("First Class");

                    // Create a map to associate each seat category with a unique integer
                    Map<Integer, String> seatCategoryChoices = new HashMap<>();
                    for (int i = 0; i < availableSeatCategories.size(); i++) {
                        seatCategoryChoices.put(i + 1, availableSeatCategories.get(i));
                    }

                    // Display available seat categories with their integer choices
                    System.out.println("Available Seat Categories:");
                    for (Map.Entry<Integer, String> entry : seatCategoryChoices.entrySet()) {
                        if (!entry.getValue().equalsIgnoreCase(seatCategory)) {
                            System.out.println(entry.getKey() + " - " + entry.getValue());
                        }
                    }

                    // Ask the user to enter the integer choice for the new seat category
                    System.out.print("Enter new seat category choice: ");
                    int choice = sc.nextInt();
                    sc.nextLine(); // Consume the newline left by nextInt()

                    // Use the choice to determine the new seat category
                    newSeatCategory = seatCategoryChoices.get(choice);
                    if (newSeatCategory == null) {
                        System.out.println("Invalid choice. Cannot update.\n");
                        return;
                    }

                    // Determine the new price based on the new seat category
                    switch (newSeatCategory.toLowerCase()) {
                        case "normal class":
                            newPrice = flight.getPrice() + 50;
                            break;
                        case "business class":
                            newPrice = flight.getPrice() + 100;
                            break;
                        case "first class":
                            newPrice = flight.getPrice() + 200;
                            break;
                        default:
                            System.out.println("Invalid seat category. Cannot update.\n");
                            return;
                    }

                    if (flight.hasAvailableSeat(newSeatCategory)) {
                        // Increment the new seat category
                        flight.incrementSeat(newSeatCategory);
                        // Decrement the old seat category
                        flight.decrementSeat(seatCategory);
                        // Update the booking's seat category and price
                        booking.setSeatCategory(newSeatCategory);
                        booking.setPrice(newPrice);
                        System.out.println("Seat Category Updated Successfully\n");
                    } else {
                        System.out.println("No available seats in the new category. Cannot update.\n");
                    }
                }
            }


        }
}