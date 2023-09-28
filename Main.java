import java.io.*;
import java.util.*;

class Main {
    static Scanner sc = new Scanner(System.in);
    public static List<Flight> flights = new ArrayList<>();
    public static List<Booking> bookings = new ArrayList<>();
    public static List<StaffAccount> staffAccounts = new ArrayList<>();
    public static List<AdminAccount> adminAccounts = new ArrayList<>();
    public static List<CustomerAccount> customerAccounts = new ArrayList<>();
    static int flag = 0;
    static CustomerAccount currentCustomer = null;

    public static void main(String[] args) {
        // Register a shutdown hook
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

        int ch = 0;
        while (ch != 9) {
            try {
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
                        adminSignUp(sc);
                        break;
                    case 2:
                        adminLogin(sc);
                        break;
                    case 3:
                        staffLogin(sc);
                        break;
                    case 4:
                        staffRecovery(sc);
                        break;
                    case 5:
                        customerSignUp(sc);
                        break;
                    case 6:
                        customerLogin(sc);
                        break;
                    case 7:
                        customerRecovery(sc);
                        break;
                    case 8:
                        deleteCustomer(sc);
                        break;
                    case 9:
                        System.out.println("Thank You for using our services");
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                        break;
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid option.\n");
                sc.nextLine(); // Consume the invalid input
            }
        }
    }

    public static String getInputField(String inputField, Scanner sc) {
        System.out.print("Enter the " + inputField + ": ");
        String input = sc.nextLine();
        if (input.isBlank()) {
            System.out.println(inputField + " cannot be blank\n");
            return getInputField(inputField, sc);
        }
        return input;
    }

