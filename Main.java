import java.io.*;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

class Main {
    static Scanner sc = new Scanner(System.in);
    static List<Flight> flights = new ArrayList<>();
    static List<Booking> bookings = new ArrayList<>();
    static List<StaffAccount> staffAccounts = new ArrayList<>();
    static List<AdminAccount> adminAccounts = new ArrayList<>();
    static List<CustomerAccount> customerAccounts = new ArrayList<>();
    static int flag = 0;
    static CustomerAccount currentCustomer = null;

    public static void main(String[] args) {
        // Load data from files only if they are not empty
        if (areFilesNonEmpty("flights.txt", "bookings.txt", "staff.txt", "admin_accounts.txt", "customer_accounts.txt")) {
            loadFlightsAndBookings();
            loadStaffAccounts();
            loadAdminAccounts();
            loadCustomerAccounts();
        }

        int ch = 0;
        while (ch != 8) {
            try {
                System.out.println("Login Menu");
                System.out.println("1 - Admin Login");
                System.out.println("2 - Staff Login");
                System.out.println("3 - Staff Account Recovery");
                System.out.println("4 - Customer SignUp");
                System.out.println("5 - Customer Login");
                System.out.println("6 - Customer Account Recovery");
                System.out.println("7 - Delete Customer Account");
                System.out.println("8 - Exit");

                System.out.print("Enter your choice: ");
                ch = sc.nextInt();
                sc.nextLine(); // Consume newline

                switch (ch) {
                    case 1:
                        adminLogin(sc);
                        break;
                    case 2:
                        staffLogin(sc);
                        break;
                    case 3:
                        staffRecovery(sc);
                        break;
                    case 4:
                        customerSignUp(sc);
                        break;
                    case 5:
                        customerLogin(sc);
                        break;
                    case 6:
                        customerRecovery(sc);
                        break;
                    case 7:
                        deleteCustomer(sc);
                        break;
                    case 8:
                        System.out.println("Thank You for using our services");
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                        break;
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid option.");
                sc.nextLine(); // Consume the invalid input
            }
        }
        saveFlightsAndBookings();
        saveStaffAccounts();
        saveAdminAccounts();
        saveCustomerAccounts();
    }

    // Function to handle Staff Account Recovery
    public static void staffRecovery(Scanner sc) {
        if (staffAccounts.isEmpty()) {
            System.out.println("No Staff available.\n");
        } else {
            try {
                System.out.print("\nEnter the Staff Username: ");
                String username = sc.nextLine();
                for (StaffAccount staff : staffAccounts) {
                    flag = 0;
                    if (staff.getUsername().equalsIgnoreCase(username)) {
                        flag = 1;
                        System.out.print("Enter the Staff Password: ");
                        String password = sc.nextLine();
                        staff.setPassword(password);
                        System.out.println("Staff Password Updated\n");
                    }
                }
                if (flag == 0)
                    System.out.println("Invalid Username\n");
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid option.");
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
                System.out.print("\nEnter the Customer Username: ");
                String username = sc.nextLine();
                System.out.print("Enter the Customer Password: ");
                String password = sc.nextLine();
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
                System.out.println("Invalid input. Please enter a valid option.");
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
                System.out.print("\nEnter the Customer Username: ");
                String username = sc.nextLine();
                for (CustomerAccount customer : customerAccounts) {
                    flag = 0;
                    if (customer.getUsername().equalsIgnoreCase(username)) {
                        flag = 1;
                        System.out.print("Enter the Customer Password: ");
                        String password = sc.nextLine();
                        customer.setPassword(password);
                        System.out.println("Customer Password Updated\n");
                        break;
                    }
                }
                if (flag == 0)
                    System.out.println("Invalid Username\n");
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid option.");
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
                    System.out.print("\nEnter the Admin Username: ");
                    username = sc.nextLine();
                    System.out.print("Enter the Admin Password: ");
                    password = sc.nextLine();
                } else {
                    System.out.print("\nEnter the Admin Username: ");
                    username = sc.nextLine();
                    System.out.print("Enter the Admin Password: ");
                    password = maskPassword();
                }

                for (AdminAccount admin : adminAccounts) {
                    flag = 0;
                    if (admin.getUsername().equalsIgnoreCase(username) && admin.getPassword().equals(password)) {
                        System.out.println("Admin Logged In\n");
                        flag = 1;
                        System.out.println("Welcome " + admin.getId() + " " + admin.getUsername());
                        Admin.admin_menu(sc);
                    }
                }
                if (flag == 0)
                    System.out.println("Invalid Username or Password\n");
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid option.");
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
        System.out.print("\nEnter the Customer Username: ");
        String username = sc.nextLine();
        System.out.print("Enter the Customer Password: ");
        String password = sc.nextLine();

        CustomerAccount customer = new CustomerAccount(username, password);
        customerAccounts.add(customer);
        System.out.println("Customer Signed Up\n");
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
                    System.out.print("\nEnter the Customer Username: ");
                    username = sc.nextLine();
                    System.out.print("Enter the Customer Password: ");
                    password = sc.nextLine();
                } else {
                    System.out.print("\nEnter the Customer Username: ");
                    username = sc.nextLine();
                    System.out.print("Enter the Customer Password: ");
                    password = maskPassword();
                }

                for (CustomerAccount customer : customerAccounts) {
                    flag = 0;
                    if (customer.getUsername().equalsIgnoreCase(username) && customer.getPassword().equals(password)) {
                        System.out.println("Customer Logged In\n");
                        flag = 1;
                        currentCustomer = customer;
                        System.out.println("Welcome " + currentCustomer.getId() + " " + currentCustomer.getUsername());
                        Customer.customer_menu(sc);
                    }
                }
                if (flag == 0)
                    System.out.println("Invalid Username or Password\n");
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid option.");
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
                    System.out.print("\nEnter the Staff Username: ");
                    username = sc.nextLine();
                    System.out.print("Enter the Staff Password: ");
                    password = sc.nextLine();
                } else {
                    System.out.print("\nEnter the Staff Username: ");
                    username = sc.nextLine();
                    System.out.print("Enter the Staff Password: ");
                    password = maskPassword();
                }

                for (StaffAccount staff : staffAccounts) {
                    flag = 0;
                    if (staff.getUsername().equalsIgnoreCase(username) && staff.getPassword().equals(password)) {
                        System.out.println("Staff Logged In\n");
                        flag = 1;
                        System.out.println("Welcome " + staff.getId() + " " + staff.getUsername());
                        Staff.staff_menu(sc);
                    }
                }
                if (flag == 0)
                    System.out.println("Invalid Username or Password\n");
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid option.");
                sc.nextLine(); // Consume the invalid input
            }
        }
    }

    // Function to load admin accounts from a file
    public static void loadAdminAccounts() {
        try (Scanner scanner = new Scanner(new File("admin_accounts.txt"))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(" ");
                if (parts.length == 2) {
                    String username = parts[0];
                    String password = parts[1];
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
        try (Scanner scanner = new Scanner(new File("customer_accounts.txt"))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(" ");
                if (parts.length == 2) {
                    String username = parts[0];
                    String password = parts[1];
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
        try (ObjectInputStream staffInput = new ObjectInputStream(new FileInputStream("staff_accounts.txt"))) {
            staffAccounts = (List<StaffAccount>) staffInput.readObject();
        } catch (IOException | ClassNotFoundException e) {
            // Handle file not found exception
        }
    }

    // Function to save staff accounts to a file
    public static void saveStaffAccounts() {
        try (ObjectOutputStream staffOutput = new ObjectOutputStream(new FileOutputStream("staff_accounts.txt"))) {
            staffOutput.writeObject(staffAccounts);
        } catch (IOException e) {
            // Handle file not found exception
        }
    }

    // Function to save customer accounts to a file
    public static void saveCustomerAccounts() {
        try (PrintWriter writer = new PrintWriter(new File("customer_accounts.txt"))) {
            for (CustomerAccount customer : customerAccounts) {
                writer.println(customer.getUsername() + " " + customer.getPassword());
            }
        } catch (FileNotFoundException e) {
            // Handle file not found exception
        }
    }

    // Function to save admin accounts to a file
    public static void saveAdminAccounts() {
        try (PrintWriter writer = new PrintWriter(new File("admin_accounts.txt"))) {
            for (AdminAccount admin : adminAccounts) {
                writer.println(admin.getUsername() + " " + admin.getPassword());
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
        try (ObjectInputStream flightsInput = new ObjectInputStream(new FileInputStream("flights.txt"));
             ObjectInputStream bookingsInput = new ObjectInputStream(new FileInputStream("bookings.txt"))) {
            flights = (List<Flight>) flightsInput.readObject();
            bookings = (List<Booking>) bookingsInput.readObject();
        } catch (IOException | ClassNotFoundException e) {
            // Handle file not found exception
        }
    }

    // Function to save flights and bookings to files
    public static void saveFlightsAndBookings() {
        try (ObjectOutputStream flightsOutput = new ObjectOutputStream(new FileOutputStream("flights.txt"));
             ObjectOutputStream bookingsOutput = new ObjectOutputStream(new FileOutputStream("bookings.txt"))) {
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
            int ch = 0;
            while (ch != 4) {
                try {
                    // Display staff menu options.
                    System.out.println("\nStaff Menu");
                    System.out.println("1 - Update Flight Status");
                    System.out.println("2 - Update Seat Availability");
                    System.out.println("3 - Passenger Details");
                    System.out.println("4 - Exit");

                    System.out.print("Enter your choice: ");
                    ch = sc.nextInt();
                    sc.nextLine(); // Consume newline

                    switch (ch) {
                        case 1:
                            updateFlightStatus(sc);
                            break;
                        case 2:
                            updateSeatAvailability(sc);
                            break;
                        case 3:
                            // Prompt for customer and flight information and display passenger details.
                            System.out.print("\nEnter the Customer Id: ");
                            String cus_id = sc.nextLine();
                            System.out.print("Enter the Flight Id: ");
                            String flight_id = sc.nextLine();
                            passenger_details(cus_id, flight_id);
                            break;
                        case 4:
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
                    System.out.print("\nEnter the Flight ID: ");
                    String id = sc.nextLine();
                    System.out.print("Enter the Flight Name: ");
                    String name = sc.nextLine();
                    System.out.print("Enter the Source: ");
                    String source = sc.nextLine();
                    System.out.print("Enter the Destination: ");
                    String destination = sc.nextLine();
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
                    System.out.print("\nEnter the Flight ID to update status: ");
                    String id = sc.nextLine();
                    System.out.print("Enter the new status: ");
                    String newStatus = sc.nextLine();

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
                    System.out.print("\nEnter the Flight ID to update seat availability: ");
                    String id = sc.nextLine();
                    System.out.print("Enter the seat category (Normal/Business/First): ");
                    String seatCategory = sc.nextLine();
                    System.out.print("Enter the new seat count: ");
                    int newSeatCount = sc.nextInt();

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
            while (ch != 8) {
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
                    System.out.println("8 - Exit");

                    System.out.print("Enter your choice: ");
                    ch = sc.nextInt();
                    sc.nextLine(); // Consume newline

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

        // Method to delete a staff account.
        public static void deleteStaffAccount(Scanner sc) {
            if (staffAccounts.isEmpty()) {
                System.out.println("No Staff available.\n");
            } else {
                try {
                    System.out.print("\nEnter the Staff Username to delete: ");
                    String username = sc.nextLine();

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
                    System.out.print("\nEnter the Staff Username to update: ");
                    String username = sc.nextLine();
                    System.out.print("Enter the new Staff Password: ");
                    String password = sc.nextLine();

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
            try {
                System.out.print("\nEnter the Flight ID: ");
                String id = sc.nextLine();
                System.out.print("Enter the Flight Name: ");
                String name = sc.nextLine();
                System.out.print("Enter the Source: ");
                String source = sc.nextLine();
                System.out.print("Enter the Destination: ");
                String destination = sc.nextLine();
                System.out.print("Enter the Time: ");
                String time = sc.nextLine();
                System.out.print("Enter the Status: ");
                String status = sc.nextLine();
                System.out.print("Enter the Price: ");
                int price = sc.nextInt();
                System.out.print("Enter the Normal Seats: ");
                int normalSeats = sc.nextInt();
                System.out.print("Enter the Business Class Seats: ");
                int businessSeats = sc.nextInt();
                System.out.print("Enter the First Class Seats: ");
                int firstClassSeats = sc.nextInt();
                sc.nextLine(); // Consume newline

                System.out.print("Enter the Luggage Weight Limit (optional, press Enter to skip): ");
                String luggageLimit = sc.nextLine();

                // Create a new Flight object and add it to the list
                Flight newFlight = new Flight(id, name, source, destination, time, status, price, normalSeats,
                        businessSeats, firstClassSeats, luggageLimit);

                flights.add(newFlight);

                System.out.println("Flight Added Successfully\n");
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter valid data.\n");
                sc.nextLine(); // Consume the invalid input
            }
        }

        // Method to update flight details.
        public static void update_flights(Scanner sc) {
            if (flights.isEmpty()) {
                System.out.println("No Flights available.\n");
            } else {
                try {
                    System.out.print("\nEnter the Flight ID to update: ");
                    String id = sc.nextLine();
                    System.out.print("Enter the new Flight Name: ");
                    String name = sc.nextLine();
                    System.out.print("Enter the new Source: ");
                    String source = sc.nextLine();
                    System.out.print("Enter the new Destination: ");
                    String destination = sc.nextLine();
                    System.out.print("Enter the new Time: ");
                    String time = sc.nextLine();
                    System.out.print("Enter the new Status: ");
                    String status = sc.nextLine();
                    System.out.print("Enter the new Price: ");
                    int price = sc.nextInt();
                    System.out.print("Enter the new Normal Seats: ");
                    int normalSeats = sc.nextInt();
                    System.out.print("Enter the new Business Class Seats: ");
                    int businessSeats = sc.nextInt();
                    System.out.print("Enter the new First Class Seats: ");
                    int firstClassSeats = sc.nextInt();
                    sc.nextLine(); // Consume newline

                    System.out.print("Enter the new Luggage Weight Limit (optional, press Enter to skip): ");
                    String luggageLimit = sc.nextLine();

                    for (Flight flight : flights) {
                        if (flight.getId().equalsIgnoreCase(id)) {
                            flight.setName(name);
                            flight.setSource(source);
                            flight.setDestination(destination);
                            flight.setTime(time);
                            flight.setStatus(status);
                            flight.setPrice(price);
                            flight.setNormalSeats(normalSeats);
                            flight.setBusinessSeats(businessSeats);
                            flight.setFirstClassSeats(firstClassSeats);

                            if (!luggageLimit.isEmpty()) {
                                flight.setLuggageWeightLimit(luggageLimit);
                            }

                            System.out.println("Flight Updated Successfully\n");
                            return; // Exit the method when the flight is found and updated
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

        // Method to delete a flight.
        public static void delete_flights(Scanner sc) {
            if (flights.isEmpty()) {
                System.out.println("No Flights available.\n");
            } else {
                try {
                    System.out.print("\nEnter the Flight ID to delete: ");
                    String id = sc.nextLine();

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
                System.out.print("\nEnter the Staff Username: ");
                String username = sc.nextLine();
                System.out.print("Enter the Staff Password: ");
                String password = sc.nextLine();

                // Create a new StaffAccount object and add it to the list
                StaffAccount staffAccount = new StaffAccount(username, password);
                staffAccounts.add(staffAccount);

                System.out.println("Staff Account Added Successfully\n");
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter valid data.\n");
                sc.nextLine(); // Consume the invalid input
            }
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
            while (ch != 6) {
                try {
                    // Display customer menu options
                    System.out.println("\nCustomer Menu");
                    System.out.println("1 - Book Flights");
                    System.out.println("2 - Check-In");
                    System.out.println("3 - Flight Details");
                    System.out.println("4 - View Booking History");
                    System.out.println("5 - Manage Reservations");
                    System.out.println("6 - Exit\n");

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
                            System.out.print("\nEnter the Source: ");
                            String source = sc.nextLine();
                            System.out.print("Enter the Destination: ");
                            String destination = sc.nextLine();
                            flight_details(source, destination);
                            break;
                        case 4:
                            viewBookingHistory();
                            break;
                        case 5:
                            manageReservations(sc);
                            break;
                        case 6:
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
                    // Get flight details for check-in
                    System.out.print("\nEnter the Flight ID: ");
                    String id = sc.nextLine();
                    System.out.print("Enter the Flight Name: ");
                    String name = sc.nextLine();
                    System.out.print("Enter the Source: ");
                    String source = sc.nextLine();
                    System.out.print("Enter the Destination: ");
                    String destination = sc.nextLine();

                    for (Flight flight : flights) {
                        if (flight.getId().equalsIgnoreCase(id)) {
                            if (flight.getName().equalsIgnoreCase(name) && flight.getSource().equalsIgnoreCase(source)
                                    && flight.getDestination().equalsIgnoreCase(destination)) {
                                if (flight.getStatus().equalsIgnoreCase("Operational")) {
                                    // Check luggage weight
                                    System.out.print("Enter the Customer's Luggage Weight (in kg): ");
                                    double luggageWeight = sc.nextDouble();

                                    // Get the luggage limit of the specific plane
                                    double luggageLimit = Double.parseDouble(flight.getLuggageWeightLimit());

                                    if (luggageWeight <= luggageLimit) {
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
                    System.out.print("\nEnter the Customer Name: ");
                    String name = sc.nextLine();
                    System.out.print("Enter the Flight Name: ");
                    String flightName = sc.nextLine();
                    System.out.print("Enter the Source: ");
                    String source = sc.nextLine();
                    System.out.print("Enter the Destination: ");
                    String destination = sc.nextLine();
                    System.out.print("Enter the Time: ");
                    String time = sc.nextLine();
                    System.out.print("Enter the Seat Category (Normal or Business or First Class): ");
                    String seatCategory = sc.nextLine();
                    System.out.print("Enter the Check-In Option (Normal or VIP check-in or Private terminal or lounge or Charter and private jet services): ");
                    String checkInOption = sc.nextLine();

                    // Define seat category prices
                    Map<String, Integer> seatCategoryPrices = new HashMap<>();
                    seatCategoryPrices.put("normal class", 1500);
                    seatCategoryPrices.put("business class", 9000);
                    seatCategoryPrices.put("first class", 19000);

                    // Define check-in option prices
                    Map<String, Integer> checkInOptionPrices = new HashMap<>();
                    checkInOptionPrices.put("normal", 0);
                    checkInOptionPrices.put("vip check-in", 5000);
                    checkInOptionPrices.put("private terminal or lounge", 10000);
                    checkInOptionPrices.put("charter and private jet services", 15000);

                    Flight selectedFlight = null;
                    for (Flight flight : flights) {
                        if (flight.getName().equalsIgnoreCase(flightName) && flight.getTime().equalsIgnoreCase(time) && flight.getSource().equalsIgnoreCase(source) && flight.getDestination().equalsIgnoreCase(destination)) {
                            selectedFlight = flight;
                            flightsFound = true; // Set the flag to true if flights are found
                            break;
                        }
                    }

                    if (selectedFlight != null) {
                        if (currentCustomer != null) {
                            tot_price += selectedFlight.getPrice();

                            // Calculate prices based on seat category and check-in option
                            int seatCategoryPrice = seatCategoryPrices.getOrDefault(seatCategory.toLowerCase(), 0);
                            int checkInOptionPrice = checkInOptionPrices.getOrDefault(checkInOption.toLowerCase(), 0);

                            tot_price += seatCategoryPrice;
                            tot_price += checkInOptionPrice;

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
                            } else if (selectedFlight.getFirstClassSeats() > 0) {
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
                    } else {
                        System.out.println("No matching flights found.\n");
                    }
                } catch (InputMismatchException e) {
                    System.out.println("Invalid input. Please enter valid data.\n");
                    sc.nextLine(); // Consume the invalid input
                }
            }
        }

        public static void viewBookingHistory() {
            System.out.println("\nBooking History for Passenger: " + currentCustomer.getId());
            System.out.println("Passenger Name | Flight ID | Flight Name       | Time | Source | Destination | Seat Category | Check-In Option");

            for (Booking booking : bookings) {
                if (booking.getCustomerId().equals(currentCustomer.getId())) {
                    System.out.printf("%-14s | %-9s | %-16s | %-8s | %-6s | %-12s | %-13s | %-15s%n", booking.getCustomerName(), booking.getFlight().getId(), booking.getFlight().getName(), booking.getFlight().getTime(), booking.getFlight().getSource(), booking.getFlight().getDestination(), booking.getSeatCategory(), booking.getCheckInOption());
                }
            }
            System.out.println();
        }

        public static void manageReservations(Scanner sc) {
            if (bookings.isEmpty()) {
                System.out.println("No Bookings available.\n");
            } else {
                try {
                    // Get flight ID and customer name for managing reservations
                    System.out.print("\nEnter the Flight ID to manage reservation: ");
                    String id = sc.nextLine();
                    System.out.print("Enter the Customer Name: ");
                    String customerName = sc.nextLine();

                    for (Booking booking : bookings) {
                        if (booking.getFlight().getId().equalsIgnoreCase(id) && booking.getCustomerName().equalsIgnoreCase(customerName)) {
                            // Display reservation management options
                            System.out.println("1 - Cancel Reservation");
                            System.out.println("2 - Update Seat Category");
                            System.out.println("3 - Exit");
                            System.out.print("Enter your choice: ");
                            int choice = sc.nextInt();
                            sc.nextLine(); // Consume newline

                            switch (choice) {
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

// Flight class represents flight information
class Flight implements Serializable {
    // Fields to store flight information
    private String id;
    private String name;
    private String source;
    private String destination;
    private String time;
    private String status;
    private int price;
    private int normalSeats;
    private int businessSeats;
    private int firstClassSeats;
    private String weightLimit;

    // Constructor to initialize flight information
    public Flight(String id, String name, String source, String destination, String time, String status, int price, int normalSeats, int businessSeats, int firstClassSeats, String weightLimit) {
        this.id = id;
        this.name = name;
        this.source = source;
        this.destination = destination;
        this.time = time;
        this.status = status;
        this.price = price;
        this.normalSeats = normalSeats;
        this.businessSeats = businessSeats;
        this.firstClassSeats = firstClassSeats;
        this.weightLimit = weightLimit;
    }

    // Getters and setters for all fields

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getNormalSeats() {
        return normalSeats;
    }

    public void setNormalSeats(int normalSeats) {
        this.normalSeats = normalSeats;
    }

    public int getBusinessSeats() {
        return businessSeats;
    }

    public void setBusinessSeats(int businessSeats) {
        this.businessSeats = businessSeats;
    }

    public int getFirstClassSeats() {
        return firstClassSeats;
    }

    public void setFirstClassSeats(int firstClassSeats) {
        this.firstClassSeats = firstClassSeats;
    }

    public String getLuggageWeightLimit() {
        return weightLimit;
    }

    public void setLuggageWeightLimit(String weightLimit) {
        this.weightLimit = weightLimit;
    }

    // Check if there are available seats in a given seat category
    public boolean hasAvailableSeat(String seatCategory) {
        switch (seatCategory.toLowerCase()) {
            case "normal class":
                return normalSeats > 0;
            case "business class":
                return businessSeats > 0;
            case "first class":
                return firstClassSeats > 0;
            default:
                return false;
        }
    }

    // Increment the seat count for a given seat category
    public void incrementSeat(String seatCategory) {
        switch (seatCategory.toLowerCase()) {
            case "normal class":
                normalSeats++;
                break;
            case "business class":
                businessSeats++;
                break;
            case "first class":
                firstClassSeats++;
                break;
        }
    }

    // Decrement the seat count for a given seat category
    public void decrementSeat(String seatCategory) {
        switch (seatCategory.toLowerCase()) {
            case "normal class":
                normalSeats--;
                break;
            case "business class":
                businessSeats--;
                break;
            case "first class":
                firstClassSeats--;
                break;
        }
    }
}

// Booking class represents flight bookings made by customers
class Booking implements Serializable {
    private String customerId;
    private String customerName;
    private Flight flight;
    private String seatCategory;
    private String checkInOption;
    private int price;

    // Constructor to initialize booking information
    public Booking(String id, String name, Flight flight, String seatCategory, String checkInOption, int price) {
        this.customerId = id;
        this.customerName = name;
        this.flight = flight;
        this.seatCategory = seatCategory;
        this.checkInOption = checkInOption;
        this.price = price;
    }

    // Getter and setter for customerId
    public String getCustomerId() {
        return customerId;
    }

    // Getter for customerName
    public String getCustomerName() {
        return customerName;
    }

    // Setter for customerName
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    // Getter for flight
    public Flight getFlight() {
        return flight;
    }

    // Setter for flight
    public void setFlight(Flight flight) {
        this.flight = flight;
    }

    // Getter for seatCategory
    public String getSeatCategory() {
        return seatCategory;
    }

    // Setter for seatCategory
    public void setSeatCategory(String seatCategory) {
        this.seatCategory = seatCategory;
    }

    // Getter for checkInOption
    public String getCheckInOption() {
        return checkInOption;
    }

    // Setter for checkInOption
    public void setCheckInOption(String checkInOption) {
        this.checkInOption = checkInOption;
    }

    // Getter for price
    public int getPrice() {
        return price;
    }

    // Setter for price
    public void setPrice(int price) {
        this.price = price;
    }
}

// StaffAccount class represents staff accounts used for authentication
class StaffAccount implements Serializable {
    private static final AtomicInteger staffIdGenerator = new AtomicInteger(1);
    private String id;
    private String username;
    private String password;

    // Constructor to initialize staff account information
    public StaffAccount(String username, String password) {
        this.id = generateId(staffIdGenerator);
        this.username = username;
        this.password = password;
    }

    // Getter for id
    public String getId() {
        return id;
    }

    // Getter for username
    public String getUsername() {
        return username;
    }

    // Setter for username
    public void setUsername(String username) {
        this.username = username;
    }

    // Getter for password
    public String getPassword() {
        return password;
    }

    // Setter for password
    public void setPassword(String password) {
        this.password = password;
    }

    // Generates a unique staff ID based on an atomic counter
    private static String generateId(AtomicInteger generator) {
        int id = generator.getAndIncrement();
        return String.format("stf%04d", id); // Adjust the format to have a fixed length of 7 characters
    }
}

// CustomerAccount class represents customer accounts used for authentication
class CustomerAccount implements Serializable {
    private static final AtomicInteger customerIdGenerator = new AtomicInteger(1);
    private String id;
    private String username;
    private String password;

    // Constructor to initialize customer account information
    public CustomerAccount(String username, String password) {
        this.id = generateId(customerIdGenerator);
        this.username = username;
        this.password = password;
    }

    // Getter for id
    public String getId() {
        return id;
    }

    // Getter for username
    public String getUsername() {
        return username;
    }

    // Setter for username
    public void setUsername(String username) {
        this.username = username;
    }

    // Getter for password
    public String getPassword() {
        return password;
    }

    // Setter for password
    public void setPassword(String password) {
        this.password = password;
    }

    // Generates a unique customer ID based on an atomic counter
    private static String generateId(AtomicInteger generator) {
        int id = generator.getAndIncrement();
        return String.format("cus%04d", id); // Adjust the format to have a fixed length of 7 characters
    }
}

// AdminAccount class represents admin accounts used for authentication
class AdminAccount implements Serializable {
    private static final AtomicInteger adminIdGenerator = new AtomicInteger(1);
    private String id;
    private String username;
    private String password;

    // Constructor to initialize admin account information
    public AdminAccount(String username, String password) {
        this.id = generateId(adminIdGenerator);
        this.username = username;
        this.password = password;
    }

    // Getter for id
    public String getId() {
        return id;
    }

    // Getter for username
    public String getUsername() {
        return username;
    }

    // Getter for password
    public String getPassword() {
        return password;
    }

    // Generates a unique admin ID based on an atomic counter
    private static String generateId(AtomicInteger generator) {
        int id = generator.getAndIncrement();
        return String.format("adm%04d", id); // Adjust the format to have a fixed length of 7 characters
    }
}