    public static int parseIntWithValidation(String fieldName, Scanner sc) {
        while (true) {
            try {
                System.out.print("Enter the " + fieldName + ": ");
                int input = Integer.parseInt(sc.nextLine());
                if (input < 0) {
                    System.out.println(fieldName + " cannot be negative\n");
                } else {
                    return input;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid " + fieldName + ". Please enter a valid integer.\n");
                parseIntWithValidation(fieldName, sc);
            }
        }
    }

    public static String getFormattedFlightID(String inputField, Scanner sc) {
        while (true) {
            System.out.print("Enter the " + inputField + ": ");
            String input = sc.nextLine();
            if (input.isBlank()) {
                System.out.println(inputField + " cannot be blank\n");
                continue;
            }
            try {
                int id = Integer.parseInt(input);
                if (id >= 0 && id <= 999) {
                    // Format the ID to have three digits
                    return String.format("%03d", id);
                } else {
                    System.out.println(inputField + " must be between 0 and 999\n");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid " + inputField + ". Please enter a valid integer.\n");
            }
        }
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

    // Function to delete a Customer Account
    public static void deleteCustomer(Scanner sc) {
        if (customerAccounts.isEmpty()) {
            System.out.println("No Customer available.\n");
        } else {
            try {
                String username = getInputField("Customer Username", sc);
                String password = getInputField("Customer Password", sc);
                for (CustomerAccount customer : customerAccounts) {
                    flag = 0;
                    if (customer.getUsername().equalsIgnoreCase(username) && customer.getPassword().equals(password)) {
                        flag = 1;
                        customerAccounts.remove(customer);
                        System.out.println("Customer Account Deleted\n");
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
            
            String password = getInputField("Admin Password", sc);

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
                    String id = parts[0];
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
                    String id = parts[0];
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
                    String id = parts[0];
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
            flights = (List<Flight>) flightsInput.readObject();
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
        public static void staff_menu(Scanner sc)
        {
            Admin adm = new Admin();
            int ch = 0;
            while (ch != 5) {
                try {
                    // Display staff menu options.
                    System.out.println("\nStaff Menu");
                    System.out.println("1 - Update Flight Status");
                    System.out.println("2 - Update Seat Availability");
                    System.out.println("3 - Passenger Details");
                    System.out.println("4 - Display Flights");
                    System.out.println("5 - Exit");

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
                            updateFlightStatus(sc);
                            break;
                        case 2:
                            updateSeatAvailability(sc);
                            break;
                        case 3:
                            // Prompt for customer and flight information and display passenger details.
                            String cus_id = getInputField("Customer Id", sc);
                            System.out.print("Enter the Flight Id: ");
                            String flight_id = getFormattedFlightID("Flight ID", sc);
                            passenger_details(cus_id, flight_id);
                            break;
                        case 4:
                            adm.display_flights();
                            break;
                        case 5:
                            System.out.println("Staff Logged Out\n");
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

        // Method to perform passenger check-in.
        public static void check_in(Scanner sc) {
            if (bookings.isEmpty()) {
                System.out.println("No Bookings available.\n");
            } else {
                try {
                    String id = getFormattedFlightID("Flight ID", sc);
                    String name = getInputField("Flight Name", sc);
                    String source = getInputField("Source", sc);
                    String destination = getInputField("Destination", sc);
                    if (source.equalsIgnoreCase(destination)) {
                        System.out.println("Source and Destination cannot be the same\n");
                        getInputField(destination, sc);
                    }
                    for (Flight flight : flights) {
                        if (flight.getId().equalsIgnoreCase(id)) {
                            if (flight.getName().equalsIgnoreCase(name) && flight.getSource().equalsIgnoreCase(source)
                                    && flight.getDestination().equalsIgnoreCase(destination)) {
                                if (flight.getStatus().equalsIgnoreCase("Operational")) {
                                    System.out.println("Check-In Successful\n");
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

        // Method to update flight status.
        public static void updateFlightStatus(Scanner sc) {
            if (flights.isEmpty()) {
                System.out.println("No Flights available.\n");
            } else {
                try {
                    String id = getFormattedFlightID("Flight ID", sc);
                    String newStatus = getInputField("new status", sc);

                    for (Flight flight : flights) {
                        if (flight.getId().equalsIgnoreCase(id)) {
                            flight.setStatus(newStatus);
                            System.out.println("Flight Status Updated Successfully\n");
                            return;
                        }
                    }

                    // Flight not found, print error message
                    System.out.println("Flight ID does not exist\n");
                } catch (InputMismatchException e) {
                    System.out.println("Invalid input. Please enter valid data.\n");
                    sc.nextLine(); // Consume the invalid input
                }
            }
        }

        // Method to update seat availability.
        public static void updateSeatAvailability(Scanner sc) {
            if (flights.isEmpty()) {
                System.out.println("No Flights available.\n");
            } else {
                try {
                    String id = getFormattedFlightID("Flight ID", sc);
                    String seatCategory = getInputField("seat category (Normal/Business/First)", sc);
                    int newSeatCount = parseIntWithValidation("seat count", sc);
                    
                    for (Flight flight : flights) {
                        if (flight.getId().equalsIgnoreCase(id)) {
                            if (seatCategory.equalsIgnoreCase("Normal")) {
                                flight.setNormalSeats(newSeatCount);
                            } else if (seatCategory.equalsIgnoreCase("Business")) {
                                flight.setBusinessSeats(newSeatCount);
                            } else if (seatCategory.equalsIgnoreCase("First")) {
                                flight.setFirstClassSeats(newSeatCount);
                            } else {
                                System.out.println("Invalid seat category. Please enter Normal, Business, or First.\n");
                                return;
                            }
                            System.out.println("Seat Availability Updated Successfully\n");
                            return;
                        }
                    }

                    // Flight not found, print error message
                    System.out.println("Flight ID does not exist\n");
                } catch (InputMismatchException e) {
                    System.out.println("Invalid input. Please enter valid data.\n");
                    sc.nextLine(); // Consume the invalid input
                }
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
                                delete_flights(sc);
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

            // Method to delete a staff account.
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
                    System.out.println("\nFlight Details");
                    System.out.println("Flight ID | Flight Name       | Source   | Destination | Time  | Status      | Price | Normal | Business Class | First Class ");
                    for (Flight flight : flights)
                        System.out.printf("%-10s| %-18s| %-9s| %-12s| %-6s| %-12s| %-6s| %-7s| %-15s| %-12s%n", flight.getId(), flight.getName(), flight.getSource(), flight.getDestination(), flight.getTime(), flight.getStatus(), flight.getPrice(), flight.getNormalSeats(), flight.getBusinessSeats(), flight.getFirstClassSeats());
                    System.out.println();
                }
            }

            // Method to add a new flight.
            public static void add_flights(Scanner sc) {
                String id = getFormattedFlightID("Flight ID", sc);
                String name = getInputField("Flight Name", sc);
                String source = getInputField("Source", sc);
                String destination = getInputField("Destination", sc);
                if (source.equalsIgnoreCase(destination)) {
                    System.out.println("Source and Destination cannot be the same\n");
                    getInputField(destination, sc);
                }
                String newTime = getInputField("New Time (HH:mm)", sc);
                String status = getInputField("Status", sc);
                int price = parseIntWithValidation("Price", sc);
                int normalSeats = parseIntWithValidation("Normal Seats", sc);
                int businessSeats = parseIntWithValidation("Business Class Seats", sc);
                int firstClassSeats = parseIntWithValidation("First Class Seats", sc);
                String luggageLimit = getInputField("Luggage Weight Limit (optional, press Enter to skip)", sc);
                if (luggageLimit.isBlank()) {
                    luggageLimit = "0";
                }

                // Add the flight to the list of flights
                Flight newFlight = new Flight(id, name, source, destination, newTime, status, price, normalSeats, businessSeats, firstClassSeats, luggageLimit);
                flights.add(newFlight);
                System.out.println("Flight Added Successfully\n");
            }

            // Method to update flight details selectively.
            public static void update_flights(Scanner sc) {
                if (flights.isEmpty()) {
                    System.out.println("No Flights available.\n");
                } else {
                    try {
                        String id = getFormattedFlightID("Flight ID", sc);

                        Flight selectedFlight = null;
                        for (Flight flight : flights) {
                            if (flight.getId().equalsIgnoreCase(id)) {
                                selectedFlight = flight;
                                break;
                            }
                        }

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
                                String input = sc.nextLine().trim(); // Get the user input and remove leading/trailing spaces

                                if (input.isEmpty()) {
                                    System.out.println("Invalid input. Please enter a valid index.");
                                    continue; // Restart the loop if input is empty
                                }

                                int choice;
                                try {
                                    choice = Integer.parseInt(input);
                                } catch (NumberFormatException e) {
                                    System.out.println("Invalid input. Please enter a valid index.");
                                    continue; // Restart the loop if input is not a valid integer
                                }

                                switch (choice) {
                                    case 0:
                                        updating = false;
                                        break;
                                    case 1:
                                        String newName = getInputField("Flight Name", sc);
                                        if (!newName.isBlank()) {
                                            selectedFlight.setName(newName);
                                        } else {
                                            System.out.println("Flight Name cannot be blank.");
                                        }
                                        break;
                                    case 2:
                                        String newSource = getInputField("Source", sc);
                                        if (!newSource.isBlank()) {
                                            selectedFlight.setSource(newSource);
                                        } else {
                                            System.out.println("Source cannot be blank.");
                                        }
                                        break;
                                    case 3:
                                        String newDestination = getInputField("Destination", sc);
                                        if (!newDestination.isBlank()) {
                                            if (!newDestination.equalsIgnoreCase(selectedFlight.getSource())) {
                                                selectedFlight.setDestination(newDestination);
                                            } else {
                                                System.out.println("Destination cannot be the same as the source.");
                                            }
                                        } else {
                                            System.out.println("Destination cannot be blank.");
                                        }
                                        break;
                                    case 4:
                                        String newTime = getInputField("New Time (HH:mm)", sc);
                                        if (isValidTimeFormat(newTime)) {
                                            selectedFlight.setTime(newTime);
                                        } else {
                                            System.out.println("Invalid time format. Please use HH:mm format.");
                                        }
                                        break;
                                    case 5:
                                        String newStatus = getInputField("Status", sc);
                                        selectedFlight.setStatus(newStatus);
                                        break;
                                    case 6:
                                        int newPrice = parseIntWithValidation("Price", sc);
                                        selectedFlight.setPrice(newPrice);
                                        break;
                                    case 7:
                                        int newNormalSeats = parseIntWithValidation("Normal Seats", sc);
                                        selectedFlight.setNormalSeats(newNormalSeats);
                                        break;
                                    case 8:
                                        int newBusinessSeats = parseIntWithValidation("Business Class Seats", sc);
                                        selectedFlight.setBusinessSeats(newBusinessSeats);
                                        break;
                                    case 9:
                                        int newFirstClassSeats = parseIntWithValidation("First Class Seats", sc);
                                        selectedFlight.setFirstClassSeats(newFirstClassSeats);
                                        break;
                                    case 10:
                                        String newLuggageLimit = getInputField("Luggage Weight Limit (optional, press Enter to skip)", sc);
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
                        sc.nextLine(); // Consume the invalid input
                    }
                }
            }
            // Check if the input time is in HH:mm format
            private static boolean isValidTimeFormat(String time) {
                return time.matches("^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$");
            }

            // Method to delete a flight.
            public static void delete_flights(Scanner sc) {
                if (flights.isEmpty()) {
                    System.out.println("No Flights available.\n");
                } else {
                    try {
                        String id = getFormattedFlightID("Flight ID", sc);

                        boolean flightFound = false;
                        Iterator<Flight> iterator = flights.iterator();
                        while (iterator.hasNext()) {
                            Flight flight = iterator.next();
                            if (flight.getId().equalsIgnoreCase(id)) {
                                iterator.remove();
                                flightFound = true;
                                System.out.println("Flight Deleted Successfully\n");
                                break;
                            }
                        }

                        if (!flightFound) {
                            System.out.println("Flight ID does not exist\n");
                        }
                    } catch (InputMismatchException e) {
                        System.out.println("Invalid input. Please enter a valid Flight ID.\n");
                        sc.nextLine(); // Consume the invalid input
                    }
                }
            }

            // Method to add a new staff account.
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
        
                            Flight flight = getFlightById(flightId); // Get the corresponding flight
        
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
        
        // Helper method to get a flight by its ID
        private static Flight getFlightById(String flightId) {
            for (Flight flight : flights) {
                if (flight.getId().equalsIgnoreCase(flightId)) {
                    return flight;
                }
            }
            return null; // Flight not found
        }        

        public static void flight_details(String source, String destination) {
            boolean flightsFound = false; // Flag to track if any flights are found
            int count = 0;
            for (Flight flight : flights) {
                if (count == 0) {
                    System.out.println("\nFlight Details");
                    System.out.println("Flight ID | Flight Name       | Source | Destination | Time  | Status      | Price | Normal | Business Class | First Class");
                }
                if (flight.getSource().equalsIgnoreCase(source) && flight.getDestination().equalsIgnoreCase(destination)) {
                    System.out.printf("%-10s| %-18s| %-7s| %-12s| %-6s| %-12s| %-6s| %-7s| %-15s| %-12s%n", flight.getId(), flight.getName(), flight.getSource(), flight.getDestination(), flight.getTime(), flight.getStatus(), flight.getPrice(), flight.getNormalSeats(), flight.getBusinessSeats(), flight.getFirstClassSeats());
                    flightsFound = true; // Set the flag to true if flights are found
                }
                count++;
            }

            if (!flightsFound) {
                System.out.println("No flights found from " + source + " to " + destination + "\n");
            } else {
                System.out.println();
            }
        }

        public static void book(Scanner sc) {
            int tot_price = 0;
            boolean flightsFound = false; // Flag to track if any flights are found
        
            if (flights.isEmpty()) {
                System.out.println("No Flights available.\n");
            } else {
                try {
                    // Get booking details
                    String source = getInputField("Source", sc);
                    String destination = getInputField("Destination", sc);
                    if (source.equalsIgnoreCase(destination)) {
                        System.out.println("Source and Destination cannot be the same\n");
                        getInputField(destination, sc);
                    }
        
                    // Display available flights from source to destination
                    int flightIndex = 1;
                    System.out.println("Available Flights from " + source + " to " + destination + ":");
                    Map<Integer, Flight> flightMap = new HashMap<>();
                    for (Flight flight : flights) {
                        if (flight.getSource().equalsIgnoreCase(source) && flight.getDestination().equalsIgnoreCase(destination)) {
                            flightMap.put(flightIndex, flight);
                            System.out.println(flightIndex + ". " + flight.getName() + " - " + flight.getTime());
                            flightIndex++;
                        }
                    }
        
                    if (flightIndex == 1) {
                        System.out.println("No matching flights found.\n");
                        return; // Exit the method if no flights are found
                    }
        
                    // Ask the user to select a flight
                    System.out.print("Enter the number of the flight you want to book: ");
                    String selectedFlightIndex = sc.nextLine();
                    if (selectedFlightIndex.isBlank())
                    {
                        System.out.println("Flight number cannot be blank\n");
                        return;
                    }
        
                    Flight selectedFlight = flightMap.get(Integer.parseInt(selectedFlightIndex));
        
                    String name = getInputField("Customer Name", sc);
                
                    System.out.println("Seats Available");
                    System.out.println("1. Normal Class Seats: " + selectedFlight.getNormalSeats());
                    System.out.println("2. Business Class Seats: " + selectedFlight.getBusinessSeats());
                    System.out.println("3. First Class Seats: " + selectedFlight.getFirstClassSeats());
                    String seatCategoryIndex = getInputField("Seat Category (1 for Normal, 2 for Business, 3 for First Class)", sc);
                    System.out.println("Check-In Options");
                    System.out.println("1. Online Check-In");
                    System.out.println("2. Airport Check-In");
                    System.out.println("3. Private Terminal or Lounge");
                    System.out.println("4. Charter and Private Jet Services");
                    String checkInOptionIndex = getInputField("Enter the Check-In Option (1 for Online, 2 for Airport, 3 for Private Terminal, 4 for Charter)", sc); 
                            
                    // Define seat category and check-in option maps
                    Map<Integer, String> seatCategoryMap = Map.of(1, "normal class", 2, "business class", 3, "first class");
                    Map<Integer, String> checkInOptionMap = Map.of(1, "online check-in", 2, "airport check-in", 3, "private terminal or lounge", 4, "charter and private jet services");
        
                    // Get seat category and check-in option based on user input
                    String seatCategory = seatCategoryMap.getOrDefault(Integer.parseInt(seatCategoryIndex), "");
                    String checkInOption = checkInOptionMap.getOrDefault(Integer.parseInt(checkInOptionIndex), "");
        
                    // Check if the selected options are valid
                    if (seatCategory.isEmpty() || checkInOption.isEmpty()) {
                        System.out.println("Invalid seat category or check-in option. Please try again.\n");
                        return;
                    }
        
                    // Define seat category prices
                    Map<String, Integer> seatCategoryPrices = new HashMap<>();
                    seatCategoryPrices.put("normal class", 1500);
                    seatCategoryPrices.put("business class", 9000);
                    seatCategoryPrices.put("first class", 19000);
        
                    // Define check-in option prices
                    Map<String, Integer> checkInOptionPrices = new HashMap<>();
                    checkInOptionPrices.put("airport check-in", 0);
                    checkInOptionPrices.put("online check-in", 5000);
                    checkInOptionPrices.put("private terminal or lounge", 10000);
                    checkInOptionPrices.put("charter and private jet services", 15000);
        
                    if (selectedFlight != null) {
                        if (currentCustomer != null) {
                            tot_price += selectedFlight.getPrice();
        
                            // Calculate prices based on seat category and check-in option
                            int seatCategoryPrice = seatCategoryPrices.getOrDefault(seatCategory.toLowerCase(), 0);
                            int checkInOptionPrice = checkInOptionPrices.getOrDefault(checkInOption.toLowerCase(), 0);
        
                            tot_price += seatCategoryPrice;
                            tot_price += checkInOptionPrice;
        
                            // Handle seat bookings based on selected category
                            if (seatCategory.equalsIgnoreCase("normal class") && selectedFlight.getNormalSeats() > 0) {
                                Booking booking = new Booking(currentCustomer.getId(), name, selectedFlight, seatCategory, checkInOption, tot_price);
                                // Add the booking to the customer's bookings
                                Customer.addBooking(booking);
                                selectedFlight.setNormalSeats(selectedFlight.getNormalSeats() - 1);
                                System.out.println("Booking Successful\n");
                            } else if (seatCategory.equalsIgnoreCase("business class") && selectedFlight.getBusinessSeats() > 0) {
                                Booking booking = new Booking(currentCustomer.getId(), name, selectedFlight, seatCategory, checkInOption, tot_price);
                                // Add the booking to the customer's bookings
                                Customer.addBooking(booking);
                                selectedFlight.setBusinessSeats(selectedFlight.getBusinessSeats() - 1);
                                System.out.println("Booking Successful\n");
                            } else if (seatCategory.equalsIgnoreCase("first class") && selectedFlight.getFirstClassSeats() > 0) {
                                Booking booking = new Booking(currentCustomer.getId(), name, selectedFlight, seatCategory, checkInOption, tot_price);
                                // Add the booking to the customer's bookings
                                Customer.addBooking(booking);
                                selectedFlight.setFirstClassSeats(selectedFlight.getFirstClassSeats() - 1);
                                System.out.println("Booking Successful\n");
                            } else {
                                System.out.println("No available seats in the selected category.\n");
                            }
        
                            if (flightsFound) {
                                System.out.println("Booking Details");
                                System.out.println("Passenger Name: " + name);
                                System.out.println("Flight ID: " + selectedFlight.getId());
                                System.out.println("Flight Name: " + selectedFlight.getName());
                                System.out.println("Flight Time: " + selectedFlight.getTime());
                                System.out.println("Source: " + selectedFlight.getSource());
                                System.out.println("Destination: " + selectedFlight.getDestination());
                                System.out.println("Seat Category: " + seatCategory);
                                System.out.println("Check-In Option: " + checkInOption);
                                System.out.println("Total Price: " + tot_price);
                                System.out.println();
                            }
                        } else {
                            System.out.println("Customer not found.\n");
                        }
                    }
                } catch (InputMismatchException e) {
                    System.out.println("Invalid input. Please enter valid data.\n");
                    sc.nextLine(); // Consume the invalid input
                }
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

                switch (seatCategory.toLowerCase()) {
                    case "normal class":
                        newSeatCategory = "Business Class";
                        newPrice = flight.getPrice() + 100;
                        break;
                    case "business class":
                        newSeatCategory = "First Class";
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